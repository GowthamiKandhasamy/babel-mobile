package com.example.babel.data.models

data class UserReadingActivity(
    val userId: String = "",
    val bookId: String = "",
    val shelf: String = "", // “Currently Reading”, “Finished Reading”
    val startDate: String? = null,
    val endDate: String? = null,
    val progressType: String? = null, // “Pages” or “Percent”
    val progressValue: Int? = null,
    val rating: Int? = null,
    val comment: String? = null,
    val tags: List<String> = emptyList()
)
