package com.example.noteapp.screens.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.noteapp.Screen
import com.example.noteapp.models.SignInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(): ViewModel() {
    val email = MutableStateFlow("")

    val password = MutableStateFlow("")

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    fun updateEmail(it: String) {
        email.value = it
    }

    fun updatePassword(it: String) {
        password.value = it
    }

    fun signIn() {
        Log.d("VIEW MODEL", "signUp: email:$email and password:$password ")
    }

    fun navigateSignUp(navScreen: (String) -> Unit) {
        navScreen(Screen.SignUp.route)
    }
}