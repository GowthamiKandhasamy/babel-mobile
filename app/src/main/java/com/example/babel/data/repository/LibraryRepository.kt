package com.example.babel.data.repository

import com.example.babel.data.models.UserLibrary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class LibraryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val libraries = db.collection("user_libraries")

    suspend fun getUserLibrary(uid: String): UserLibrary? {
        val doc = libraries.document(uid).get().await()
        return if (doc.exists()) doc.toObject(UserLibrary::class.java)
        else UserLibrary(uid)
    }

    suspend fun addBookToShelf(uid: String, bookId: Long, shelf: String) {
        val ref = libraries.document(uid)
        val updateField = when (shelf) {
            "Currently Reading" -> "currentlyReading"
            "Want to Read" -> "wantToRead"
            "Finished Reading" -> "finishedReading"
            else -> throw IllegalArgumentException("Invalid shelf: $shelf")
        }
        ref.update(updateField, FieldValue.arrayUnion(bookId)).await()
    }

    suspend fun removeBookFromShelf(uid: String, bookId: Long, shelf: String) {
        val ref = libraries.document(uid)
        val updateField = when (shelf) {
            "Currently Reading" -> "currentlyReading"
            "Want to Read" -> "wantToRead"
            "Finished Reading" -> "finishedReading"
            else -> throw IllegalArgumentException("Invalid shelf: $shelf")
        }
        ref.update(updateField, FieldValue.arrayRemove(bookId)).await()
    }

    suspend fun ensureLibraryExists(uid: String) {
        val ref = libraries.document(uid)
        val doc = ref.get().await()
        if (!doc.exists()) {
            val data = UserLibrary(uid)
            ref.set(data).await()
        }
    }
}
