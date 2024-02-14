package com.example.noteapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.services.DatabaseClient
import com.example.noteapp.services.GoogleAuthUiClient
import com.example.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {



    lateinit var navController : NavHostController

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    @Inject
    lateinit var databaseClient: DatabaseClient

    val mainViewModel: MainViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //splash screen setup
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                //this laamda keep the splash screen, till the value is true
                !mainViewModel.isReady.value
            }
        }

        setContent {
            NoteAppTheme {

                navController = rememberNavController()
                NavGraph(navController = navController,googleAuthUiClient)
            }
        }

    }
}

