package com.example.noteapp

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.noteapp.auth.presentation.sign_in.SignInViewModel
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.auth.domain.repository.UserDatabaseClient


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KFunction0

val TAG = "MainViewModel"
@HiltViewModel
class MainViewModel @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val userDatabaseClient: UserDatabaseClient,

):ViewModel() {

    private val _startDestination = MutableStateFlow(Screen.SignIn.route)
    val startDestinatioon = _startDestination.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            if(googleAuthUiClient.getSignedInUser() != null){

                _startDestination.value = Screen.DocumentList.route
            }else{
                _startDestination.value = Screen.SignIn.route
            }


        }
        _isReady.value = true
    }



    fun passIntentToGoogleAuthSignInWithIntent(
        data: Intent?,
        googleAuthUiClient: GoogleAuthUiClient,
        signInViewModel: SignInViewModel
    ){
        viewModelScope.launch {
            googleAuthUiClient.signInWithIntent(
                intent = data ?: return@launch
            ) {

            signInViewModel.onSignInResult(it)
            }
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