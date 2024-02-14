package com.example.noteapp.services.impl

import android.util.Log
import com.example.noteapp.models.Note
import com.example.noteapp.models.Notes
import com.example.noteapp.services.DatabaseClient
import com.example.noteapp.services.GoogleAuthUiClient
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

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

    private var userDB:DocumentReference

    //create NoteDatabase for user if not exist
    init {

            userDB = db.collection("NoteDatabase")
                    .document(googleAuthUiClient.getSignedInUser()!!.userId)


    }

    override suspend fun addNote(note: Note): Boolean {
        var success:Boolean = false
        userDB.set(note).addOnSuccessListener {success = true }.addOnFailureListener{success = false}
        return success
    }

    override suspend fun getNotes(userId: String): List<Note> {
        TODO("Not yet implemented")
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