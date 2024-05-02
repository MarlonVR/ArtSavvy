package com.example.artsavvy.ui.screens

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.R
import com.example.artsavvy.viewmodel.ArtViewModel
import coil.compose.rememberImagePainter
import com.example.artsavvy.di.AppModule
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art

class Exhibition {

    companion object{
        @Composable
        fun Screen(navController: NavController) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    TopBar(backToHome = { navController.navigate("home") })
                    SearchBar()
                    ArtList(reloadPage = { navController.navigate("exhibition") })
                }
            }
        }

        @Composable
        private fun TopBar(backToHome: () -> Unit) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(onClick = backToHome) {
                    Image(
                        painter = painterResource(id = R.drawable.botao_voltar),
                        contentDescription = "Gallery Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Obras em Exposição",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        @Composable
        private fun SearchBar() {
            var textState = remember { mutableStateOf(TextFieldValue("")) }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                BasicTextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, CircleShape)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    decorationBox = { innerTextField ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                            Spacer(Modifier.width(8.dp))
                            innerTextField()
                        }
                    }
                )
            }
        }

        @Composable
        fun ArtList(reloadPage: () -> Unit) {
            val artManager: ArtManager = AppModule.provideArtManager(AppModule.provideFirebaseDatabase())
            val artViewModel = remember { ArtViewModel(artManager) }
            val artPieces by artViewModel.artPieces.observeAsState(initial = emptyList())
            val isAdmin by artViewModel.isAdmin.observeAsState(initial = false)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn {
                    items(artPieces, key = { art -> art.id }) { art ->
                        ArtCard(art, artManager, isAdmin){
                             artManager.removeArt(art.id)
                             reloadPage()
                        }
                    }
                }
            }
        }


        @Composable
        fun ArtCard(art: Art, artManager: ArtManager, isAdmin: Boolean, onDelete: () -> Unit) {
            Card(
                modifier = Modifier
                    .width(300.dp)
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
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            EditableTextLink(text = "Editar", onClick = { /* TODO */ })
                            EditableTextLink(text = "Excluir", onClick = { onDelete();  })
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