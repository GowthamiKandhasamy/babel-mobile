package com.example.babel.data.repository

import android.util.Log
import com.example.babel.data.models.UserLibrary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserLibraryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val libs = db.collection("user_library")

    suspend fun markAsFinished(userId: String, bookId: Long) {
        val doc = libs.document(userId)
        val library = doc.get().await().toObject(UserLibrary::class.java)
        val updated = if (library != null) {
            if (bookId !in library.finishedReading) {
                library.copy(
                    finishedReading = library.finishedReading + bookId,
                    currentlyReading = library.currentlyReading - bookId,
                    wantToRead = library.wantToRead - bookId
                )
            } else library
        } else {
            UserLibrary(userId = userId, finishedReading = listOf(bookId))
        }

        doc.set(updated).await()
        Log.d("UserLibraryRepo", "Marked bookId=$bookId finished for user=$userId")
    }
}
