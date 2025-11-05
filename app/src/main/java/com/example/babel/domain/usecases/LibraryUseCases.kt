package com.example.babel.domain.usecases

import com.example.babel.data.models.UserLibrary
import com.example.babel.data.repository.LibraryRepository

class LibraryUseCases(private val repo: LibraryRepository) {

    suspend fun getLibrary(uid: String): UserLibrary? = repo.getUserLibrary(uid)

    suspend fun addBook(uid: String, bookId: Long, shelf: String) =
        repo.addBookToShelf(uid, bookId, shelf)

    suspend fun removeBook(uid: String, bookId: Long, shelf: String) =
        repo.removeBookFromShelf(uid, bookId, shelf)

    suspend fun initLibrary(uid: String) = repo.ensureLibraryExists(uid)
}
