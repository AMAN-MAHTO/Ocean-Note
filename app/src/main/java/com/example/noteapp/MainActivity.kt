package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.repository.UserDatabaseClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {



    lateinit var navController : NavHostController

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient



    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //splash screen setup
//        installSplashScreen().apply {
//            setKeepOnScreenCondition{
//                //this laamda keep the splash screen, till the value is true
//                !mainViewModel.isReady.value
//            }
//        }

        setContent {
            AppTheme {

                navController = rememberNavController()
                NavGraph(navHostController = navController,googleAuthUiClient)
            }
        }

    }
}

