package com.example.noteapp.services.impl

import android.util.Log
import com.example.noteapp.models.Note
import com.example.noteapp.models.NoteData
import com.example.noteapp.services.DatabaseClient
import com.example.noteapp.services.GoogleAuthUiClient
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val TAG = "FIREBASE"

class FirebaseDatabaseClientImpl @Inject constructor(
    googleAuthUiClient: GoogleAuthUiClient,
    db: FirebaseFirestore
): DatabaseClient {



    private var userDB:DocumentReference? = null
    private var notesCollectionReference: CollectionReference? = null

    //create NoteDatabase for user if not exist
    init {
        val userData = googleAuthUiClient.getSignedInUser()
        if(userData != null){
            userDB = db.collection("NoteDatabase")
                .document(userData.userId)

//            userDB?.set(userData)
            notesCollectionReference = userDB?.collection("Notes")

        }
        Log.d(TAG, "userDB: $userDB")



    }

    override suspend fun addNote(note: NoteData): Boolean {
        var success:Boolean = false
        notesCollectionReference?.let {
            it.add(note)
                .addOnSuccessListener { success = true }.addOnFailureListener { success = false }
        }
        return success
    }

    override suspend fun getNotes(): List<Note> {
        val list = mutableListOf<Note>()
        notesCollectionReference?.let {
            try {
                //why, because interacting with a Firestore database, which is asynchronous in nature.
                val querySnapshot= it.get().await()

                querySnapshot.documents.forEach {documentSnapshot->

                    documentSnapshot.toObject<NoteData>()?.let { list.add(Note(documentSnapshot.id,it)) }

                }


            }catch(e: Exception) {
                    Log.d(TAG, "failed to get user notes", e)
                }
        }

        return list
    }

    override suspend fun getNoteById(id: String): NoteData? {
        var note:NoteData? = null
        notesCollectionReference?.let {
            try {
                val querySnapshot= it.document(id).get().await()
                Log.d(TAG, "getNoteById id=$id : noteData: ${querySnapshot.toObject<NoteData>()}")
                note = querySnapshot.toObject<NoteData>()

            }catch (e:Exception){
                Log.d(TAG, "failed to get note with id ", e)

            }
        }
        return note
    }

    override suspend fun updateNoteById(note: Note): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNoteById(id: String): Boolean {
        TODO("Not yet implemented")
    }


}