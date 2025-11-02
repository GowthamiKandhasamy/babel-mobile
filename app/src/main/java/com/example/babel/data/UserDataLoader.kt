package com.example.babel.data

import android.content.Context
import com.example.babel.models.Book
import com.example.babel.ui.models.User

object UserDataLoader {

    // For now, single mock user.
    fun loadSampleUser(context: Context): User {
        // Load books to get valid IDs.
        val books = BookLoader.loadSampleBooks(context)
        val bookIds = books.map { it.id }

        // Split them into random shelves (just for demo)
        return User(
            id = 1,
            name = "Wanderer",
            currentlyReading = bookIds.take(5),
            wantToRead = bookIds.drop(5).take(5),
            finishedReading = bookIds.drop(10).take(5)
        )
    }
}
