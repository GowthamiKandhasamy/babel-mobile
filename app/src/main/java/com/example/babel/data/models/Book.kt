package com.example.babel.data.models

data class Book(
    val id: Long = 0,
    val title: String = "",
    val subtitle: String? = null,
    val authors: List<String> = emptyList(),
    val averageRating: Double? = 0.0,
    val publishedDate: String? = null,
    val isbn10: String? = null,
    val isbn13: String? = null,
    val pageCount: Int? = null,
    val printType: String? = null,
    val maturityRating: String? = null,
    val coverImage: String? = null,
    val smallThumbnail: String? = null,
    val language: String? = null,
    val genre_id: List<Int> = emptyList()
)

