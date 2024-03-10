package com.example.noteapp

import android.app.Activity.RESULT_OK
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
import com.example.noteapp.screens.sign_in.SignIn
import com.example.noteapp.screens.sign_in.SignInViewModel
import com.example.noteapp.services.GoogleAuthUiClient



@Composable
fun NavGraph(navController: NavHostController,
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
        navController = navController,
        startDestination = mainViewModel.startDestinatioon.value
    ) {


        composable(
            Screen.SignIn.route
        ){


            // if the google sign_in is successful
            LaunchedEffect(key1 = state.isSignInSuccessful){
                if(state.isSignInSuccessful){
                    navController.popBackStack()
                    navController.navigate(Screen.Home.route)
                    signInViewModel.resetState()
                }
            }


            SignIn(
                onGoogleSignIn = {
                    mainViewModel.googleSignIntentLauncher(launcher, googleAuthUiClient)
                },
                onClickSignUpText = {
                    navController.popBackStack()
                    navController.navigate(it)
                }
            )
        }
        composable(
            Screen.SignUp.route
        ){
            // if the google sign_in is successful
            LaunchedEffect(key1 = state.isSignInSuccessful){
                if(state.isSignInSuccessful){
                    navController.popBackStack()
                    navController.navigate(Screen.Home.route)
                    signInViewModel.resetState()
                }
            }
            SignUp(
                onGoogleSignIn = {
                    mainViewModel.googleSignIntentLauncher(launcher, googleAuthUiClient)
                },
                onClickSignInText = {
                navController.popBackStack()
                navController.navigate(it)
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
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            })
        }

        composable(
            Screen.Home.route
        ){
            Home(navController = navController,
                onNoteListTileClick = {
                    navController.navigate(Screen.EditNote.setId(it))
                },
                onClickAddNewNote = {
                    navController.navigate(Screen.EditNote.setId())
                }
            )
        }

    }

}