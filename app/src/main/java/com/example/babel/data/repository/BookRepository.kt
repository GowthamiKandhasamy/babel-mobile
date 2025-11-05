package com.example.babel.data.repository

import com.example.babel.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random
import kotlin.text.get

class BookRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val booksCollection = db.collection("books")
    val dateFormatter = SimpleDateFormat("yyyy[-MM][-dd]", Locale.getDefault())

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

    /**
     * Featured books: Top 10 highest-rated, randomized for each session.
     */
    suspend fun getFeaturedBooks(): List<Book> {
        val snapshot = db.collection("books")
            .orderBy("averageRating") // your Firestore field for rating
            .limitToLast(50)          // pull top 50, we’ll shuffle locally
            .get()
            .await()

        val books = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
        return books.shuffled(Random(System.currentTimeMillis())).take(10)
    }

    /**
     * New releases: Books published within last 30 days.
     */
    suspend fun getNewReleases(): List<Book> {
        val snapshot = db.collection("books").get().await()
        val allBooks = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }

        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }
        val cutoffDate = calendar.time

        // Filter by publication date >= cutoff
        return allBooks.filter { book ->
            book.publishedDate?.let { dateStr ->
                try {
                    val pubDate = dateFormatter.parse(dateStr)
                    pubDate != null && pubDate.after(cutoffDate)
                } catch (_: Exception) {
                    false
                }
            } ?: false
        }.sortedByDescending { it.publishedDate } // recent first
    }

    /**
     * Recommended books by user’s preferred genre.
     */
    suspend fun getBooksByUserGenre(genreId: Int): List<Book> {
        val snapshot = db.collection("books")
            .whereArrayContains("genre_id", genreId)
            .limit(15)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
    }
}

