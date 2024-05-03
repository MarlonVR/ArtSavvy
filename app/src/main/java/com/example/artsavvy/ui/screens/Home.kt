package com.example.artsavvy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.model.Exhibition
import com.example.artsavvy.ui.components.TopBar
import com.example.artsavvy.viewmodel.ExhibitionViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter

class Home {

    companion object{
        @Composable
        fun Screen(navController: NavController, viewModel: ExhibitionViewModel = viewModel()) {
            val exhibitions = viewModel.exhibitions.observeAsState(listOf())

            LaunchedEffect(Unit) {
                viewModel.loadExhibitions()
            }

            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    TopBar("home", navController)
                    if (exhibitions.value.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                            Text("Carregando...", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(top = 8.dp)) {
                            items(exhibitions.value) { exhibition ->
                                val exhibitionId = exhibition.id
                                ExhibitionCard(exhibition, onClick = { navController.navigate("exhibition/$exhibitionId") })
                            }
                        }
                    }
                }
            }
        }

        private val LightGray = Color(0xFFF5F5F5)
        @Composable
        fun ExhibitionCard(exhibition: Exhibition, onClick: () -> Unit) {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp,
                backgroundColor = LightGray
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = rememberImagePainter(data = exhibition.imgUrl),
                        contentDescription = "Exhibition Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = exhibition.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = exhibition.description, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "De ${exhibition.start} até ${exhibition.end}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
