package com.example.babel.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babel.data.repository.ExploreRepository
import com.example.babel.repository.LocationWeatherRepository
import com.example.babel.data.models.Book
import com.example.babel.data.models.Journal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ExploreUiState(
    val cityBooks: List<Book> = emptyList(),
    val weatherBooks: List<Book> = emptyList(),
    val editorPicks: List<Book> = emptyList(),
    val publicJournals: List<Journal> = emptyList(),
    val cityMessage: String = "",
    val weatherMessage: String = "",
    val weatherTitle: String = "",
    val isLoading: Boolean = true
)

class ExploreViewModel(
    private val locationRepo: LocationWeatherRepository,
    private val exploreRepo: ExploreRepository,
    private val apiKey: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState

    fun loadExplore(apiKey: String) {
        viewModelScope.launch {
            Log.d("ExploreViewModel", "Loading explore data...")

            try {
                // Get location and weather dynamically
                locationRepo.getLocation { lat, lon, subLocality ->
                    locationRepo.getWeather(lat, lon, apiKey) { condition ->
                        viewModelScope.launch {
                            val cityBooks = exploreRepo.getPopularByCity(subLocality)
                            val weatherBooks = exploreRepo.getWeatherBooks(condition)
                            val editorPicks = exploreRepo.getEditorsPicks()
                            val publicJournals = exploreRepo.getPublicJournals()

                            _uiState.value = ExploreUiState(
                                cityBooks = cityBooks,
                                weatherBooks = weatherBooks,
                                editorPicks = editorPicks,
                                publicJournals = publicJournals,
                                cityMessage = if (cityBooks.isEmpty()) "No city books found" else "",
                                weatherMessage = if (weatherBooks.isEmpty()) "No weather books found" else "",
                                weatherTitle = "Weather Recommendations",
                                isLoading = false
                            )
                            Log.d("ExploreViewModel", "✅ Explore data loaded successfully")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ExploreViewModel", "❌ Error loading explore data", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
