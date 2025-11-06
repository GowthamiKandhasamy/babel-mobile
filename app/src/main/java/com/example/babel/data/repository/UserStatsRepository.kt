package com.example.babel.data.repository

import android.util.Log
import com.example.babel.data.models.UserStats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserStatsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val stats = db.collection("user_stats")

    suspend fun updateUserStats(
        userId: String,
        rating: Int,
        bookGenreIds: List<Int>,
        bookAuthorIds: List<String>
    ) {
        val doc = stats.document(userId)
        val existing = doc.get().await().toObject(UserStats::class.java)

        val updated = if (existing != null) {
            val newAvg = ((existing.avgRating * existing.booksRead) + rating) / (existing.booksRead + 1)
            existing.copy(
                booksRead = existing.booksRead + 1,
                avgRating = newAvg,
                favoriteGenres = (existing.favoriteGenres + bookGenreIds).distinct(),
                favoriteAuthors = (existing.favoriteAuthors + bookAuthorIds).distinct()
            )
        } else {
            UserStats(
                userId = userId,
                booksRead = 1,
                avgRating = rating.toDouble(),
                favoriteGenres = bookGenreIds,
                favoriteAuthors = bookAuthorIds
            )
        }

        doc.set(updated).await()
        Log.d("UserStatsRepo", "Updated stats for $userId → avg=${updated.avgRating}")
    }
}
