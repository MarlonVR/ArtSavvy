package com.example.artsavvy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.R

class Exhibition {

    companion object{
        @Composable
        fun Screen(navController: NavController) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    TopBar(backToHome = { navController.navigate("home") })
                    SearchBar()
                    ArtList()
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
        private fun ArtList() {
            val artPieces = listOf(
                "Obra 01", "Obra 02", "Obra 03", "Obra 04", "Obra 05", "Obra 06"
            )

            LazyColumn {
                items(artPieces.chunked(2)) { rowPieces ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        for (art in rowPieces) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .height(150.dp)
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(art, color = Color.White)
                            }
                        }
                        if (rowPieces.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

    }
}