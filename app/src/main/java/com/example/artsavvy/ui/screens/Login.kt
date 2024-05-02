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
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.R
import com.google.firebase.auth.FirebaseAuth

class Login {
    companion object {
        @Composable
        fun Screen(navController: NavController) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var loginError by remember { mutableStateOf(false) }

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
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Senha") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (loginError) {
                        Text(
                            "Email ou senha incorretos. Tente novamente.",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LoginButton(onLogin = {
                        performLogin(email, password, navController) { success ->
                            loginError = !success
                        }
                    })
                }
            }
        }

        @Composable
        private fun LoginButton(onLogin: () -> Unit) {
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(48.dp)
            ) {
                Text("Login")
            }
        }


        private fun performLogin(email: String, password: String, navController: NavController, onResult: (Boolean) -> Unit) {
            if (email.isBlank() || password.isBlank()) {
                onResult(false) // Não realizar login se e-mail ou senha estiverem vazios, pra evitar crashar o app
                return
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true)
                        navController.navigate("home")
                    } else {
                        onResult(false)
                    }
                }
        }

    }
}