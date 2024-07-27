package com.example.noteapp.note.domain.repository

import com.example.noteapp.Permission
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.domain.models.ShareHolder
import com.example.noteapp.note.domain.models.Shared

interface DatabaseClient2 {

    suspend fun addDocument(document: Document): String

    suspend fun addShared(shared: Shared): String
    suspend fun getRealtimeDocumentOwned(listener: (List<Document>) -> Unit)

    suspend fun getRealtimeSharedDocumentOfCurrentUser(
        sharedListner: (List<Shared>) -> Unit,
        docListner: (List<Document>) -> Unit
    )

    suspend fun getRealtimeDocumentById(id: String, listener: (Document) -> Unit)

    suspend fun getSharedCard(docId: String, listener: (Shared) -> Unit)

    suspend fun updateDocument(document: Document)
    suspend fun deleteDocumentById(_docId: String)
    suspend fun getRealtimeSearch(query: String, listener: (List<copyUser>) -> Unit)

    suspend fun getRealtimeShareHolderOfGivenDocument(
        docId: String,
        listener: (shareHolderList: List<ShareHolder>) -> Unit
    )

    suspend fun updateSharedPermission(shareHolder: ShareHolder, permission: Permission)
    suspend fun addEditor(document: Document)
    suspend fun removeEditor(document: Document, listener: () -> Unit)
    suspend fun addVersion(document: Document)
}