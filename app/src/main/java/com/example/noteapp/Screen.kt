package com.example.noteapp

const val DOCUMENT_SCREEN_ARGUMENT_ID = "docId"
const val SHARE_SCREEN_ARGUMENT_ID = "share_doc_id"
const val MANAGE_ACCESS_SCREEN_ARGUMENT_ID = "manage_access_doc_id"

sealed class Screen(val route: String) {

    object SignIn : Screen(route = "sign_in_screen")
    object Share :
        Screen(route = "share_in_screen?$SHARE_SCREEN_ARGUMENT_ID={$SHARE_SCREEN_ARGUMENT_ID}") {
        fun setId(documentId: String = ""): String =
            "share_in_screen?$SHARE_SCREEN_ARGUMENT_ID=$documentId"
    }

    object ManageAccess :
        Screen(route = "manage_access_screen?$MANAGE_ACCESS_SCREEN_ARGUMENT_ID={$MANAGE_ACCESS_SCREEN_ARGUMENT_ID}") {
        fun setId(documentId: String = ""): String =
            "manage_access_screen?$MANAGE_ACCESS_SCREEN_ARGUMENT_ID=$documentId"
    }


    object DocumentList : Screen(route = "document_list_scree")
    object Document :
        Screen(route = "document_screen?$DOCUMENT_SCREEN_ARGUMENT_ID={$DOCUMENT_SCREEN_ARGUMENT_ID}") {
        fun setId(documentId: String = ""): String =
            "document_screen?$DOCUMENT_SCREEN_ARGUMENT_ID=$documentId"
    }
}

enum class Permission() {
    ALL, READ, WRITE
}