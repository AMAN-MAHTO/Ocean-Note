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
            notesCollectionReference = db.collection("Notes")

        }
        Log.d(TAG, "userDB: $userDB")



    }

    override suspend fun addNote(note: NoteData): String {
        var docRef = ""
        notesCollectionReference?.let {
            docRef = it.add(note).await().toString()

        }
        return docRef
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

    override suspend fun getRealTimeNotes(listner: (List<Note>) -> Unit) {
        notesCollectionReference?.let {
            try {
                //why, because interacting with a Firestore database, which is asynchronous in nature.
                it.addSnapshotListener { snapshot, e ->
                    val list = mutableListOf<Note>()
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {

                        snapshot.documents.forEach {documentSnapshot->

                            documentSnapshot.toObject<NoteData>()?.let { list.add(Note(documentSnapshot.id,it)) }

                        }

                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                    listner(list)
                }


            }catch(e: Exception) {
                Log.d(TAG, "failed to get user notes", e)
            }
        }
    }

    override suspend fun updateNoteById(noteId: String, noteData: NoteData):Boolean {
        var sucess = false
        try {
            notesCollectionReference?.let {
                it.document(noteId).update(
                    mapOf(
                        "title" to noteData.title,
                        "body" to noteData.body,
                        "createdDate" to noteData.createdDate,
                        "updatedDate" to noteData.updatedDate
                    )
                ).await()
                sucess = true
            }
        }catch(e: Exception) {
            Log.d(TAG, "failed to update user notes", e)
        }
        return sucess

    }

    override suspend fun deleteNoteById(id: String): Boolean {
        TODO("Not yet implemented")
    }


}