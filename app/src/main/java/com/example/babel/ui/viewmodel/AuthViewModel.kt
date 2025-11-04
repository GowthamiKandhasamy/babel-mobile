package com.example.babel.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.babel.data.models.User
import com.example.babel.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val isLogin: Boolean = true
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(isLogin = !_uiState.value.isLogin)
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return repository.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String): Result<User> {
        return repository.signUp(email, password)
    }

    fun signOut() = repository.signOut()
}
