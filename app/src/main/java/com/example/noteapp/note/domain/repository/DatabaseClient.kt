package com.example.noteapp.note.domain.repository

import com.example.noteapp.note.domain.models.Note


interface DatabaseClient {
    suspend fun getNotes():List<Note>
    suspend fun getNoteById(id: String): Note?

    suspend fun getRealTimeNotes(listner: (List<Note>)->Unit)
    suspend fun updateNoteById(note: Note):Boolean
    suspend fun deleteNoteById(id: String):Boolean
    suspend fun addNote(note: Note):String
}