package com.example.babel.data.local

import android.content.Context
import com.example.babel.data.models.User

object UserDataLoader {

    // For now, single mock user.
    fun loadSampleUser(context: Context): User {
        // Load books to get valid IDs.
        val books = BookLoader.loadSampleBooks(context)
        val bookIds = books.map { it.id }

        // Split them into random shelves (just for demo)
        return User(
            uid = "345038063",
            email = "william.paterson@my-own-personal-domain.com",
            name = "Wanderer",
            photoUrl = "https://picsum.photos/200",
            label = "New Reader",
            createdAt = 1672531200000
        )
    }
}
