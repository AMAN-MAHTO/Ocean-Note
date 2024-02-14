package com.example.noteapp.services.impl

import android.util.Log
import com.example.noteapp.models.Note
import com.example.noteapp.models.Notes
import com.example.noteapp.services.DatabaseClient
import com.example.noteapp.services.GoogleAuthUiClient
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.log

val TAG = "FIREBASE"

class FirebaseDatabaseClientImpl @Inject constructor(
    googleAuthUiClient: GoogleAuthUiClient,
    private val db: FirebaseFirestore
): DatabaseClient {

    val notes = emptyList<Note>()
    val note = Note(
        id = 1,
        title = "Meeting Notes and Action Items",
        body = "Discussed project updates and deadlines with the team. Assigned action items to each team member.",
        createdDate = "2024-01-10 09:30:00",
        updatedDate = "2024-01-10 09:45:00"
    )

    private var userDB:DocumentReference? = null

    //create NoteDatabase for user if not exist
    init {
        if(googleAuthUiClient.getSignedInUser() != null){

            userDB = db.collection("NoteDatabase")
                .document(googleAuthUiClient.getSignedInUser()!!.userId)
        }



    }

    override suspend fun addNote(note: Note): Boolean {
        var success:Boolean = false
        userDB?.let {
            it.collection("Notes").document(note.id.toString()).set(note)
                .addOnSuccessListener { success = true }.addOnFailureListener { success = false }
        }
        return success
    }

    override suspend fun getNotes(userId: String): List<Note> {
        val list = mutableListOf<Note>()
        userDB?.let {
            try {
                //why, because interacting with a Firestore database, which is asynchronous in nature.
                val querySnapshot= it.collection("Notes").get().await()
                querySnapshot.documents.forEach {
                    it.toObject<Note>()?.let { list.add(it) }

                }


            }catch(e: Exception) {
                    Log.d(TAG, "get failed with ", e)
                }
        }

        return list
    }

    override suspend fun getNoteById(id: String): Note {
        TODO("Not yet implemented")
    }

    override suspend fun updateNoteById(note: Note): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNoteById(id: String): Boolean {
        TODO("Not yet implemented")
    }


}