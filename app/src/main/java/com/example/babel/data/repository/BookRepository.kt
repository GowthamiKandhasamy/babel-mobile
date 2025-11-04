package com.example.babel.data.repository

import com.example.babel.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val booksCollection = db.collection("books")

    suspend fun getAllBooks(): List<Book> {
        val snapshot = booksCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
    }

    suspend fun getBookById(bookId: String): Book? {
        val doc = booksCollection.document(bookId).get().await()
        return doc.toObject(Book::class.java)
    }

    suspend fun getBooksByGenre(genreId: Int): List<Book> {
        val snapshot = booksCollection
            .whereArrayContains("genre_id", genreId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
    }
}

suspend fun getNewReleases(): List<Book> {
    val snapshot = db.collection("books")
        .whereGreaterThanOrEqualTo("publishedDate", "2022")
        .get().await()
    return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
}

suspend fun getFeaturedBooks(): List<Book> {
    val snapshot = db.collection("books")
        .whereEqualTo("featured", true)
        .get().await()
    return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
}

suspend fun getBooksByUserGenre(genreId: Int): List<Book> {
    val snapshot = db.collection("books")
        .whereArrayContains("genre_id", genreId)
        .get().await()
    return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
}

