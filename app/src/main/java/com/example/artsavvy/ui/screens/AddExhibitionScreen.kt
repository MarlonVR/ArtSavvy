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
import com.example.artsavvy.di.AppModule
import com.example.artsavvy.di.AppModule.provideExhibitionManager
import com.example.artsavvy.di.AppModule.provideFirebaseDatabase
import com.example.artsavvy.model.Exhibition
import com.example.artsavvy.ui.components.TopBar

@Composable
fun AddExhibitionScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var imgUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(routeName = "add_exhibition", navController = navController)
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
            Text("Adicionar Nova Exposição", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nome da Exposição"
            )
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descrição da Exposição"
            )
            CustomTextField(
                value = start,
                onValueChange = { start = it },
                label = "Data de Início (DD/MM/YYYY)"
            )
            CustomTextField(
                value = end,
                onValueChange = { end = it },
                label = "Data de Término (DD/MM/YYYY)"
            )
            CustomTextField(
                value = imgUrl,
                onValueChange = { imgUrl = it },
                label = "Link da Imagem"
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colors.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(onClick = {
                if (name.isNotBlank() && description.isNotBlank() && start.isNotBlank() && end.isNotBlank() && imgUrl.isNotBlank()) {
                    addExhibition(name, description, start, end, imgUrl)
                    navController.popBackStack()
                } else {
                    errorMessage = "Todos os campos devem ser preenchidos"
                }
            }) {
                Text("Adicionar Exposição")
            }
        }
    }
}

private fun addExhibition(name: String, description: String, start: String, end: String, imgUrl: String) {
    val newExhibition = Exhibition(
        name = name,
        description = description,
        start = start,
        end = end,
        imgUrl = imgUrl
    )

    provideExhibitionManager(provideFirebaseDatabase()).addExhibition(newExhibition)
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
