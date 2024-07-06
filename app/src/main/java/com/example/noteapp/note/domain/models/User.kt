package com.example.noteapp.note.domain.models

import com.google.type.DateTime

data class User(
    val id: String,
    val username: String,
    val email: String,
    val ownedDocument: List<String>,
    val photoURL : String,
    val createdAt: DateTime,
    val updatedAt: DateTime
)