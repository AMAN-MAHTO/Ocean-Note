package com.example.noteapp.note.domain.models

data class Version(
    val docId: String = "",
    val title: String = "",
    val body: String = "",
    val ownerId: String = "",
    val createdAt: Long = 0,


    )