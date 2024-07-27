package com.example.noteapp.note.domain.models

import com.google.type.DateTime


data class Document(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val ownerId: String = "",
    val currentEditors: List<String> = emptyList(),
    val lastEditTime: Long = 0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val sharedIdList: List<String> = emptyList(),
    val versionIdList: List<String> = emptyList(),

    )

data class editor(
    val id: String = "",
    val currsorPosition: String = "",
)

//data class Document(
//
//    val ownerId: String = "",
//
//
//)