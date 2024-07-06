package com.example.noteapp.auth.data

import android.util.Log
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.auth.domain.repository.UserDatabaseClient
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserDatabaseClientImpl @Inject constructor(
    googleAuthUiClient: GoogleAuthUiClient,
    db: FirebaseFirestore
): UserDatabaseClient {
    var userData: UserData? = null
    var userCollectionRef:CollectionReference? = null
    init {
        userData = googleAuthUiClient.getSignedInUser()
        if(userData != null){

        userCollectionRef = db.collection("users")
        }
    }

    override suspend fun addUser(copyUser: copyUser):Boolean {
        Log.d("SignIn", "addUser: $copyUser")
        try {
            userCollectionRef?.let {
                val id=    it.add(copyUser).await().id
                Log.d("SignIn", "userCollection copy user id: $id")
                return true
            }
        }catch (e: Exception){
            Log.d("SignIn", "addUser: ERROR $e")
            return false
        }
        return false
    }
}