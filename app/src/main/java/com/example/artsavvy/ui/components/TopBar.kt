package com.example.artsavvy.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.artsavvy.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TopBar(routeName: String, navController: NavController) {
    val text = when (routeName) {
        "home" -> "Exposições"
        "exhibition" -> "Obras em Exposição"
        else -> ""
    }
    var showSearchBar by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                if (routeName == "home") {
                    showLogoutDialog = true
                } else {
                    navController.popBackStack()
                }
            }, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.botao_voltar),
                    contentDescription = "Voltar",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colors.onBackground
                )
            }
            Spacer(Modifier.weight(1f))
            if (!showSearchBar) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            } else {
                SearchBar { showSearchBar = false }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {/* TOCAR NARRAÇÃO*/}, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colors.onBackground
                )
            }
            IconButton(onClick = { showSearchBar = !showSearchBar }, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Pesquisar",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(onConfirm = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login")
        }, onDismiss = {
            showLogoutDialog = false
        })
    }
}

@Composable
fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Logout") },
        text = { Text("Deseja fazer o logout?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Sim")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Não")
            }
        }
    )
}


@Composable
private fun SearchBar(onClose: () -> Unit) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.Gray, CircleShape)
    ) {
        BasicTextField(
            value = textState,
            onValueChange = { textState = it },
            singleLine = true,
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .padding(horizontal = 20.dp, vertical = 10.dp),
            decorationBox = { innerTextField ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                    Spacer(Modifier.width(8.dp))
                    innerTextField()
                }
            }
        )
    }
}




