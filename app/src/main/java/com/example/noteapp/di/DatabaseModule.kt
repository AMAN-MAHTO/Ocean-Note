package com.example.noteapp.di

import com.example.noteapp.auth.data.FirebaseUserDatabaseClientImpl
import com.example.noteapp.note.domain.repository.DatabaseClient
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.repository.UserDatabaseClient
import com.example.noteapp.note.data.FirebaseDatabaseClientImpl
import com.example.noteapp.note.data.FirebaseDatabaseClientImpl2
import com.example.noteapp.note.domain.repository.DatabaseClient2
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun getFirebaseDatabaseClient(googleAuthUiClient: GoogleAuthUiClient): DatabaseClient {
        return FirebaseDatabaseClientImpl(
            googleAuthUiClient = googleAuthUiClient,
            db = Firebase.firestore
        )
    }
    @Provides
    @Singleton
    fun getFirebaseDatabaseClient2(googleAuthUiClient: GoogleAuthUiClient): DatabaseClient2 {
        return FirebaseDatabaseClientImpl2(
            googleAuthUiClient = googleAuthUiClient,
            db = Firebase.firestore
        )
    }

    @Provides
    @Singleton
    fun getFirebaseUserDatabaseClient(googleAuthUiClient: GoogleAuthUiClient): UserDatabaseClient {
        return FirebaseUserDatabaseClientImpl(
            googleAuthUiClient = googleAuthUiClient,
            db = Firebase.firestore
        )
    }

}