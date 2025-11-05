package com.example.babel.data.models

data class UserStats(
    val userId: String = "",
    val booksRead: Int = 0,
    val totalPages: Int = 0,
    val avgRating: Double = 0.0,
    val favoriteGenres: List<Int> = emptyList(),
    val favoriteAuthors: List<String> = emptyList(),
    val goalPages: Int = 20000,
    val goalBooks: Int = 50,
    val goalProgress: Float = 0f,
    val booksPerMonth: Map<String, Int> = emptyMap()
)
