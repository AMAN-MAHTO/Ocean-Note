package com.example.noteapp.auth.presentation.sign_up

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.noteapp.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor():ViewModel() {

    val email = MutableStateFlow("")

    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")


    fun updateEmail(it: String) {
        email.value = it
    }

    fun updatePassword(it: String) {
        password.value = it
    }
    fun updateConfirmPassword(it: String) {
        confirmPassword.value = it
    }

    fun signUp() {
        Log.d("VIEW MODEL", "signUp: email:$email and password:$password and confirmPassword:$confirmPassword")
    }

    fun navigateSignIn(navScreen: (String) -> Unit) {
        navScreen(Screen.SignIn.route)
    }
}