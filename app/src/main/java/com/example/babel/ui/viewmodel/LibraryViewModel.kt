package com.example.babel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babel.data.models.Book
import com.example.babel.data.models.UserLibrary
import com.example.babel.data.repository.BookRepository
import com.example.babel.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LibraryUiState(
    val library: UserLibrary? = null,
    val allBooks: List<Book> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class LibraryViewModel(
    private val libraryRepo: LibraryRepository = LibraryRepository(),
    private val bookRepo: BookRepository = BookRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState

    fun loadLibrary(uid: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val library = libraryRepo.getUserLibrary(uid)
                val allBooks = bookRepo.getAllBooks()
                _uiState.value = LibraryUiState(
                    library = library,
                    allBooks = allBooks,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = LibraryUiState(error = e.message, isLoading = false)
            }
        }
    }

    fun addBook(uid: String, bookId: String, shelf: String) {
        viewModelScope.launch {
            libraryRepo.addBookToShelf(uid, bookId, shelf)
            loadLibrary(uid)
        }
    }
}
