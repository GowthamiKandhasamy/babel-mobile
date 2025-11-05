package com.example.babel.data.models

data class UserLibrary(
    val userId: String = "",
    val currentlyReading: List<String> = emptyList(), // book IDs
    val wantToRead: List<String> = emptyList(),
    val finishedReading: List<String> = emptyList()
)
