package com.example.babel.data.repository

import android.util.Log
import com.example.babel.data.models.Journal
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class JournalRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val coll = db.collection("journals")

    // ✅ Fetch all journals for a specific user
    suspend fun getUserJournals(uid: String): List<Journal> {
        return try {
            val snapshot = coll.whereEqualTo("ownerId", uid).get().await()
            snapshot.documents.mapNotNull { doc ->
                val journal = doc.toObject(Journal::class.java)
                journal?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("JournalRepo", "❌ Failed to fetch journals: ${e.message}", e)
            emptyList()
        }
    }

    // ✅ Add a new journal
    suspend fun addJournal(journal: Journal) {
        val ref = coll.document()
        try {
            Log.d("JournalRepo", "🟡 Attempting to add journal for user: ${journal.ownerId}")
            ref.set(
                mapOf(
                    "id" to ref.id,
                    "ownerId" to journal.ownerId,
                    "content" to journal.content,
                    "visibility" to journal.visibility,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "likes" to 0
                )
            ).await()
            Log.d("JournalRepo", "✅ Journal added successfully with ID: ${ref.id}")
        } catch (e: Exception) {
            Log.e("JournalRepo", "❌ Failed to add journal: ${e.message}", e)
        }
    }

    // ✅ Edit an existing journal
    suspend fun editJournal(id: String, newContent: String, visibility: String) {
        try {
            coll.document(id).update(
                mapOf(
                    "content" to newContent,
                    "visibility" to visibility
                )
            ).await()
            Log.d("JournalRepo", "✅ Journal updated: $id")
        } catch (e: Exception) {
            Log.e("JournalRepo", "❌ Failed to update journal: ${e.message}", e)
        }
    }

    // ✅ Delete a journal
    suspend fun deleteJournal(id: String) {
        try {
            coll.document(id).delete().await()
            Log.d("JournalRepo", "✅ Journal deleted: $id")
        } catch (e: Exception) {
            Log.e("JournalRepo", "❌ Failed to delete journal: ${e.message}", e)
        }
    }
}
