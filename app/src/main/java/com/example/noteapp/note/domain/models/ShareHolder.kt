package com.example.noteapp.note.domain.models

data class ShareHolder(
    val sharedId: String,
    val documentId: String,
    val email: String = "",
    val profilePic: String ="",
    val permissionType: String = "",
)
