package com.example.babel.data.repository

import com.example.babel.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("User not created"))

            val newUser = User(
                uid = user.uid,
                email = email,
                createdAt = System.currentTimeMillis()
            )

            db.collection("users").document(user.uid).set(
                hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "created_at" to FieldValue.serverTimestamp(),
                    "name" to "",
                    "photo_url" to "",
                    "label" to "New Reader"
                )
            ).await()

            db.collection("user_stats").document(user.uid).set(
                hashMapOf(
                    "books_read" to 0,
                    "avg_rating" to 0.0,
                    "favorite_genres" to listOf<String>(),
                    "favorite_authors" to listOf<String>()
                )
            ).await()

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("User not found"))

            val snapshot = db.collection("users").document(user.uid).get().await()
            val data = snapshot.toObject(User::class.java)

            if (data != null) Result.success(data)
            else Result.failure(Exception("No user data found"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun currentUser(): User? {
        val u = auth.currentUser ?: return null
        return User(uid = u.uid, email = u.email ?: "")
    }
}
