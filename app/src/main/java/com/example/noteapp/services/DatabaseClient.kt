package com.example.noteapp.services

import com.example.noteapp.models.Note
import com.example.noteapp.models.NoteData

interface DatabaseClient {
    suspend fun getNotes():List<Note>
    suspend fun getNoteById(id: String):NoteData?
    suspend fun updateNoteById(note: Note):Boolean
    suspend fun deleteNoteById(id: String):Boolean
    suspend fun addNote(note:NoteData):Boolean
}