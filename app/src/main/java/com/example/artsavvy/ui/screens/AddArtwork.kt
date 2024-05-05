package com.example.artsavvy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.di.AppModule.provideArtManager
import com.example.artsavvy.di.AppModule.provideFirebaseDatabase
import com.example.artsavvy.model.Art
import com.example.artsavvy.ui.components.TopBar

@Composable
fun AddArtworkScreen(navController: NavController, exhibitionId: String) {
    var artworkName by remember { mutableStateOf("") }
    var artworkAuthor by remember { mutableStateOf("") }
    var artworkImageUrl by remember { mutableStateOf("") }
    var artworkDescription by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(routeName = "add_artwork", navController = navController, exhibitionId = exhibitionId)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Adicionar Nova Obra", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colors.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            CustomTextField(
                value = artworkName,
                onValueChange = { artworkName = it },
                label = "Nome da Obra"
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = artworkAuthor,
                onValueChange = { artworkAuthor = it },
                label = "Autor da Obra"
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = artworkImageUrl,
                onValueChange = { artworkImageUrl = it },
                label = "Link da Imagem"
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = artworkDescription,
                onValueChange = { artworkDescription = it },
                label = "Descrição da Obra"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (artworkName.isBlank() || artworkAuthor.isBlank() || artworkImageUrl.isBlank() || artworkDescription.isBlank()) {
                    errorMessage = "Todos os campos devem ser preenchidos."
                } else {
                    addArtwork(artworkName, artworkAuthor, artworkImageUrl, artworkDescription, exhibitionId)
                    navController.popBackStack()
                }
            }) {
                Text("Adicionar Obra")
            }
        }
    }
}

@Composable
private fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    )
}

private fun addArtwork(name: String, author: String, imageUrl: String, description: String, exhibitionId: String) {
    val newArt = Art(
        name = name,
        author = author,
        imageUrl = imageUrl,
        description = description,
        exhibitionId = exhibitionId
    )

    provideArtManager(provideFirebaseDatabase()).addArt(newArt)
}
