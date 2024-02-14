package com.example.noteapp.di

import android.content.Context
import com.example.noteapp.services.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GoogleAuthModule {

    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(@ApplicationContext appContext: Context): GoogleAuthUiClient {
        return GoogleAuthUiClient(
            context = appContext,
            oneTapClient = Identity.getSignInClient(appContext)
            )
    }


}