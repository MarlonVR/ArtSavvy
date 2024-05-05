package com.example.artsavvy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.manager.UserManager
import com.example.artsavvy.model.User
import com.example.artsavvy.ui.components.TopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class Register {
    companion object {
        @Composable
        fun Screen(navController: NavController) {
            var username by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var registrationError by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf("") }

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
                        text = "Cadastre-se no Art Savvy",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nome de Usuário") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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
                    if (registrationError) {
                        Text(
                            errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    RegisterButton(onRegister = {
                        performRegistration(email, password, username, navController) { success, message ->
                            registrationError = !success
                            errorMessage = message
                        }
                    })
                    TextButton {
                        navController.navigate("login")
                    }
                }
            }
        }

        @Composable
        private fun RegisterButton(onRegister: () -> Unit) {
            Button(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(48.dp)
            ) {
                Text("Criar Conta")
            }
        }

        @Composable
        private fun TextButton(onNavigate: () -> Unit) {
            OutlinedButton(
                onClick = onNavigate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Já tem uma conta? Faça o login")
            }
        }

        private fun performRegistration(email: String, password: String, username: String, navController: NavController, onResult: (Boolean, String) -> Unit) {
            if (email.isBlank() || password.isBlank() || username.isBlank()) {
                onResult(false, "Nome de usuário, email e senha não podem ser vazios")
                return
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        if (firebaseUser != null) {
                            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()
                            firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    val user = User(
                                        id = firebaseUser.uid,
                                        email = email,
                                        admin = false
                                    )
                                    UserManager(FirebaseDatabase.getInstance()).addUser(firebaseUser.uid, user)
                                    navController.navigate("home")
                                    onResult(true, "")
                                } else {
                                    onResult(false, "Falha ao atualizar o perfil do usuário")
                                }
                            }
                        } else {
                            onResult(false, "Erro ao obter informações do usuário")
                        }
                    } else {
                        onResult(false, task.exception?.localizedMessage ?: "Falha no cadastro")
                    }
                }
        }

    }
}
