package com.example.babel.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

suspend fun updateBooksAndAuthors() {
    val db = FirebaseFirestore.getInstance()
    val booksSnapshot = db.collection("books").get().await()

    for (doc in booksSnapshot) {
        val bookId = doc.getLong("id") ?: continue
        val authors = doc.get("authors") as? List<String> ?: continue
        val authorIds = authors.map { it.lowercase().replace(" ", "_") }

        // Update the book with the computed authorIds
        db.collection("books")
            .document(doc.id)
            .update("authorIds", authorIds)
            .await()

        // Update or create each author document
        authors.forEachIndexed { index, authorName ->
            val authorId = authorIds[index]
            db.collection("authors")
                .document(authorId)
                .set(
                    mapOf(
                        "id" to authorId,
                        "name" to authorName,
                        "booksWritten" to FieldValue.arrayUnion(bookId)
                    ),
                    SetOptions.merge()
                )
                .await()
        }
    }
}
