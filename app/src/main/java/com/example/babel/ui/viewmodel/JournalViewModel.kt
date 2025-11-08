package com.example.babel.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babel.data.models.Journal
import com.example.babel.data.repository.JournalRepository
import com.example.babel.data.repository.LocalJournalRepository
import com.example.babel.ui.screens.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class JournalUiState(
    val journals: List<Journal> = emptyList(),
    val localJournals: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class JournalViewModel(
    private val repo: JournalRepository = JournalRepository()
) : ViewModel() {

    private var localRepo: LocalJournalRepository? = null

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState


    fun initLocal(context: Context) {
        localRepo = LocalJournalRepository(context)
        loadLocalJournals()
    }


    fun loadUserJournals(uid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val data = repo.getUserJournals(uid)
                _uiState.value = _uiState.value.copy(
                    journals = data,
                    isLoading = false,
                    error = null
                )
                Log.d("JournalVM", "✅ Loaded ${data.size} journals for user $uid")
            } catch (e: Exception) {
                Log.e("JournalVM", "❌ Failed to load journals: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load journals",
                    isLoading = false
                )
            }
        }
    }

    // ✅ Add a new journal (Firestore)
    fun addJournal(uid: String, content: String, visibility: String) {
        viewModelScope.launch {
            try {
                Log.d("JournalVM", "🟡 Adding new journal for $uid...")
                repo.addJournal(Journal(ownerId = uid, content = content, visibility = visibility))
                Log.d("JournalVM", "✅ Journal added successfully")
                loadUserJournals(uid)
            } catch (e: Exception) {
                Log.e("JournalVM", "❌ Failed to add journal: ${e.message}", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    // ✅ Edit a journal
    fun editJournal(id: String, content: String, visibility: String, uid: String) {
        viewModelScope.launch {
            try {
                repo.editJournal(id, content, visibility)
                Log.d("JournalVM", "✅ Journal updated: $id")
                loadUserJournals(uid)
            } catch (e: Exception) {
                Log.e("JournalVM", "❌ Failed to update journal: ${e.message}", e)
            }
        }
    }

    // ✅ Delete a journal
    fun deleteJournal(id: String, uid: String) {
        viewModelScope.launch {
            try {
                repo.deleteJournal(id)
                Log.d("JournalVM", "✅ Journal deleted: $id")
                loadUserJournals(uid)
            } catch (e: Exception) {
                Log.e("JournalVM", "❌ Failed to delete journal: ${e.message}", e)
            }
        }
    }

    // ✅ Local operations
    fun loadLocalJournals() {
        _uiState.value = _uiState.value.copy(
            localJournals = localRepo?.getLocalJournals() ?: emptyList()
        )
    }

    fun addLocalJournal(entry: JournalEntry) {
        localRepo?.addLocalJournal(entry)
        loadLocalJournals()
    }

    fun editLocalJournal(entry: JournalEntry) {
        localRepo?.editLocalJournal(entry)
        loadLocalJournals()
    }

    fun deleteLocalJournal(id: String) {
        localRepo?.deleteLocalJournal(id)
        loadLocalJournals()
    }
}
