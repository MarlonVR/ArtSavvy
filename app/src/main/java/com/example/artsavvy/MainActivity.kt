package com.example.artsavvy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
import com.example.artsavvy.model.Exhibition
import com.example.artsavvy.navigation.ArtSavvyApp
import com.example.artsavvy.ui.screens.Login
import com.example.artsavvy.ui.theme.ArtSavvyTheme
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSavvyTheme {
                ArtSavvyApp.NavController()
            }
        }
    }
}