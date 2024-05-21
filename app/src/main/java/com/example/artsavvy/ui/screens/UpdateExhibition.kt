package com.example.artsavvy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.artsavvy.ui.components.TopBar
import com.example.artsavvy.viewmodel.ExhibitionViewModel

@Composable
fun UpdateExhibition(navController: NavController, exhibitionId: String, exhibitionViewModel: ExhibitionViewModel = viewModel()) {
    val exhibitionDetails by exhibitionViewModel.exhibitionDetails.observeAsState()

    val name = remember(exhibitionDetails) { mutableStateOf(exhibitionDetails?.name ?: "") }
    val description = remember(exhibitionDetails) { mutableStateOf(exhibitionDetails?.description ?: "") }
    val start = remember(exhibitionDetails) { mutableStateOf(exhibitionDetails?.start ?: "") }
    val end = remember(exhibitionDetails) { mutableStateOf(exhibitionDetails?.end ?: "") }
    val imgUrl = remember(exhibitionDetails) { mutableStateOf(exhibitionDetails?.imgUrl ?: "") }

    LaunchedEffect(exhibitionId) {
        exhibitionViewModel.getExhibitionById(exhibitionId)
    }

    Scaffold(
        topBar = {
            TopBar("edit_exhibition", navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            imgUrl.value.let {
                if (it.isNotEmpty()) {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Exhibition Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 16.dp)
                    )
                }
            }

            Text("Editar Exposição", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = name.value, onValueChange = { name.value = it }, label = "Título")
            CustomTextField(value = description.value, onValueChange = { description.value = it }, label = "Descrição")
            CustomTextField(value = start.value, onValueChange = { start.value = it }, label = "Data de Início")
            CustomTextField(value = end.value, onValueChange = { end.value = it }, label = "Data de Término")
            CustomTextField(value = imgUrl.value, onValueChange = { imgUrl.value = it }, label = "Link da Imagem")
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = {
                    exhibitionViewModel.updateExhibition(exhibitionId, mapOf(
                        "name" to name.value,
                        "description" to description.value,
                        "start" to start.value,
                        "end" to end.value,
                        "imgUrl" to imgUrl.value
                    ))
                    navController.popBackStack()
                }) {
                    Text("Salvar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    navController.popBackStack()
                }) {
                    Text("Cancelar")
                }
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
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}
