package com.example.noteapp.note.domain.repository

import com.example.noteapp.Permission
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.domain.models.ShareHolder
import com.example.noteapp.note.domain.models.Shared

interface DatabaseClient2 {

    suspend fun addDocument(
        document: Document,
        userData: UserData,
        listener: (docId: String) -> Unit
    ): String

    suspend fun getRealtimeDocumentOwned(userData: UserData, listener: (List<Document>) -> Unit)

    suspend fun getRealtimeSharedDocumentOfCurrentUser(
        userData: UserData,
        sharedListner: (List<Shared>) -> Unit,
        docListner: (List<Document>) -> Unit
    )

    suspend fun getSharedCard(docId: String, userData: UserData, listener: (Shared) -> Unit)
    suspend fun addShared(shared: Shared, userData: UserData): String
    suspend fun updateSharedPermission(shareHolder: ShareHolder, permission: Permission)
    suspend fun deleteSharedPermission(shareHolder: ShareHolder, listener: () -> Unit)

    suspend fun getRealtimeDocumentById(id: String, listener: (Document) -> Unit)
    suspend fun updateDocument(document: Document)
    suspend fun deleteDocumentById(_docId: String)

    suspend fun getRealtimeSearch(query: String, listener: (List<copyUser>) -> Unit)

    suspend fun getRealtimeShareHolderOfGivenDocument(
        docId: String,
        listener: (shareHolderList: List<ShareHolder>) -> Unit
    )

    suspend fun getEditorProfile(
        docId: String,
        listener: (List<copyUser>) -> Unit
    )

    suspend fun addEditor(document: Document, userData: UserData)
    suspend fun removeEditor(document: Document, userData: UserData, listener: () -> Unit)
    suspend fun addVersion(document: Document)
    suspend fun getOwnerDataFromDocId(docId: String, listener: (copyUser) -> Unit)
}