package com.example.noteapp.auth.domain.model

data class SignInState(

    val isSignInSuccessful: Boolean = false,
    val isNewUser: Boolean = false,
    val signInError: String? = null
)