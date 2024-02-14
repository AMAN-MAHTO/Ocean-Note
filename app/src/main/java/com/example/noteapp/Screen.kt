package com.example.noteapp


sealed class Screen(val route:String) {
    object SignUp: Screen(route = "sign_up_screen")
    object SignIn: Screen(route = "sign_in_screen")
    object Note : Screen(route = "note_screen")
    object Home: Screen(route = "home_screen")
}