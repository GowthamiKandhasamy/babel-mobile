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
import org.json.JSONObject
import java.io.InputStream
import kotlin.coroutines.resume

data class LocalityBounds(val latMin: Double, val latMax: Double, val lngMin: Double, val lngMax: Double)

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

    fun getLocation(callback: (lat: Double, lon: Double, subLocality: String) -> Unit) {
        val lat = 13.061
        val lon = 80.238
        val subLocality = matchLocality(lat, lon)
        Log.d("LocationWeatherRepo", "Lat: $lat, Lon: $lon, Locality: $subLocality")
        callback(lat, lon, subLocality)
    }

    fun getWeather(lat: Double, lon: Double, apiKey: String, callback: (condition: String) -> Unit) {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$apiKey"

        val request = StringRequest(Request.Method.GET, url, { response ->
            try {
                val obj = JSONObject(response)
                val weatherArr = obj.getJSONArray("weather")
                val weatherMain = if (weatherArr.length() > 0)
                    weatherArr.getJSONObject(0).getString("main")
                else "Clear"

                Log.d("LocationWeatherRepo", "Weather detected: $weatherMain")
                callback(weatherMain)
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

    private fun matchLocality(lat: Double, lon: Double): String {
        var bestMatch: Pair<String, Double>? = null
        for ((name, bounds) in localities) {
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
            val totalDist = latDist + lonDist
            if (bestMatch == null || totalDist < bestMatch.second) {
                bestMatch = name to totalDist
            }
        }
        return (bestMatch?.first ?: "unknown").lowercase().replace(" ", "_")

    }

    private fun mapDocToBook(bookDoc: com.google.firebase.firestore.DocumentSnapshot): Book {
        return Book(
            id = bookDoc.getLong("id") ?: 0,
            title = bookDoc.getString("title") ?: "",
            subtitle = bookDoc.getString("subtitle") ?: "N/A",
            authors = bookDoc.get("authors") as? List<String> ?: emptyList(),
            authorIds = bookDoc.get("authorIds") as? List<String> ?: emptyList(),
            genreId = (bookDoc.get("genreId") as? List<*>)?.mapNotNull {
                when (it) {
                    is Long -> it.toInt()
                    is Double -> it.toInt()
                    else -> null
                }
            } ?: emptyList(),
            averageRating = bookDoc.getDouble("averageRating") ?: 0.0,
            coverImage = bookDoc.getString("coverImage") ?: "",
            smallThumbnail = bookDoc.getString("smallThumbnail") ?: "",
            isbn10 = bookDoc.getString("isbn10") ?: "",
            isbn13 = bookDoc.getString("isbn13") ?: "",
            language = bookDoc.getString("language") ?: "",
            maturityRating = bookDoc.getString("maturityRating") ?: "",
            pageCount = bookDoc.getLong("pageCount")?.toInt() ?: 0,
            printType = bookDoc.getString("printType") ?: "",
            publishedDate = bookDoc.getString("publishedDate") ?: ""
        )
    }

    // ------------------ City Books ------------------
    fun fetchCityBooks(city: String, callback: (List<Book>) -> Unit) {
        val firestoreCity = if (city.startsWith("Chennai -")) city else "Chennai - $city"

        db.collection("locationRecommendation")
            .whereEqualTo("city", firestoreCity)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d("LocationWeatherRepo", "No city books found for $firestoreCity, using fallback Guindy IDs")
                    // Hardcoded fallback IDs for Guindy
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
                val fallbackBookIds = listOf(19L, 20L, 35L, 37, 70L, 71L)
                fetchBooksByIds(fallbackBookIds, callback)
            }
    }

    // ------------------ Weather Books ------------------
    fun fetchWeatherBooks(condition: String, callback: (List<Book>) -> Unit) {
        val normalizedCondition = condition.replaceFirstChar { it.uppercaseChar() }

        db.collection("weatherRecommendation")
            .whereEqualTo("condition", normalizedCondition)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d("LocationWeatherRepo", "No weather books found for $normalizedCondition, using fallback Clouds IDs")
                    // Hardcoded fallback IDs for Clouds
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

    // ------------------ Helper to fetch Book objects ------------------
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
                .addOnFailureListener { count++; if (count == bookIds.size) callback(books) }
        }
    }

    // ------------------ Suspend versions ------------------
    suspend fun fetchCityBooksSuspend(city: String): List<Book> =
        suspendCancellableCoroutine { cont -> fetchCityBooks(city) { cont.resume(it) } }

    suspend fun fetchWeatherBooksSuspend(condition: String): List<Book> =
        suspendCancellableCoroutine { cont -> fetchWeatherBooks(condition) { cont.resume(it) } }
}
