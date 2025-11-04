package com.example.babel.data.models

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val label: String = "New Reader",
    val createdAt: Long? = null
)
