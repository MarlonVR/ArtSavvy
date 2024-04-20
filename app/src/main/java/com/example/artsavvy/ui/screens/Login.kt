package com.example.artsavvy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.R

class Login {
    companion object {
        @Composable
        fun Screen(navController: NavController) {
            var pin by remember { mutableStateOf("") }
            var username by remember { mutableStateOf("") }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = "Art Savvy",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    TextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text("PIN de Acesso") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nome de Usuário") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    LoginButton(onClick = { navController.navigate("home") })
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        @Composable
        private fun LoginButton(onClick: () -> Unit) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .clickable(
                        onClick = onClick,
                        indication = rememberRipple(bounded = false, color = Color.White),
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.botao_login),
                    contentDescription = "Login Button"
                )
            }
        }
    }
}