package com.example.noteapp.auth.data

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.noteapp.R
import com.example.noteapp.auth.domain.model.SignInResult
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.auth.domain.model.copyUser
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.sign

class GoogleAuthUiClient (
    private val context: Context,
    private val oneTapClient: SignInClient,
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent, listener: (SignInResult)->Unit) {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        var signInResult = SignInResult(null, null, null)
        try {
             auth.signInWithCredential(googleCredentials)
                .addOnCompleteListener {
                if(it.isSuccessful){
                    val user = it.result.user
                    val additionUserInfo = it.result.additionalUserInfo
                     signInResult=  SignInResult(
                        data = user?.run {
                            UserData(
                                userId = uid,
                                username = displayName,
                                email = email,
                                profilePictureUrl = photoUrl?.toString()
                            )
                        },
                        isNewUser = additionUserInfo?.isNewUser,
                        errorMessage = null
                    )
                    listener(signInResult)
                }
            }


        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
             signInResult = SignInResult(
                data = null,
                isNewUser = null,
                errorMessage = e.message
            )
            listener(signInResult)
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
            
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            email = email,
            profilePictureUrl = photoUrl?.toString()
        )
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}