package com.example.babel.data.repository

import com.example.babel.data.models.UserStats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StatsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getUserStats(uid: String): UserStats? {
        val doc = db.collection("user_stats").document(uid).get().await()
        return doc.toObject(UserStats::class.java)
    }

    suspend fun updateReadingGoal(uid: String, goalPages: Int?, goalBooks: Int?) {
        val data = mutableMapOf<String, Any>()
        goalPages?.let { data["goal_pages"] = it }
        goalBooks?.let { data["goal_books"] = it }
        db.collection("user_stats").document(uid).update(data).await()
    }
}
