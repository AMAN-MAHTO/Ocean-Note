package com.example.noteapp.auth.domain.repository

import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.auth.domain.model.copyUser

interface UserDatabaseClient {
    suspend fun addUser(copyUser: copyUser):Boolean
}