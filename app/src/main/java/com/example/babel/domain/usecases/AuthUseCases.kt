package com.example.babel.domain.usecases

import com.example.babel.data.models.User
import com.example.babel.data.repository.AuthRepository

class AuthUseCases(private val repository: AuthRepository) {

    suspend fun signIn(email: String, password: String): Result<User> {
        return repository.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String): Result<User> {
        return repository.signUp(email, password)
    }

    fun signOut() = repository.signOut()
}
