package com.example.noteapp

const val EDIT_NOTE_ARGUMENT_ID ="id"

sealed class Screen(val route:String) {
    object SignUp: Screen(route = "sign_up_screen")
    object SignIn: Screen(route = "sign_in_screen")
    object EditNote : Screen(route = "note_screen?$EDIT_NOTE_ARGUMENT_ID={$EDIT_NOTE_ARGUMENT_ID}"){
        fun setId(id: String = ""): String {
            return "note_screen?$EDIT_NOTE_ARGUMENT_ID=$id"
        }
    }
    object Home: Screen(route = "home_screen")
}