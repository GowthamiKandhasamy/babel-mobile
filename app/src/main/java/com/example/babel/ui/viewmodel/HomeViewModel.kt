package com.example.babel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babel.data.models.Book
import com.example.babel.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val featured: List<Book> = emptyList(),
    val newReleases: List<Book> = emptyList(),
    val recommended: List<Book> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val repo: BookRepository = BookRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadHomeData(userGenre: Int?) {
        viewModelScope.launch {
            try {
                val featured = repo.getFeaturedBooks()
                val newReleases = repo.getNewReleases()
                val recommended = userGenre?.let { repo.getBooksByUserGenre(it) } ?: emptyList()
                _uiState.value = HomeUiState(
                    featured = featured,
                    newReleases = newReleases,
                    recommended = recommended,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(error = e.message, isLoading = false)
            }
        }
    }
}

