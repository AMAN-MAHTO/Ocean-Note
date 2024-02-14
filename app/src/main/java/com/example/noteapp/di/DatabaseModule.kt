package com.example.noteapp.di

import com.example.noteapp.services.DatabaseClient
import com.example.noteapp.services.GoogleAuthUiClient
import com.example.noteapp.services.impl.FirebaseDatabaseClientImpl
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
    fun getFirebaseDatabaseClient(googleAuthUiClient: GoogleAuthUiClient): DatabaseClient{
        return FirebaseDatabaseClientImpl(
            googleAuthUiClient = googleAuthUiClient,
            db = Firebase.firestore
        )
    }

}