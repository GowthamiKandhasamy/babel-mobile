package com.example.babel.data.repository

import android.content.Context
import com.example.babel.ui.screens.JournalEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalJournalRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("local_journals", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getLocalJournals(): List<JournalEntry> {
        val json = prefs.getString("journals", null) ?: return emptyList()
        val type = object : TypeToken<List<JournalEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveLocalJournals(journals: List<JournalEntry>) {
        prefs.edit().putString("journals", gson.toJson(journals)).apply()
    }

    fun addLocalJournal(entry: JournalEntry) {
        val updated = getLocalJournals().toMutableList().apply { add(entry) }
        saveLocalJournals(updated)
    }

    fun editLocalJournal(entry: JournalEntry) {
        val updated = getLocalJournals().map {
            if (it.id == entry.id) entry else it
        }
        saveLocalJournals(updated)
    }

    fun deleteLocalJournal(id: String) {
        val updated = getLocalJournals().filterNot { it.id == id }
        saveLocalJournals(updated)
    }
}
