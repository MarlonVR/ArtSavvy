package com.example.artsavvy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.artsavvy.ui.screens.Exhibition
import com.example.artsavvy.ui.screens.Home
import com.example.artsavvy.ui.screens.Login
import com.example.artsavvy.ui.screens.Register

object ArtSavvyApp {
    @Composable
    fun NavController() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                Login.Screen(navController)
            }
            composable("home") {
                Home.Screen(navController)
            }
            composable("exhibition") {
                Exhibition.Screen(navController)
            }
            composable("register") {
                Register.Screen(navController)
            }
        }
    }
}