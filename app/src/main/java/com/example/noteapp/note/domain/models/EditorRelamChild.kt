package com.example.noteapp.note.domain.models

data class EditorRelamChild(
    val id : String = "",
    val title: String = "",
    val body: String ="",
    val currentEditors: List<String> = emptyList(),
    val updatedAt: Long = 0,
    )
