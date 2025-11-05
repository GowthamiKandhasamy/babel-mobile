package com.example.babel.data.models

data class WeatherRecommendation(
    val condition: String = "",
    val bookIds: List<Long> = emptyList()
)