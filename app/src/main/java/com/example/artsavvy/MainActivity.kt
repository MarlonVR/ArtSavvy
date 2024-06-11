package com.example.artsavvy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.artsavvy.navigation.ArtSavvyApp
import com.example.artsavvy.ui.theme.ArtSavvyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TTSManager.initialize(this)
        setContent {
            ArtSavvyTheme {
                ArtSavvyApp.NavController()
            }
        }
    }
}