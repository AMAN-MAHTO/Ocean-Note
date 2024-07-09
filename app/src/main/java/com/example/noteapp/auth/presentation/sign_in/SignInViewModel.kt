package com.example.noteapp.auth.presentation.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.Screen
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.SignInState
import com.example.noteapp.auth.domain.model.SignInResult
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.auth.domain.repository.UserDatabaseClient
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,

): ViewModel() {
    private val db: FirebaseFirestore
    init {
         db= Firebase.firestore

    }
    val email = MutableStateFlow("")

    val password = MutableStateFlow("")

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        if(result.isNewUser!!){
            viewModelScope.launch {

            Log.d("SignIn", "onSignInResult: new user")
            val user = googleAuthUiClient.getSignedInUser()
            if(user != null ){
                db.collection("users")?.let {
                    val id=    it.add(copyUser(user.username,user.email,user.profilePictureUrl)).await().id
                    Log.d("SignIn", "userCollection copy user id: $id")
                    _state.update {
                        it.copy(
                            isSignInSuccessful = true,
                            isNewUser = result.isNewUser,
                            signInError = result.errorMessage,
                        )
                    }
                }

            }
            }


        }else{
            _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            isNewUser = result.isNewUser!!,
            signInError = result.errorMessage
        ) }
        }
        Log.d("SignIn", "onSignInResult: ${_state.value}")
    }

    fun resetState() {
        _state.update { SignInState() }
        Log.d("SignIn", "resetState: ${_state.value}")
    }

    fun updateEmail(it: String) {
        email.value = it
    }

    fun updatePassword(it: String) {
        password.value = it
    }

    fun signIn() {
        Log.d("SignIn", "signUp: email:$email and password:$password ")
    }







}