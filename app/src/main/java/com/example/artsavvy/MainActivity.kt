package com.example.artsavvy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
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
        name = "The Starry Night",
        author = "Vincent van Gogh",
        exhibitionId = "exhibition1",
        imageUrl = "https://imgs.search.brave.com/dr6SvW3KB8OUVz0l9G0MI6LL5bo-O9ikhrvLTdRs8eQ/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9hcnRj/ZXRlcmEuYXJ0L3dw/LWNvbnRlbnQvdXBs/b2Fkcy8yMDIyLzAz/L3RpcG9zLWRlLWFy/dGVzLXZpc3VhaXMt/cG9sbG9jay0xLmpw/Zw"
    )
    val database = FirebaseDatabase.getInstance()
    val artManager = ArtManager(database)

    artManager.addArt(newArt)
}
