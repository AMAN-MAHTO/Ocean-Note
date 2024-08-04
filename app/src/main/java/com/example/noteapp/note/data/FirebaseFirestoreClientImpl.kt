package com.example.noteapp.note.data

import android.util.Log
import com.example.noteapp.Permission
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.domain.models.ShareHolder
import com.example.noteapp.note.domain.models.Shared
import com.example.noteapp.note.domain.models.Version
import com.example.noteapp.note.domain.repository.DatabaseClient2
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val TAG = "FirebaseDBImpl"

class FirebaseFirestoreClientImpl @Inject constructor(
    googleAuthUiClient: GoogleAuthUiClient,
    db: FirebaseFirestore,

    ) : DatabaseClient2 {
    private var documentsCollectionReference: CollectionReference? = null
    private var sharedCollectionReference: CollectionReference? = null
    private var userCollectionReference: CollectionReference? = null
    private var versionCollectionReference: CollectionReference? = null

    private var userData: UserData? = null

    init {
        userData = googleAuthUiClient.getSignedInUser()
        if (userData != null) {

            documentsCollectionReference = db.collection("documents")
            sharedCollectionReference = db.collection("shared")
            userCollectionReference = db.collection("users")
            versionCollectionReference = db.collection("versions")
        }
    }

    override suspend fun addDocument(document: Document): String {
        var docId = ""
        documentsCollectionReference?.let {
            docId = it.add(document.copy(ownerId = userData!!.userId)).await().id
        }

        documentsCollectionReference?.let {
            it.document(docId).update(
                mapOf(
                    "id" to docId
                )
            ).await()
        }
        return docId
    }

    override suspend fun deleteDocumentById(docId: String) {
        try {

            documentsCollectionReference?.let {
                it.document(docId).delete().await()
            }
        } catch (e: Exception) {
            Log.d("Document", "deleteDocumentById: ERROR $e")
        }
    }

    override suspend fun updateDocument(document: Document) {
        documentsCollectionReference?.let {
            it.document(document.id).update(
                mapOf(
                    "title" to document.title,
                    "body" to document.body,
                    "updatedAt" to document.updatedAt
                )
            ).await()
        }
    }

    override suspend fun addShared(shared: Shared): String {
        var sharedId = ""
        try {

            sharedCollectionReference?.let {
                sharedId = it.add(
                    shared.copy(
                        createdBy = userData!!.userId,
                        createdAt = System.currentTimeMillis(),
                    )
                ).await().id
            }
            sharedCollectionReference?.let {
                it.document(sharedId).update(
                    mapOf(
                        "id" to sharedId
                    )
                ).await()
            }
        } catch (e: Exception) {
            Log.d(TAG, "failed to add shared", e)
        }
        // adding share id in document share list
        try {

            documentsCollectionReference?.let { collectionRefrence ->
                collectionRefrence.document(shared.documentId).get().await().toObject<Document>()
                    ?.let {
                        val list = it.sharedIdList.toMutableList()
                        list.add(sharedId)
                        collectionRefrence.document(shared.documentId).update(
                            mapOf(
                                "sharedIdList" to list
                            )
                        ).await()
                    }

            }
        } catch (e: Exception) {
            Log.d(TAG, "failed to adding share id in document share list", e)
        }
        return sharedId
    }

    override suspend fun addEditor(document: Document) {
        try {
            documentsCollectionReference?.let { docRef ->
                docRef.document(document.id).get().await().toObject<Document>()?.let {
                    val editor = it.currentEditors.toMutableList()
                    editor.add(userData!!.userId)
                    docRef.document(document.id).update(
                        mapOf(
                            "currentEditors" to editor
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "failed ", e)
        }
    }

    override suspend fun removeEditor(document: Document, listener: () -> Unit) {
        try {
            documentsCollectionReference?.let { docRef ->
                docRef.document(document.id).get().await().toObject<Document>()?.let {
                    val editor = it.currentEditors.toMutableList()
                    editor.remove(userData!!.userId)
                    docRef.document(document.id).update(
                        mapOf(
                            "currentEditors" to editor
                        )
                    )
                    if (editor.isEmpty()) {
                        listener()
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "failed ", e)

        }
    }

    override suspend fun addVersion(document: Document) {
        try {
            var versionId = ""
            versionCollectionReference?.let {
                versionId = it.add(
                    Version(
                        docId = document.id,
                        title = document.title,
                        body = document.body,
                        ownerId = document.ownerId,
                        createdAt = System.currentTimeMillis(),
                    )
                ).await().id
            }

            documentsCollectionReference?.let { docRef ->
                docRef.document(document.id).get().await().toObject<Document>()?.let {
                    val list = it.versionIdList.toMutableList()
                    list.add(versionId)
                    docRef.document(document.id).update(
                        mapOf(
                            "versionIdList" to list
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "addVersion: $e")
        }

    }

    override suspend fun getSharedCard(docId: String, listener: (Shared) -> Unit) {
        sharedCollectionReference?.let {
            try {
                it.whereEqualTo("documentId", docId).whereEqualTo("sharedWith", userData!!.email)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && !snapshot.isEmpty) {
                            snapshot.documents.forEach {
                                it.toObject<Shared>()?.let(listener)
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.d(TAG, "failed to get sharedCard", e)
            }
        }
    }

    override suspend fun getRealtimeDocumentOwned(listener: (List<Document>) -> Unit) {
        documentsCollectionReference?.let {
            try {
                it.whereEqualTo("ownerId", userData!!.userId).addSnapshotListener { snapshot, e ->
                    val result = mutableListOf<Document>()
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {

                        snapshot.documents.forEach { documentSnapshot ->

                            documentSnapshot.toObject<Document>()?.let { result.add(it) }

                        }

                    } else {
                        Log.d(TAG, "Current data RealtimeDocumentOwned: null")
                    }
                    listener(result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "failed to get RealtimeDocumentOwned", e)
            }
        }
    }

    // it get all the document that have been shared with current user
    override suspend fun getRealtimeSharedDocumentOfCurrentUser(
        sharedListner: (List<Shared>) -> Unit,
        docListner: (List<Document>) -> Unit
    ) {

        sharedCollectionReference?.let {
            try {
                val currentUserSharedDocumentIdList = mutableListOf<String>()
                val currentUserSharedCardList = mutableListOf<Shared>()
                it.whereEqualTo("sharedWith", userData!!.email).addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.d("Document", "listern Failed: $e")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        currentUserSharedDocumentIdList.clear()
                        currentUserSharedCardList.clear()
                        snapshot.documents.forEach {
                            it.toObject<Shared>()?.let {
                                currentUserSharedDocumentIdList.add(it.documentId)
                                currentUserSharedCardList.add(it)
                            }
                        }
                    }
                    Log.d(
                        "Document",
                        "getRealtimeDocumentShared: shared document list: $currentUserSharedDocumentIdList"
                    )
                    sharedListner(currentUserSharedCardList)
                    if (currentUserSharedDocumentIdList.isNotEmpty())
                        documentsCollectionReference?.let {
                            val sharedDocument = mutableListOf<Document>()
                            try {

                                it.whereIn("id", currentUserSharedDocumentIdList)
                                    .addSnapshotListener { snapshot, e ->
                                        if (e != null) {
                                            Log.d("Document", "listern Failed: $e")
                                            return@addSnapshotListener
                                        }
                                        if (snapshot != null) {
                                            sharedDocument.clear()
                                            snapshot.documents.forEach {
                                                it.toObject<Document>()?.let {

                                                    sharedDocument.add(it)
                                                }

                                            }
                                        }
                                        Log.d(
                                            "Document",
                                            "getRealtimeDocumentShared: document : $sharedDocument"
                                        )
                                        docListner(sharedDocument)
                                    }
                            } catch (e: Exception) {
                                Log.d(TAG, "failed to get RealtimeDocumentShared", e)
                            }
                        }
                }
            } catch (e: Exception) {
                Log.d(TAG, "failed to get shared", e)
            }

        }
    }

    // it get all the user that the given document is shared with
    override suspend fun getRealtimeShareHolderOfGivenDocument(
        docId: String,
        listener: (shareHolderList: List<ShareHolder>) -> Unit
    ) {
        sharedCollectionReference?.let {
            try {
                val shared = mutableListOf<Shared>()
                val sharedEmailList = mutableListOf<String>()
                val shareHolderList = mutableListOf<ShareHolder>()
                it.whereEqualTo("documentId", docId).addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("Document", "listern Failed: $e")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        snapshot.documents.forEach {
                            it.toObject<Shared>()?.let {
                                shared.add(it)
                                sharedEmailList.add(it.sharedWith)
                            }

                        }
                        if (sharedEmailList.isNotEmpty()) {
                            userCollectionReference?.let {
                                it.whereIn("email", sharedEmailList)
                                    .addSnapshotListener { snapshot, e ->
                                        if (e != null) {
                                            Log.e("Document", "listern Failed: $e")
                                            return@addSnapshotListener
                                        }
                                        snapshot?.documents?.forEach { documentSnapshot ->
                                            documentSnapshot.toObject<copyUser>()?.let { copyUser ->
                                                val shared = shared.first {
                                                    it.sharedWith == (copyUser.email ?: "")
                                                }
                                                shareHolderList.add(
                                                    ShareHolder(
                                                        sharedId = shared.id,
                                                        email = copyUser.email ?: "",
                                                        profilePic = copyUser.profilePic ?: "",
                                                        documentId = shared.documentId,
                                                        permissionType = shared.permissionType
                                                    )
                                                )
                                            }
                                        }
                                        listener(shareHolderList)
                                    }
                            }
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e("Document", "getRealtimeShareHolderOfGivenDocument: $e")
            }
        }
    }

    override suspend fun updateSharedPermission(shareHolder: ShareHolder, permission: Permission) {
        try {
            sharedCollectionReference?.let {
                it.document(shareHolder.sharedId).update(
                    mapOf(
                        "permissionType" to permission.toString()
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateSharedPermission: error $e")
        }
    }

    override suspend fun deleteSharedPermission(shareHolder: ShareHolder) {
        try {
            sharedCollectionReference?.let {
                it.document(shareHolder.sharedId).delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteSharedPermission: error $e")
        }
    }


    override suspend fun getRealtimeDocumentById(id: String, listener: (Document) -> Unit) {
        documentsCollectionReference?.let {
            try {

                it.whereEqualTo("id", id).addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.d("Document", "listern Failed: $e")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        snapshot.documents.forEach {
                            it.toObject<Document>()?.let(listener)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "failed to get doc", e)
            }
        }
    }

    override suspend fun getRealtimeSearch(query: String, listener: (List<copyUser>) -> Unit) {
        userCollectionReference?.let {
            try {
                it.whereGreaterThanOrEqualTo("email", query)
                    .whereLessThanOrEqualTo("email", query + "~")
                    .get().await().let {
                        val result = mutableListOf<copyUser>()
                        it.documents.forEach {
                            it.toObject<copyUser>()?.let {
                                result.add(it)
                            }
                        }
                        Log.d("Search", "onQueryChange: ${result}")

                        listener(result)
                    }

            } catch (e: Exception) {
                Log.e("Search", "failed to get search user", e)
            }
        }
    }
}