package com.example.babel.data

import android.content.Context
import com.example.babel.ui.models.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GenreLoader {
    fun loadSampleGenres(context: Context): List<Genre> {
        val json = context.assets.open("genre_samples.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Genre>>() {}.type
        return Gson().fromJson(json, listType)
    }
}
