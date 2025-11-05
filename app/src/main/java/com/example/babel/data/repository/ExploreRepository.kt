package com.example.babel.data.repository

import com.example.babel.data.models.Book
import com.example.babel.data.models.Journal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExploreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getPopularByCity(city: String): List<Book> {
        val cityDoc = db.collection("popular_by_city").document(city).get().await()
        val bookIds = cityDoc.get("bookIds") as? List<String> ?: emptyList()

        if (bookIds.isEmpty()) return emptyList() // ✅ prevent crash

        val snapshot = db.collection("books")
            .whereIn("id", bookIds)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
    }

    suspend fun getWeatherBooks(condition: String): List<Book> {
        val weatherDoc = db.collection("weather_books").document(condition).get().await()
        val ids = weatherDoc.get("bookIds") as? List<String> ?: emptyList()

        if (ids.isEmpty()) return emptyList() // ✅ prevent crash

        val snapshot = db.collection("books")
            .whereIn("id", ids)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
    }

    suspend fun getEditorsPicks(): List<Book> {
        val snapshot = db.collection("editor_picks").get().await()
        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
    }

    suspend fun getPublicJournals(): List<Journal> {
        val snapshot = db.collection("journals")
            .whereEqualTo("visibility", "public")
            .orderBy("likes")
            .limit(10)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Journal::class.java) }
    }
}
