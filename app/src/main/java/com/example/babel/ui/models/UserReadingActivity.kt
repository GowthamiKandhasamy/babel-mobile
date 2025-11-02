package com.example.babel.models

data class UserReadingActivity(
    val bookId: Int,
    val shelf: String, // "Currently Reading", "Finished Reading", "Want to Read"
    val startDate: String? = null,
    val endDate: String? = null,
    val rating: Int? = null,
    val comment: String? = null,
    val tags: List<String> = emptyList(),
    val progressType: String? = null, // "Pages" or "Percent"
    val progressValue: Int? = null
)
