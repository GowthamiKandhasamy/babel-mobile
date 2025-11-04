package com.example.babel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babel.data.models.Book
import com.example.babel.data.models.Journal
import com.example.babel.data.repository.ExploreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ExploreUiState(
    val cityBooks: List<Book> = emptyList(),
    val weatherBooks: List<Book> = emptyList(),
    val editorPicks: List<Book> = emptyList(),
    val publicJournals: List<Journal> = emptyList(),
    val isLoading: Boolean = true
)

class ExploreViewModel(
    private val repo: ExploreRepository = ExploreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState

    fun loadExplore(city: String, weather: String) {
        viewModelScope.launch {
            val cityBooks = repo.getPopularByCity(city)
            val weatherBooks = repo.getWeatherBooks(weather)
            val editors = repo.getEditorsPicks()
            val journals = repo.getPublicJournals()

            _uiState.value = ExploreUiState(
                cityBooks = cityBooks,
                weatherBooks = weatherBooks,
                editorPicks = editors,
                publicJournals = journals,
                isLoading = false
            )
        }
    }
}
