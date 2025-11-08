package com.example.babel.repository

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.babel.data.models.Book
import com.example.babel.data.models.LocationRecommendation
import com.example.babel.data.models.WeatherRecommendation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.math.*

data class LocalityBounds(
    val latMin: Double,
    val latMax: Double,
    val lngMin: Double,
    val lngMax: Double
) {
    val centerLat: Double get() = (latMin + latMax) / 2
    val centerLng: Double get() = (lngMin + lngMax) / 2
}

class LocationWeatherRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()

    private val localities: Map<String, LocalityBounds> by lazy {
        val inputStream: InputStream = context.assets.open("Location_bounding_boxes.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(json)
        val map = mutableMapOf<String, LocalityBounds>()
        obj.keys().forEach { key ->
            val bound = obj.getJSONObject(key)
            map[key] = LocalityBounds(
                latMin = bound.getDouble("lat_min"),
                latMax = bound.getDouble("lat_max"),
                lngMin = bound.getDouble("lng_min"),
                lngMax = bound.getDouble("lng_max")
            )
        }
        map
    }

    // ------------------ LOCATION DETECTION ------------------
    fun getLocation(callback: (lat: Double, lon: Double, subLocality: String) -> Unit) {
        // Normally from location provider; for testing, fixed coordinate near Red Hills
        val lat = 13.061
        val lon = 80.238
        val subLocality = matchLocality(lat, lon)
        Log.d("LocationWeatherRepo", "Lat: $lat, Lon: $lon, Locality: $subLocality")
        callback(lat, lon, subLocality)
    }

    private fun matchLocality(lat: Double, lon: Double): String {
        // Step 1: If inside bounding box, that’s the locality
        localities.forEach { (name, bounds) ->
            if (lat in bounds.latMin..bounds.latMax && lon in bounds.lngMin..bounds.lngMax) {
                Log.d("LocationWeatherRepo", "Matched directly inside bounds of $name")
                return name.lowercase().replace(" ", "_")
            }
        }

        // Step 2: Otherwise, compute a weighted proximity to each bounding box
        var bestMatch = "unknown"
        var bestScore = Double.MIN_VALUE

        for ((name, bounds) in localities) {
            // Distance to the center of the area
            val centerDist = haversine(lat, lon, bounds.centerLat, bounds.centerLng)
            // Distance to the nearest boundary (if outside)
            val latDist = when {
                lat < bounds.latMin -> bounds.latMin - lat
                lat > bounds.latMax -> lat - bounds.latMax
                else -> 0.0
            }
            val lonDist = when {
                lon < bounds.lngMin -> bounds.lngMin - lon
                lon > bounds.lngMax -> lon - bounds.lngMax
                else -> 0.0
            }
            val edgeDist = sqrt(latDist.pow(2) + lonDist.pow(2))

            // Combine center distance and edge proximity
            val proximityScore = 1 / (centerDist + edgeDist + 0.001) // higher = closer

            if (proximityScore > bestScore) {
                bestScore = proximityScore
                bestMatch = name
            }
        }

        Log.d("LocationWeatherRepo", "Closest locality: $bestMatch (score=%.5f)".format(bestScore))
        return bestMatch.lowercase().replace(" ", "_")
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return 2 * R * asin(sqrt(a))
    }

    // ------------------ WEATHER DETECTION ------------------
    fun getWeather(lat: Double, lon: Double, apiKey: String, callback: (condition: String) -> Unit) {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$apiKey"

        val request = StringRequest(Request.Method.GET, url, { response ->
            try {
                val obj = JSONObject(response)
                val main = obj.getJSONObject("main")
                val wind = obj.getJSONObject("wind")
                val clouds = obj.optJSONObject("clouds")

                val weatherMain = obj.getJSONArray("weather").getJSONObject(0).getString("main")
                val temp = main.getDouble("temp")
                val humidity = main.getDouble("humidity")
                val windSpeed = wind.getDouble("speed")
                val cloudiness = clouds?.optDouble("all") ?: 0.0

                val condition = classifyWeather(weatherMain, temp, humidity, windSpeed, cloudiness)
                Log.d(
                    "LocationWeatherRepo",
                    "Weather detected: $condition ($weatherMain, $temp°C, hum=$humidity%, wind=$windSpeed, clouds=$cloudiness)"
                )
                callback(condition)
            } catch (e: Exception) {
                e.printStackTrace()
                callback("Unknown")
            }
        }, { error ->
            error.printStackTrace()
            callback("Unknown")
        })

        Volley.newRequestQueue(context).add(request)
    }

    private fun classifyWeather(
        main: String,
        temp: Double,
        humidity: Double,
        wind: Double,
        cloudiness: Double
    ): String {
        val inputStream: InputStream = context.assets.open("weather_condition_criteria.json")
        val jsonArray = JSONArray(inputStream.bufferedReader().use { it.readText() })

        var bestMatch = "Unknown"
        var bestScore = 0.0

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val condition = obj.getString("condition")
            val criteria = obj.getJSONObject("criteria")

            var score = 0.0

            // Match main
            if (criteria.has("weather_main")) {
                val mains = criteria.getJSONArray("weather_main")
                val matches = (0 until mains.length()).any {
                    mains.getString(it).equals(main, ignoreCase = true)
                }
                if (matches) score += 2.0 else continue
            }

            fun inRange(value: Double, min: Double, max: Double) = value in min..max

            val tempMin = criteria.optDouble("temp_min", Double.NEGATIVE_INFINITY)
            val tempMax = criteria.optDouble("temp_max", Double.POSITIVE_INFINITY)
            val humidityMin = criteria.optDouble("humidity_min", Double.NEGATIVE_INFINITY)
            val humidityMax = criteria.optDouble("humidity_max", Double.POSITIVE_INFINITY)
            val windMin = criteria.optDouble("wind_min", Double.NEGATIVE_INFINITY)
            val windMax = criteria.optDouble("wind_max", Double.POSITIVE_INFINITY)
            val cloudMin = criteria.optDouble("cloudiness_min", Double.NEGATIVE_INFINITY)
            val cloudMax = criteria.optDouble("cloudiness_max", Double.POSITIVE_INFINITY)

            if (inRange(temp, tempMin, tempMax)) score += 1
            if (inRange(humidity, humidityMin, humidityMax)) score += 1
            if (inRange(wind, windMin, windMax)) score += 1
            if (inRange(cloudiness, cloudMin, cloudMax)) score += 1

            if (score > bestScore) {
                bestScore = score
                bestMatch = condition
            }
        }

        return bestMatch
    }

    // ------------------ FIRESTORE FETCH ------------------
    fun fetchCityBooks(city: String, callback: (List<Book>) -> Unit) {
        val firestoreCity = if (city.startsWith("Chennai -")) city else "Chennai - $city"

        db.collection("locationRecommendation")
            .whereEqualTo("city", firestoreCity)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d("LocationWeatherRepo", "No city books found for $firestoreCity, fallback used")
                    val fallbackBookIds = listOf(19L, 20L, 35L, 37L, 70L, 71L)
                    fetchBooksByIds(fallbackBookIds, callback)
                    return@addOnSuccessListener
                }

                val doc = snapshot.documents[0].toObject(LocationRecommendation::class.java)
                val bookIds = doc?.bookIds ?: emptyList()
                fetchBooksByIds(bookIds, callback)
            }
            .addOnFailureListener {
                Log.e("LocationWeatherRepo", "Failed to fetch city books", it)
                val fallbackBookIds = listOf(19L, 20L, 35L, 37L, 70L, 71L)
                fetchBooksByIds(fallbackBookIds, callback)
            }
    }

    fun fetchWeatherBooks(condition: String, callback: (List<Book>) -> Unit) {
        val normalizedCondition = condition.replaceFirstChar { it.uppercaseChar() }

        db.collection("weatherRecommendation")
            .whereEqualTo("condition", normalizedCondition)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d("LocationWeatherRepo", "No weather books found for $normalizedCondition, fallback used")
                    val fallbackBookIds = listOf(6L, 20L, 21L, 22L, 68L, 74L, 70L, 71L)
                    fetchBooksByIds(fallbackBookIds, callback)
                    return@addOnSuccessListener
                }

                val doc = snapshot.documents[0].toObject(WeatherRecommendation::class.java)
                val bookIds = doc?.bookIds ?: emptyList()
                fetchBooksByIds(bookIds, callback)
            }
            .addOnFailureListener {
                Log.e("LocationWeatherRepo", "Failed to fetch weather books", it)
                val fallbackBookIds = listOf(6L, 20L, 21L, 22L, 68L, 74L, 70L, 71L)
                fetchBooksByIds(fallbackBookIds, callback)
            }
    }

    private fun fetchBooksByIds(bookIds: List<Long>, callback: (List<Book>) -> Unit) {
        if (bookIds.isEmpty()) {
            callback(emptyList())
            return
        }

        val books = mutableListOf<Book>()
        var count = 0
        bookIds.forEach { id ->
            db.collection("books").document(id.toString()).get()
                .addOnSuccessListener { doc ->
                    doc.toObject(Book::class.java)?.let { books.add(it) }
                    count++
                    if (count == bookIds.size) callback(books)
                }
                .addOnFailureListener {
                    count++
                    if (count == bookIds.size) callback(books)
                }
        }
    }

    // ------------------ SUSPEND VERSIONS ------------------
    suspend fun fetchCityBooksSuspend(city: String): List<Book> =
        suspendCancellableCoroutine { cont ->
            fetchCityBooks(city) { cont.resume(it) }
        }

    suspend fun fetchWeatherBooksSuspend(condition: String): List<Book> =
        suspendCancellableCoroutine { cont ->
            fetchWeatherBooks(condition) { cont.resume(it) }
        }
}
