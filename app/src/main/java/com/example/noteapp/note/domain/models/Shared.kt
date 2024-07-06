package com.example.noteapp.note.domain.models

import com.google.type.DateTime

data class Shared(
    val id: String = "",
    val sharedWith: String = "", // sahared user email
    val documentId: String = "",
    val permissionType: String = "",
    val createdBy: String = "",
    val createdAt: Long = 0
)