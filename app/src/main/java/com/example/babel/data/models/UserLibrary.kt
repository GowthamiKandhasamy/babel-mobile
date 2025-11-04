package com.example.babel.data.models

data class UserLibrary(
    val uid: String = "",
    val currentlyReading: List<String> = emptyList(),
    val wantToRead: List<String> = emptyList(),
    val finishedReading: List<String> = emptyList()
)
