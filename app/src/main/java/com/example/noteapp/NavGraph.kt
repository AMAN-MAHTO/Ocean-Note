package com.example.noteapp

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bookofgiants.screens.sign_up.SignUp
import com.example.noteapp.screens.home.Home
import com.example.noteapp.screens.note.Note
import com.example.noteapp.auth.presentation.sign_in.SignIn
import com.example.noteapp.auth.presentation.sign_in.SignInViewModel
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.repository.UserDatabaseClient
import com.example.noteapp.note.presentation.DocumentListScreen
import com.example.noteapp.note.presentation.DocumentScreen
import kotlin.math.log


@Composable
fun NavGraph(navHostController: NavHostController,
             googleAuthUiClient: GoogleAuthUiClient,
             signInViewModel: SignInViewModel = hiltViewModel(),
             mainViewModel: MainViewModel = hiltViewModel()
) {
    val startDestination = mainViewModel.startDestinatioon

    val state by signInViewModel.state.collectAsState()
    // to send the intent, we get from IntentSender
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {result->
            if(result.resultCode == RESULT_OK){
                mainViewModel.passIntentToGoogleAuthSignInWithIntent(result.data,googleAuthUiClient,signInViewModel)

            }

        })
    NavHost(
        navController = navHostController,
        startDestination = mainViewModel.startDestinatioon.value
    ) {


        composable(
            Screen.SignIn.route
        ){


            // if the google sign_in is successful
            LaunchedEffect(key1 = state.isSignInSuccessful){
                if(state.isSignInSuccessful){
                    Log.d("SignIn", "NavGraph: navigating, signInsuccessful")
                    navHostController.popBackStack()
                    navHostController.navigate(Screen.DocumentList.route)
                    signInViewModel.resetState()
                    }

            }



            SignIn(
                onGoogleSignIn = {
                    mainViewModel.googleSignIntentLauncher(launcher, googleAuthUiClient)
                },
                onClickSignUpText = {
                    navHostController.popBackStack()
                    navHostController.navigate(it)
                }
            )
        }
        composable(
            Screen.SignUp.route
        ){
            // if the google sign_in is successful
            // if the google sign_in is successful
            LaunchedEffect(key1 = state.isSignInSuccessful){
                if(state.isSignInSuccessful){
                    Log.d("SignIn", "NavGraph: navigating, signInsuccessful")
                    navHostController.popBackStack()
                    navHostController.navigate(Screen.DocumentList.route)
                    signInViewModel.resetState()
                }

            }
//            LaunchedEffect(key1 = state.isSignInSuccessful){
//                if(state.isSignInSuccessful){
//                    if (state.isNewUser){
//                        Log.d("SignIn", "NavGraph: createCopyUser call")
////                        signInViewModel.createCopyUserOnSignUp()
//                            navHostController.popBackStack()
//                            navHostController.navigate(Screen.DocumentList.route)
//                            signInViewModel.resetState()
//
//                    }else{
//                    navHostController.popBackStack()
//                    navHostController.navigate(Screen.Home.route)
//                    signInViewModel.resetState()
//                    }
//                }
//            }
            SignUp(
                onGoogleSignIn = {
                    mainViewModel.googleSignIntentLauncher(launcher, googleAuthUiClient)
                },
                onClickSignInText = {
                navHostController.popBackStack()
                navHostController.navigate(it)
            }
            )
        }

        composable(
            Screen.EditNote.route,
            arguments = listOf(navArgument(EDIT_NOTE_ARGUMENT_ID){defaultValue = ""})

        ){
            // by default this argument get stored at two place NavBackStackEntry and SavedStateHandle
            // thourgh SavedSateHandle we can access this argument is viewModel
            Note(onEditBackIconClickNavigation = {
                navHostController.popBackStack()
                navHostController.navigate(Screen.Home.route)
            })
        }

        composable(
            Screen.Home.route
        ){
            Home(navHostController = navHostController,
                onNoteListTileClick = {
                    navHostController.navigate(Screen.EditNote.setId(it))
                },
                onClickAddNewNote = {
                    navHostController.navigate(Screen.EditNote.setId())
                }
            )
        }

        composable(
            Screen.Document.route,
            arguments = listOf( navArgument(DOCUMENT_SCREEN_ARGUMENT_ID){defaultValue = ""})
        ){
            DocumentScreen(
                onClickBack = navHostController::popBackStack,
                navHostController = navHostController
            )
        }

        composable(
            Screen.DocumentList.route
        ){
            DocumentListScreen(navHostController = navHostController)
        }

    }

}