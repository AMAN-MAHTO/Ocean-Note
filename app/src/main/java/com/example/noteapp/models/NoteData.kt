package com.example.noteapp.models

data class Notes(
    val id : Int,
    val userId: String,
    val notes: List<Note>
)

data class Note(
    val id: String,
    val data: NoteData
)

data class NoteData(
    var title: String = "",
    var body: String = "",
    val createdDate: String = "",
    var updatedDate:String = ""
)
