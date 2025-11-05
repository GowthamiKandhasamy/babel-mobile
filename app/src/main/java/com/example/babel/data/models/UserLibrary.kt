package com.example.babel.data.models

data class UserLibrary(
    val userId: String = "",
    val currentlyReading: List<Long> = emptyList(), // book IDs
    val wantToRead: List<Long> = emptyList(),
    val finishedReading: List<Long> = emptyList()
)
