package com.example.noteapp.auth.domain.model

data class SignInResult(
    val data: UserData?,
    val isNewUser: Boolean?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String?
)

data class copyUser(
    val username: String? = "",
    val email: String? ="",
    val profilePic: String? =""
)