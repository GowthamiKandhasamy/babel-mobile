package com.example.babel.data

import android.content.Context
import com.example.babel.models.UserReadingActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UserReadingActivityLoader {
    fun loadUserActivity(context: Context): List<UserReadingActivity> {
        return try {
            val json = context.assets.open("user_activity.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<UserReadingActivity>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            sampleUserActivity()
        }
    }

    private fun sampleUserActivity(): List<UserReadingActivity> = listOf(
        UserReadingActivity(
            bookId = 1,
            shelf = "Finished Reading",
            startDate = "01 Jan 2025",
            endDate = "07 Jan 2025",
            rating = 4,
            tags = listOf("fantasy", "adventure"),
            progressType = "Pages",
            progressValue = 350
        ),
        UserReadingActivity(
            bookId = 2,
            shelf = "Finished Reading",
            startDate = "15 Jan 2025",
            endDate = "28 Jan 2025",
            rating = 5,
            tags = listOf("romance", "comfort"),
            progressType = "Pages",
            progressValue = 420
        ),
        UserReadingActivity(
            bookId = 3,
            shelf = "Currently Reading",
            startDate = "10 Feb 2025",
            progressType = "Percent",
            progressValue = 45
        )
    )
}
