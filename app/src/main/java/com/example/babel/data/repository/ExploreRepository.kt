package com.example.babel.data.repository

import android.util.Log
import com.example.babel.data.models.Book
import com.example.babel.data.models.Journal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExploreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val TAG = "ExploreRepo"

    private fun normalizeKey(key: String): String {
        return key.trim().lowercase().replace("chennai - ", "").replace(" ", "_")
    }

    private fun parseBookIds(rawList: List<*>?): List<Int> {
        Log.d(TAG, "parseBookIds() raw input: $rawList")
        return rawList?.mapNotNull {
            when (it) {
                is Long -> it.toInt()
                is Double -> it.toInt()
                is String -> it.toIntOrNull()
                else -> {
                    Log.w(TAG, "⚠️ Unexpected type in bookIds list: ${it?.javaClass?.simpleName}")
                    null
                }
            }
        } ?: emptyList()
    }

    /** Fetch books recommended for a city (normalized) */
    suspend fun getPopularByCity(city: String): List<Book> {
        val normalizedCity = normalizeKey(city)
        Log.d(TAG, "---- getPopularByCity('$normalizedCity') called ----")

        return try {
            val cityDocRef = db.collection("locationRecommendations").document(normalizedCity)
            Log.d(TAG, "Fetching Firestore document: ${cityDocRef.path}")
            val cityDoc = cityDocRef.get().await()

            if (!cityDoc.exists()) {
                Log.w(TAG, "❌ No Firestore document found for city '$normalizedCity'")
                return emptyList()
            }

            val bookIds = parseBookIds(cityDoc.get("bookIds") as? List<*>)
            Log.d(TAG, "City '$normalizedCity' returned book IDs: $bookIds (count=${bookIds.size})")

            val books = mutableListOf<Book>()
            for (id in bookIds) {
                val doc = db.collection("books").document(id.toString()).get().await()
                doc.toObject(Book::class.java)?.let {
                    books.add(it)
                    Log.d(TAG, "✅ Added city book: ${it.title}")
                }
            }

            Log.d(TAG, "Returning ${books.size} books for '$normalizedCity'")
            books
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching city books for '$normalizedCity'", e)
            emptyList()
        }
    }

    /** Fetch books based on weather condition (normalized) */
    suspend fun getWeatherBooks(condition: String): List<Book> {
        val normalizedWeather = normalizeKey(condition)
        Log.d(TAG, "---- getWeatherBooks('$normalizedWeather') called ----")

        return try {
            val weatherDocRef = db.collection("weatherRecommendations").document(normalizedWeather)
            Log.d(TAG, "Fetching Firestore document: ${weatherDocRef.path}")
            val weatherDoc = weatherDocRef.get().await()

            if (!weatherDoc.exists()) {
                Log.w(TAG, "❌ No Firestore document found for weather '$normalizedWeather'")
                return emptyList()
            }

            val bookIds = parseBookIds(weatherDoc.get("bookIds") as? List<*>)
            Log.d(TAG, "Weather '$normalizedWeather' returned book IDs: $bookIds (count=${bookIds.size})")

            val books = mutableListOf<Book>()
            for (id in bookIds) {
                val doc = db.collection("books").document(id.toString()).get().await()
                doc.toObject(Book::class.java)?.let {
                    books.add(it)
                    Log.d(TAG, "✅ Added weather book: ${it.title}")
                }
            }

            Log.d(TAG, "Returning ${books.size} weather books for '$normalizedWeather'")
            books
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching weather books for '$normalizedWeather'", e)
            emptyList()
        }
    }

    /** Fetch editor picks */
    suspend fun getEditorsPicks(): List<Book> {
        Log.d(TAG, "Fetching editor picks...")
        return try {
            val snapshot = db.collection("editor_picks").get().await()
            val result = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
            Log.d(TAG, "✅ Loaded ${result.size} editor picks")
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching editor picks", e)
            emptyList()
        }
    }

    /** Fetch top public journals */
    suspend fun getPublicJournals(): List<Journal> {
        Log.d(TAG, "Fetching public journals...")
        return try {
            val snapshot = db.collection("journals")
                .whereEqualTo("visibility", "public")
                .orderBy("likes")
                .limit(10)
                .get()
                .await()
            val result = snapshot.documents.mapNotNull { it.toObject(Journal::class.java) }
            Log.d(TAG, "✅ Loaded ${result.size} public journals")
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching journals", e)
            emptyList()
        }
    }
}
