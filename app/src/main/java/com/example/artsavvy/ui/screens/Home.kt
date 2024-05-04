package com.example.artsavvy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.ui.components.TopBar
import com.example.artsavvy.viewmodel.ExhibitionViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artsavvy.ui.components.ExhibitionCard

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

    }
}
