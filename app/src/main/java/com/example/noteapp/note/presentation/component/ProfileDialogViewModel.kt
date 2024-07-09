package com.example.noteapp.note.presentation.component

import androidx.lifecycle.ViewModel
import com.example.noteapp.auth.data.GoogleAuthUiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ProfileDialogViewModel @Inject constructor(
    googleAuthUiClient: GoogleAuthUiClient
) :ViewModel() {
    private val _userData = MutableStateFlow(googleAuthUiClient.getSignedInUser())
    val userData = _userData.asStateFlow()
}