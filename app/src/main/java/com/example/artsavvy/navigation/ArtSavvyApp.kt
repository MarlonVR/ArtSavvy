package com.example.artsavvy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artsavvy.ui.screens.AddArtworkScreen
import com.example.artsavvy.ui.screens.AddExhibitionScreen
import com.example.artsavvy.ui.screens.ArtDetails
import com.example.artsavvy.ui.screens.Exhibition
import com.example.artsavvy.ui.screens.Home
import com.example.artsavvy.ui.screens.Login
import com.example.artsavvy.ui.screens.Register
import com.example.artsavvy.ui.screens.UpdateExhibition
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
            composable(
                route = "edit_art/{artId}",
                arguments = listOf(navArgument("artId") { type = NavType.StringType })
            ) { backStackEntry ->
                val artId = backStackEntry.arguments?.getString("artId")
                if (!artId.isNullOrBlank()) {
                    Exhibition.UpdateArt(navController, artId)
                }
            }
            composable(
                route = "edit_exhibition/{exhibitionId}",
                arguments = listOf(navArgument("exhibitionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val exhibitionId = backStackEntry.arguments?.getString("exhibitionId")
                if (!exhibitionId.isNullOrBlank()) {
                    UpdateExhibition(navController, exhibitionId)
                }
            }
            composable(
                route = "art_details/{artId}",
                arguments = listOf(navArgument("artId") { type = NavType.StringType })
            ) { backStackEntry ->
                val artId = backStackEntry.arguments?.getString("artId")
                if (!artId.isNullOrBlank()) {
                    ArtDetails.Screen(navController, artId)
                }
            }
            composable(
                route = "add_artwork/{exhibitionId}",
                arguments = listOf(navArgument("exhibitionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val exhibitionId= backStackEntry.arguments?.getString("exhibitionId")
                if (!exhibitionId.isNullOrBlank()) {
                    AddArtworkScreen(navController, exhibitionId)
                }
            }
            composable("add_exhibition") {
                AddExhibitionScreen(navController)
            }

        }
    }
}
