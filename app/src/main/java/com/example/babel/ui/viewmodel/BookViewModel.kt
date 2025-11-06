package com.example.babel.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babel.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())
    private val _filteredBooks = MutableStateFlow<List<Book>>(emptyList())
    val bookList: StateFlow<List<Book>> = _filteredBooks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadAllBooks()
    }

    fun loadAllBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("books").get().await()
                val books = snapshot.toObjects(Book::class.java)
                println("✅ Loaded ${books.size} books from Firestore")
                _allBooks.value = books
                _filteredBooks.value = books.toList() // create new reference
            } catch (e: Exception) {
                println("❌ Firestore error: ${e.localizedMessage}")
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchBooks(query: String) {
        val normalized = query.trim().lowercase()
        println("🔍 Searching for \"$normalized\"")

        val filtered = if (normalized.isEmpty()) {
            _allBooks.value
        } else {
            _allBooks.value.filter { book ->
                book.title.lowercase().contains(normalized) ||
                        book.authors.any { it.lowercase().contains(normalized) }
            }
        }

        println("🎯 Found ${filtered.size} results for \"$normalized\"")

        // Update with new list reference
        _filteredBooks.value = filtered.toList()
    }
}
