package com.example.babel.data.utils

import java.security.MessageDigest
import java.util.*

object SecurityUtils {

    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }

    fun verifyPassword(input: String, hashed: String): Boolean {
        return hashPassword(input) == hashed
    }
}
