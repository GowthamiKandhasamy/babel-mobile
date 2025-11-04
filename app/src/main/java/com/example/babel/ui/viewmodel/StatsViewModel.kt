package com.example.babel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babel.data.models.UserStats
import com.example.babel.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StatsUiState(
    val stats: UserStats? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class StatsViewModel(
    private val repo: StatsRepository = StatsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState

    fun loadStats(uid: String) {
        viewModelScope.launch {
            try {
                val data = repo.getUserStats(uid)
                _uiState.value = StatsUiState(stats = data, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = StatsUiState(error = e.message, isLoading = false)
            }
        }
    }

    fun updateGoal(uid: String, pages: Int?, books: Int?) {
        viewModelScope.launch {
            repo.updateReadingGoal(uid, pages, books)
            loadStats(uid)
        }
    }
}
