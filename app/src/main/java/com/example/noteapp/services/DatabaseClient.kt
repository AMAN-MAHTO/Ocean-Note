package com.example.noteapp.services

import com.example.noteapp.models.Note

interface DatabaseClient {
    suspend fun getNotes(userId: String):List<Note>
    suspend fun getNoteById(id: String):Note
    suspend fun updateNoteById(note: Note):Boolean
    suspend fun deleteNoteById(id: String):Boolean
    suspend fun addNote(note:Note):Boolean
}