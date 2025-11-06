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

    // ---------------- SIGN UP ----------------
    suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User not created"))

            val newUser = User(
                uid = firebaseUser.uid,
                email = email,
                name = "",
                photoUrl = "",
                label = "New Reader",
                createdAt = System.currentTimeMillis()
            )

            // Create user profile in Firestore
            createFirestoreUser(newUser)

            // Initialize stats document
            createUserStats(firebaseUser.uid)

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- SIGN IN ----------------
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User not found"))

            val docRef = db.collection("users").document(firebaseUser.uid)
            val snapshot = docRef.get().await()

            val user = if (snapshot.exists()) {
                snapshot.toObject(User::class.java)
            } else {
                // If user document doesn’t exist (edge case), create it
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    name = "",
                    photoUrl = "",
                    label = "New Reader",
                    createdAt = System.currentTimeMillis()
                )
                createFirestoreUser(newUser)
                newUser
            }

            if (user != null) Result.success(user)
            else Result.failure(Exception("No user data found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- SIGN OUT ----------------
    fun signOut() {
        auth.signOut()
    }
    // ---------------- LOGOUT WITH RESULT ----------------
    suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- CURRENT USER ----------------
    fun currentUser(): User? {
        val u = auth.currentUser ?: return null
        return User(uid = u.uid, email = u.email ?: "")
    }

    // ---------------- PRIVATE HELPERS ----------------

    private suspend fun createFirestoreUser(user: User) {
        val userData = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "name" to user.name,
            "photo_url" to user.photoUrl,
            "label" to user.label,
            "created_at" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(user.uid).set(userData).await()
    }

    private suspend fun createUserStats(uid: String) {
        val statsData = hashMapOf(
            "books_read" to 0,
            "avg_rating" to 0.0,
            "favorite_genres" to listOf<String>(),
            "favorite_authors" to listOf<String>()
        )
        db.collection("user_stats").document(uid).set(statsData).await()
    }
}
