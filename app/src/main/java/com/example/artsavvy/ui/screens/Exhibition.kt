package com.example.artsavvy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.artsavvy.viewmodel.ArtViewModel
import coil.compose.rememberImagePainter
import com.example.artsavvy.di.AppModule
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
import com.example.artsavvy.ui.components.ArtCard
import com.example.artsavvy.ui.components.TopBar

class Exhibition {

    companion object{
        @Composable
        fun Screen(navController: NavController, exhibitionId: String) {
            val artManager: ArtManager = AppModule.provideArtManager(AppModule.provideFirebaseDatabase())
            val artViewModel = remember { ArtViewModel(artManager) }
            val artPieces by artViewModel.artPieces.observeAsState(initial = emptyList())
            val isAdmin by artViewModel.isAdmin.observeAsState(initial = false)
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(exhibitionId) {
                artViewModel.loadArtsForExhibition(exhibitionId) {
                    isLoading = false  // Atualiza o estado de carregamento após os dados serem carregados
                }
            }

            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    TopBar(routeName = "exhibition", navController = navController, exhibitionId)
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (artPieces.isEmpty()) {
                        Text(
                            text = "Por enquanto sem obras",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onBackground
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(artPieces, key = { art -> art.id }) { art ->
                                ArtCard(
                                    art = art,
                                    isAdmin = isAdmin,
                                    onDelete = {
                                        artViewModel.removeArt(art.id)
                                        navController.navigate("exhibition/$exhibitionId")
                                    },
                                    onEdit = {
                                        navController.navigate("edit_art/${art.id}")
                                    },
                                    onClick = {
                                        navController.navigate("art_details/${art.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun UpdateArt(navController: NavController, artId: String) {
            val artManager: ArtManager = AppModule.provideArtManager(AppModule.provideFirebaseDatabase())
            val artViewModel = remember { ArtViewModel(artManager) }
            var art by remember { mutableStateOf<Art?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(artId) {
                artViewModel.getArtById(artId) { fetchedArt ->
                    art = fetchedArt
                    isLoading = fetchedArt == null
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopBar(routeName = "updateArt", navController = navController)
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (art != null) {
                        var name by remember(art) { mutableStateOf(art!!.name) }
                        var author by remember(art) { mutableStateOf(art!!.author) }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberImagePainter(data = art!!.imageUrl),
                                contentDescription = "Obra de Arte",
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(250.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nome da Obra") },
                                leadingIcon = { Icon(Icons.Default.Create, contentDescription = "Nome") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = author,
                                onValueChange = { author = it },
                                label = { Text("Autor da Obra") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Autor") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    artViewModel.editArt(artId, Art(id = artId, name = name, author = author))
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF87CEFA)),
                                modifier = Modifier.padding(horizontal = 40.dp)
                            ) {
                                Text("Salvar Alterações")
                            }
                        }
                    } else {
                        Text("Detalhes da obra não encontrados.", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}