package com.example.noteapp.note.domain.repository

import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.domain.models.EditorRelamChild
import com.google.firebase.database.DatabaseReference

interface RealtimeDatabaseClient {

    suspend fun addDocument(document: Document, userData: UserData): String
    suspend fun updateDocument(document: Document)
    suspend fun deleteDocumentById(_docId: String)
    suspend fun updateDocumentVersion(document: Document)
    suspend fun getLatestVersion(document: Document, listener: () -> Unit)
    suspend fun removeVersion(document: Document)
    suspend fun getRealtimeVersion(id: String, listener: (Document) -> Unit)


    suspend fun createEditorRealm(document: Document)
    suspend fun removeEditorRealm(document: Document)
    suspend fun getRealtimeEditorRealm(document: Document, listener: (EditorRelamChild) -> Unit)
    suspend fun updateEditorRealm(editorRelamChild: EditorRelamChild)
    suspend fun removeEditorRealmListener(document: Document)
}