package com.example.noteapp.screens.home

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController


data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val onItemClick: (navController: NavController)->Unit
)