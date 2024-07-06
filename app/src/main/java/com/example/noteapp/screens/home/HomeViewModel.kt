package com.example.noteapp.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.Sync
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.Screen
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.note.domain.repository.DatabaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    googleAuthUiClient: GoogleAuthUiClient,
    databaseClient: DatabaseClient
) : ViewModel(){

    private val _userData = googleAuthUiClient.getSignedInUser()

    private val _navigationItems = listOf(
        NavigationItem(
            title = "Notes",
            unselectedIcon = Icons.Outlined.Notes,
            selectedIcon = Icons.Filled.Notes,
            onItemClick = {

            }
        ),
        NavigationItem(
            title = "Add Note",
            unselectedIcon = Icons.Outlined.Add,
            selectedIcon = Icons.Filled.Add,
            onItemClick = {

            }
        ),
        NavigationItem(
            title = "Sync Notes",
            unselectedIcon = Icons.Outlined.Sync,
            selectedIcon = Icons.Filled.Sync,
            onItemClick = {

            }
        ),
        NavigationItem(
            title = "Rate Us",
            unselectedIcon = Icons.Outlined.StarRate,
            selectedIcon = Icons.Filled.StarRate,
            onItemClick = {

            }
        ),NavigationItem(
            title = "Share App",
            unselectedIcon = Icons.Outlined.Share,
            selectedIcon = Icons.Filled.Share,
            onItemClick = {

            }
        ),
        NavigationItem(
            title = "Logout",
            unselectedIcon = Icons.Outlined.Logout,
            selectedIcon = Icons.Filled.Logout,
            onItemClick = {
                viewModelScope.launch {
                    googleAuthUiClient.signOut()
                    it.popBackStack()
                    it.navigate(Screen.SignIn.route)
                }
            }
        )

    )

    fun getNavigationItems(): List<NavigationItem> {
        return _navigationItems
    }

    fun getUserData(): UserData? {
        return _userData
    }


}