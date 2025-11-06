package com.example.babel.data.repository

import android.util.Log
import com.example.babel.data.models.Book
import com.example.babel.data.models.BookRating
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class BookRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val booksCollection = db.collection("books")
    private val dateFormatter = SimpleDateFormat("yyyy[-MM][-dd]", Locale.getDefault())

    /** 🔧 Fetch all books with type-safe averageRating */
    suspend fun getAllBooks(): List<Book> {
        val snapshot = booksCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            val avg = data["averageRating"]
            val book = doc.toObject(Book::class.java) ?: return@mapNotNull null
            val fixedBook = when (avg) {
                is Long -> book.copy(averageRating = avg.toDouble())
                is Double -> book.copy(averageRating = avg)
                is Float -> book.copy(averageRating = avg.toDouble())
                else -> book
            }
            Log.d("BookRepo", "✅ ${book.id} -> avg=$avg (${avg?.javaClass?.simpleName})")
            fixedBook
        }
    }

    /** Fetch book by numeric ID with type coercion */
    suspend fun getBookById(bookId: Long): Book? {
        val query = booksCollection.whereEqualTo("id", bookId).limit(1).get().await()
        val doc = query.documents.firstOrNull() ?: return null
        val data = doc.data ?: return null
        val avg = data["averageRating"]
        val book = doc.toObject(Book::class.java) ?: return null
        return when (avg) {
            is Long -> book.copy(averageRating = avg.toDouble())
            is Double -> book.copy(averageRating = avg)
            is Float -> book.copy(averageRating = avg.toDouble())
            else -> book
        }
    }

    /** Featured Books — Top 10 by rating */
    suspend fun getFeaturedBooks(): List<Book> {
        val snapshot = booksCollection
            .orderBy("averageRating")
            .limitToLast(50)
            .get()
            .await()

        val books = snapshot.documents.mapNotNull { doc ->
            val data = doc.data
            val avg = data?.get("averageRating")
            val book = doc.toObject(Book::class.java)
            when (avg) {
                is Long -> book?.copy(averageRating = avg.toDouble())
                is Double -> book?.copy(averageRating = avg)
                else -> book
            }
        }
        return books.shuffled(Random(System.currentTimeMillis())).take(10)
    }

    /** New releases: published within 30 days */
    suspend fun getNewReleases(): List<Book> {
        val snapshot = booksCollection.get().await()
        val allBooks = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
        val cutoff = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }.time
        return allBooks.filter { book ->
            book.publishedDate?.let { date ->
                try {
                    val parsed = dateFormatter.parse(date)
                    parsed != null && parsed.after(cutoff)
                } catch (_: Exception) {
                    false
                }
            } ?: false
        }.sortedByDescending { it.publishedDate }
    }

    /** Books by genre */
    suspend fun getBooksByUserGenre(genreId: Int): List<Book> {
        val snapshot = booksCollection
            .whereArrayContains("genreId", genreId)
            .limit(15)
            .get().await()
        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
    }

    /** Other books by same author */
    suspend fun getBooksByAuthor(authorIds: List<String>, excludeBookId: Long): List<Book> {
        if (authorIds.isEmpty()) return emptyList()
        val snapshot = booksCollection
            .whereArrayContainsAny("authorIds", authorIds.map { it.lowercase(Locale.ROOT).trim() })
            .get().await()
        val results = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
        val filtered = results.filter { it.id != excludeBookId }
        Log.d("BookRepo", "📚 Found ${filtered.size} other books by authors=$authorIds")
        return filtered
    }

    /** Similar books by genre ±30 years */
    suspend fun getSimilarBooks(currentBook: Book): List<Book> {
        if (currentBook.genreId.isEmpty()) return emptyList()
        val genreId = currentBook.genreId.first()
        val snapshot = booksCollection.whereArrayContains("genreId", genreId).get().await()
        val all = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
        val year = currentBook.publishedDate?.takeLast(4)?.toIntOrNull()
        return all.filter {
            it.id != currentBook.id &&
                    (it.publishedDate?.takeLast(4)?.toIntOrNull()?.let { y ->
                        year == null || (y in (year - 30)..(year + 30))
                    } ?: false)
        }.sortedByDescending { it.averageRating ?: 0.0 }.take(15)
    }

    /** Rating distribution */
    suspend fun getRatingDistribution(bookId: Long): Map<Int, Int> {
        val snapshot = db.collection("bookRatings").whereEqualTo("bookId", bookId).get().await()
        val all = snapshot.documents.mapNotNull { it.toObject(BookRating::class.java) }
        return (1..5).associateWith { star -> all.count { it.rating == star } }
    }
}
