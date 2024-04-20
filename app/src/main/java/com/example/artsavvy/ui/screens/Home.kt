package com.example.artsavvy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.R

class Home {

    companion object{
        @Composable
        fun Screen(navController: NavController) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ){
                        IconButton(
                            onClick = { navController.navigate("login") },
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.botao_voltar),
                                contentDescription = "Botão de voltar a página",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        IconButton(
                            onClick = { /* TODO: Handle play action */ },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.botao_play),
                                contentDescription = "Botão de reproduzir audio",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.centro_cultural),
                            contentDescription = "Gallery Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(
                        onClick = { navController.navigate("exhibition") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                    ) {
                        Text("Ver Catálogo de Obras")
                    }
                }
            }
        }
    }
}