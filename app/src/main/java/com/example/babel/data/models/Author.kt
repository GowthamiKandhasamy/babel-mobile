package com.example.babel.data.models

data class Author(
    val id: String = "",
    val name: String = "",
    val bio: String? = null,
    val booksWritten: List<Long> = emptyList()
)
