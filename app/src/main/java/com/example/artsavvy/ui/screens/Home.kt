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
import com.example.artsavvy.manager.TextHolder
import com.example.artsavvy.ui.components.ExhibitionCard
import com.example.artsavvy.model.Exhibition

class Home {

    companion object{
        @Composable
        fun Screen(navController: NavController, viewModel: ExhibitionViewModel = viewModel()) {
            val exhibitions by viewModel.exhibitions.observeAsState(listOf())
            val isAdmin by viewModel.isAdmin.observeAsState(initial = false)

            var searchResults by remember { mutableStateOf<List<Exhibition>>(emptyList()) }
            var isSearching by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                viewModel.loadExhibitions()
            }

            LaunchedEffect(exhibitions) {
                val introText = "Tela de exposições."
                val detailsText = buildString {
                    exhibitions.forEachIndexed { index, exhibition ->
                        append("Obra número ${index + 1}: ")
                        append("Título: ${exhibition.name}. ")
                        append("Descrição: ${exhibition.description}. ")
                        appendLine()
                    }
                }
                TextHolder.updateText(introText + detailsText)
            }


            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    TopBar(routeName = "home", navController = navController, onSearchResults = { results ->
                        searchResults = results as List<Exhibition>
                        isSearching = searchResults.isNotEmpty()
                    })

                    if (exhibitions.isEmpty() && !isSearching) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(top = 8.dp)) {
                            val itemsToShow = if (isSearching) searchResults else exhibitions
                            items(itemsToShow, key = { it.id }) { exhibition ->
                                val exhibitionId = exhibition.id
                                ExhibitionCard(
                                    exhibition = exhibition,
                                    isAdmin = isAdmin,
                                    onDelete = {
                                        viewModel.deleteExhibition(exhibition.id)
                                        navController.navigate("home")
                                    },
                                    onEdit = {
                                        navController.navigate("edit_exhibition/$exhibitionId")
                                    },
                                    onClick = {
                                        navController.navigate("exhibition/$exhibitionId")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
