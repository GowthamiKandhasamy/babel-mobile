package com.example.babel.data.models

data class EncryptedLocation(
    val userId: String = "",
    val hashedLocation: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locality: String = "",
    val timestamp: Long = 0L
)
