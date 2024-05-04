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


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ArtSavvyTheme {
        Login()
    }
}

fun testAddArt() {
    val newArt = Art(
        name = "wgweggwegwgegege221342",
        author = "Vggwgwwgwgwerg",
        exhibitionId = "-Nwzyybx5zJmy6UY8Dcd",
        imageUrl = "https://imgs.search.brave.com/MwnZnbqEqfTT1uUwg2YLPEMfV30ARMZ9vsR6Q0CZj5I/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5nZXR0eWltYWdl/cy5jb20vaWQvMjY2/MzQyNi9waG90by9m/cmVkZXJpYy1jaG9w/aW4tcG9saXNoLWNv/bXBvc2VyLmpwZz9z/PTYxMng2MTImdz0w/Jms9MjAmYz1EdThk/bGlYZEhxbW02Vk9M/SHZyaUxxTkl1bk1P/X0MwYjNvU05yMTVj/Sk5BPQ"
    )
    val database = FirebaseDatabase.getInstance()
    val artManager = ArtManager(database)

    artManager.addArt(newArt)
}



