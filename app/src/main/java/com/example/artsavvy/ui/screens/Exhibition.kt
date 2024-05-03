package com.example.artsavvy.ui.screens

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.viewmodel.ArtViewModel
import coil.compose.rememberImagePainter
import com.example.artsavvy.di.AppModule
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
import com.example.artsavvy.ui.components.TopBar

class Exhibition {

    companion object{
        @Composable
        fun Screen(navController: NavController, exhibitionId: String) {
            val artManager: ArtManager = AppModule.provideArtManager(AppModule.provideFirebaseDatabase())
            val artViewModel = remember { ArtViewModel(artManager) }

            LaunchedEffect(exhibitionId) {
                artViewModel.loadArtsForExhibition(exhibitionId)
            }

            val artPieces by artViewModel.artPieces.observeAsState(initial = emptyList())
            val isAdmin by artViewModel.isAdmin.observeAsState(initial = false)

            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    TopBar("exhibition", navController)
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(artPieces, key = { art -> art.id }) { art ->
                            ArtCard(art, isAdmin) {
                                artViewModel.removeArt(art.id)
                                navController.navigate("exhibition/$exhibitionId")
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun ArtCard(art: Art, isAdmin: Boolean, onDelete: () -> Unit) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    val painter = rememberImagePainter(data = art.imageUrl)
                    Image(
                        painter = painter,
                        contentDescription = "Obra de Arte",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Text(
                        text = art.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = art.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    if (isAdmin) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            EditableTextLink(text = "Editar", onClick = { /* TODO: Implementar edição */ })
                            EditableTextLink(text = "Excluir", onClick = onDelete)
                        }
                    }
                }
            }
        }

        @Composable
        fun EditableTextLink(text: String, onClick: () -> Unit) {
            val annotatedText = buildAnnotatedString {
                pushStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.None))
                append(text)
                pop()
            }
            ClickableText(
                text = annotatedText,
                onClick = { onClick() },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}