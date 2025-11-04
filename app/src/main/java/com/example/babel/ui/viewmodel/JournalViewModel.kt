package com.example.babel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babel.data.models.Journal
import com.example.babel.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class JournalUiState(
    val journals: List<Journal> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class JournalViewModel(
    private val repo: JournalRepository = JournalRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState

    fun loadUserJournals(uid: String) {
        viewModelScope.launch {
            try {
                val data = repo.getUserJournals(uid)
                _uiState.value = JournalUiState(journals = data, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = JournalUiState(error = e.message, isLoading = false)
            }
        }
    }

    fun addJournal(uid: String, content: String, visibility: String) {
        viewModelScope.launch {
            repo.addJournal(Journal(ownerId = uid, content = content, visibility = visibility))
            loadUserJournals(uid)
        }
    }

    fun editJournal(id: String, content: String, visibility: String, uid: String) {
        viewModelScope.launch {
            repo.editJournal(id, content, visibility)
            loadUserJournals(uid)
        }
    }

    fun deleteJournal(id: String, uid: String) {
        viewModelScope.launch {
            repo.deleteJournal(id)
            loadUserJournals(uid)
        }
    }
}
