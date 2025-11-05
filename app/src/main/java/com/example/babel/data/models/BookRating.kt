package com.example.babel.data.models

data class BookRating(
    val userId: String = "",
    val bookId: Long = 0,
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
