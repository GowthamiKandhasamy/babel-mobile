package com.example.babel.data.repository

import android.util.Log
import com.example.babel.data.models.BookRating
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RatingRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val ratings = db.collection("bookRatings")
    private val books = db.collection("books")

    suspend fun addOrUpdateRating(rating: BookRating) {
        val existing = ratings
            .whereEqualTo("userId", rating.userId)
            .whereEqualTo("bookId", rating.bookId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()

        if (existing != null) {
            existing.reference.update(
                mapOf(
                    "rating" to rating.rating,
                    "comment" to rating.comment,
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()
        } else {
            ratings.add(rating).await()
        }

        // 🔧 Recompute average
        val allRatings = ratings
            .whereEqualTo("bookId", rating.bookId)
            .get()
            .await()
            .mapNotNull { it.toObject(BookRating::class.java).rating }

        val avg = allRatings.average()
        val counts = (1..5).associateWith { star -> allRatings.count { it == star } }

        val query = books.whereEqualTo("id", rating.bookId).limit(1).get().await()
        val bookDoc = query.documents.firstOrNull()
        bookDoc?.reference?.update(
            mapOf(
                "averageRating" to avg,
                "ratingCounts" to counts
            )
        )?.await()

        Log.d("RatingRepo", "✅ Updated avg=$avg counts=$counts for bookId=${rating.bookId}")
    }
}
