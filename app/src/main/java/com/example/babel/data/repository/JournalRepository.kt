package com.example.babel.data.repository

import com.example.babel.data.models.Journal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class JournalRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val coll = db.collection("journals")

    suspend fun getUserJournals(uid: String): List<Journal> {
        val snapshot = coll.whereEqualTo("ownerId", uid).get().await()
        return snapshot.documents.mapNotNull { it.toObject(Journal::class.java) }
    }

    suspend fun addJournal(journal: Journal) {
        val ref = coll.document()
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
    }

    suspend fun editJournal(id: String, newContent: String, visibility: String) {
        coll.document(id).update(
            mapOf("content" to newContent, "visibility" to visibility)
        ).await()
    }

    suspend fun deleteJournal(id: String) {
        coll.document(id).delete().await()
    }
}
