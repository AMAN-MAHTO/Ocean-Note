package com.example.noteapp

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.screens.sign_in.SignInViewModel
import com.example.noteapp.services.GoogleAuthUiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    googleAuthUiClient: GoogleAuthUiClient
):ViewModel() {

    private val _startDestination = MutableStateFlow(Screen.SignIn.route)
    val startDestinatioon = _startDestination.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            if(googleAuthUiClient.getSignedInUser() != null){
                _startDestination.value = Screen.Home.route
            }else{
                _startDestination.value = Screen.SignIn.route
            }

            _isReady.value = true
        }
    }



    fun passIntentToGoogleAuthSignInWithIntent(
        data: Intent?,
        googleAuthUiClient: GoogleAuthUiClient,
        signInViewModel: SignInViewModel
    ){
        viewModelScope.launch {
            val signInResult = googleAuthUiClient.signInWithIntent(
                intent = data ?: return@launch
            )
            signInViewModel.onSignInResult(signInResult)
        }

    }

    fun googleSignIntentLauncher(
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        googleAuthUiClient: GoogleAuthUiClient
    ) {
        viewModelScope.launch {
            val signInIntentSender = googleAuthUiClient.signIn()

            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender?:return@launch
                ).build()
            )
        }
    }




}