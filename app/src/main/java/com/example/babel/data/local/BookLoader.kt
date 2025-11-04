package com.example.babel.data.local

import android.content.Context
import com.example.babel.data.models.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

object BookLoader {

    fun loadSampleBooks(context: Context): List<Book> {
        return try {
            val inputStream = context.assets.open("book_samples.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }

            val bookListType = object : TypeToken<List<Book>>() {}.type
            Gson().fromJson<List<Book>>(jsonString, bookListType)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
