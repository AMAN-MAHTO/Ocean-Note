package com.example.noteapp.note.domain.models

import com.google.type.DateTime

data class Edit(
    val id: String,
    val userId: String,
    val editType: String,
    val position: Int,
    val originalContent: String,
    val newContent: String,
    val createdAt: DateTime,
)