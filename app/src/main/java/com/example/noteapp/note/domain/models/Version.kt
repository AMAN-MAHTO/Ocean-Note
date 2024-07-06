package com.example.noteapp.note.domain.models

import com.google.type.DateTime

data class Version(
    val id: String,
    val versionNumber: Int,
    val content: Content,
    val createdAt: DateTime,
)