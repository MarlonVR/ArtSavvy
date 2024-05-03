package com.example.artsavvy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artsavvy.ui.screens.Exhibition
import com.example.artsavvy.ui.screens.Home
import com.example.artsavvy.ui.screens.Login
import com.example.artsavvy.ui.screens.Register
import com.google.firebase.auth.FirebaseAuth

object ArtSavvyApp {
    @Composable
    fun NavController() {
        val navController = rememberNavController()
        val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "login"

        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                Login.Screen(navController)
            }
            composable("home") {
                Home.Screen(navController)
            }
            composable("register") {
                Register.Screen(navController)
            }
            composable(
                route = "exhibition/{exhibitionId}",
                arguments = listOf(navArgument("exhibitionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val exhibitionId = backStackEntry.arguments?.getString("exhibitionId")
                if (exhibitionId != null) {
                    Exhibition.Screen(navController, exhibitionId)
                }
            }
        }
    }
}
