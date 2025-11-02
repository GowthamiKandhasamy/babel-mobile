package com.example.babel.ui.models

data class User(
    val id: Int,
    val name: String,
    val currentlyReading: List<Int> = emptyList(),
    val wantToRead: List<Int> = emptyList(),
    val finishedReading: List<Int> = emptyList()
)
