package com.example.noteapp.note.domain.models

import com.google.type.DateTime

data class Notes(
    val id : Int,
    val userId: String,
    val notes: List<Note>
)

data class Note(
    val id: String ="",
    var data:String = "",
    val createdDate: String = "",
    var updatedDate:String = ""
)

//data class NoteData(
//    var data:String = "",
//    val createdDate: String = "",
//    var updatedDate:String = ""
//)






