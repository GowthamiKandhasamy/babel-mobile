package com.example.babel.data.models

data class Journal(
    val id: String = "",
    val ownerId: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val visibility: String = "private", // “private” or “public”
    val likes: Int = 0
)
