package com.example.noteapp.note.data

import android.util.Log
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.domain.models.EditorRelamChild
import com.example.noteapp.note.domain.repository.RealtimeDatabaseClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
var TAG1 = "FirebaseRDB"
class FirebaseDatabaseClientImpl @Inject constructor(
    googleAuthUiClient: GoogleAuthUiClient,
    rdb: FirebaseDatabase
) : RealtimeDatabaseClient {

    private var userData: UserData? = null
    private var documentCollectionRef: DatabaseReference? = null
    private var editorRealmRef: DatabaseReference? = null
    init {
        userData = googleAuthUiClient.getSignedInUser()
        if(userData != null){
            documentCollectionRef = rdb.getReference("Version")
            editorRealmRef = rdb.getReference("Editor_Realm")

        }

    }
    override suspend fun addDocument(document: Document): String {
        var docId = ""
        Log.d(TAG1, "addDocument: ")
        try {
            documentCollectionRef?.let {dbRef->
                dbRef.push().key?.let {
                    docId = it
                    Log.d(TAG1, "addDocument: $it")

               dbRef.child(it).setValue(document.copy(
                   id = it,
                   ownerId = userData!!.userId,
                   updatedAt = System.currentTimeMillis(),
                   createdAt = System.currentTimeMillis(),
               ))
                       .addOnFailureListener {
                        Log.d(TAG1, "addDocument: $it")

                       }.addOnSuccessListener {
                       Log.d(TAG1, "addDocument: sucess")


                   }

                    dbRef.child(it).child("sharedIdList").setValue("")
                    dbRef.child(it).child("currentEditors").setValue("")

                }
            }
        }catch (e:Exception){
            Log.e(TAG1, "addDocument: $e", )
        }
        return docId

    }

    override suspend fun updateDocument(document: Document) {
        try {
            documentCollectionRef?.let {
                it.child(document.id).updateChildren(
                    mapOf(
                        "title" to document.title,
                        "body" to document.body,
                        "updatedAt" to document.updatedAt
                    )
                )
            }
        }catch (e:Exception){
            Log.e(TAG1, "addDocument: $e", )
        }
    }

    override suspend fun deleteDocumentById(_docId: String) {
        try {
            documentCollectionRef?.let {
                it.child(_docId).removeValue()
            }
        }catch (e:Exception){
            Log.e(TAG1, "addDocument: $e", )
        }    }

    override suspend fun updateDocumentVersion(document: Document) {
        try {
            documentCollectionRef?.let {
                it.child(document.id).setValue(
                    document
                )
            }
        }catch(e: Exception){
            Log.e(TAG1, "updateDocumentVersion: $e")
        }
    }

    override suspend fun getLatestVersion(document: Document,listener: ()->Unit) {
        try {
            documentCollectionRef?.let {
                it.child(document.id).get().let {

                    listener()
                }
            }
        }catch (e: Exception){
            Log.e(TAG1, "getLatestVersion: $e" )
        }
    }

    override suspend fun removeVersion(document: Document) {
        try {
            documentCollectionRef?.let {
                it.child(document.id).removeValue()
            }
        }catch (e: Exception){
            Log.e(TAG1, "removeVersion: $e" )
        }
    }

    override suspend fun getRealtimeVersion(id: String, listener: (Document) -> Unit){
        try {
            documentCollectionRef?.let {
                it.child(id).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue(Document::class.java)?.let { it1 -> listener(it1) }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG1, "loadPost:onCancelled", error.toException())

                    }

                })
            }
        }catch (e: Exception){
            Log.e(TAG1, "getRealtimeVersion: $e" )
        }
    }

    override suspend fun createEditorRealm(document: Document) {
        try {
            editorRealmRef?.let {
                it.child(document.id).setValue(
                    mapOf(
                        "id" to document.id,
                        "title" to document.title,
                         "body" to document.body,
                        "currentEditors" to document.currentEditors,
                        "updateAt" to document.updatedAt,
                    )
                )
            }
        }catch (e: Exception){
            Log.e(TAG1, "createEditorRealm: $e" )
        }
    }

    override suspend fun removeEditorRealm(document: Document) {
        try {


            editorRealmRef?.let {
                it.child(document.id).removeValue()
            }
        }catch (e: Exception){
            Log.e(TAG1, "removeEditorRelam: $e" )
        }
    }

    override suspend fun updateEditorRealm(editorRelamChild: EditorRelamChild){
        try {
            editorRealmRef?.let {
                it.child(editorRelamChild.id).updateChildren(
                    mapOf(
                        "title" to editorRelamChild.title,
                        "body" to editorRelamChild.body,
                        "currentEditors" to editorRelamChild.currentEditors,
                        "updateAt" to System.currentTimeMillis(),

                        )
                )
            }
        }catch (e: Exception){
            Log.e(TAG1, "updateEditorRealm: $e" )
        }
    }

    override suspend fun getRealtimeEditorRealm(document: Document, listener: (EditorRelamChild) -> Unit) {
        try {
            editorRealmRef?.let {

                it.child(document.id).addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){

                            snapshot.getValue(EditorRelamChild::class.java)
                                ?.let { it1 -> listener(it1) }
                            }else{
                                CoroutineScope(Dispatchers.IO).launch {
                                createEditorRealm(document)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w(TAG1, "getRealtimeEditorRealm:onCancelled", error.toException())

                        }

                    }
                )
            }
        }catch (e: Exception){
            Log.e(TAG1, "getRealtimeEditorRealm: $e" )
        }
    }


}