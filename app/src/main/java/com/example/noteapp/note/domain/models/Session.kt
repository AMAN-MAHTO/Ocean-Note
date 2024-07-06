package com.example.noteapp.note.domain.models

import com.google.type.DateTime

data class Session(
    val id: String,
    val users : List<String>,
    val createdAt: DateTime,
    val updatedAt: DateTime,

    )

