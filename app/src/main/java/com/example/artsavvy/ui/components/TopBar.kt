package com.example.artsavvy.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.artsavvy.R
import com.example.artsavvy.di.AppModule
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
import com.example.artsavvy.model.Exhibition
import com.example.artsavvy.viewmodel.ArtViewModel
import com.example.artsavvy.viewmodel.ExhibitionViewModel
import com.example.artsavvy.viewmodel.TopBarViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    routeName: String,
    navController: NavController,
    exhibitionId: String? = null,
    onSearchResults: (List<Any>) -> Unit,
    onShowQRCode: (() -> Unit)? = null
) {
    val text = when (routeName) {
        "home" -> "Exposições"
        "exhibition" -> "Obras em Exposição"
        "QRCode da Obra" -> routeName
        "Detalhes da Obra" -> routeName
        else -> ""
    }
    var showSearchBar by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val topBarViewModel = remember { TopBarViewModel() }
    val isAdmin by topBarViewModel.isAdmin.collectAsState()

    val artManager: ArtManager = AppModule.provideArtManager(AppModule.provideFirebaseDatabase())
    val artViewModel = remember { ArtViewModel(artManager) }

    val exhibitionViewModel = remember { ExhibitionViewModel() }
    var isLoading by remember { mutableStateOf(true) }

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
                if (!showSearchBar) {
                    if (routeName == "home") {
                        showLogoutDialog = true
                    } else {
                        navController.popBackStack()
                    }
                } else {
                    showSearchBar = false
                    if (routeName == "home") {
                        navController.popBackStack()
                        navController.navigate("home")
                    } else if (routeName == "exhibition") {
                        navController.popBackStack()
                        navController.navigate("exhibition/$exhibitionId")
                    }
                }
            }, modifier = Modifier.size(48.dp)) {
                if (showSearchBar) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar Pesquisa",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.botao_voltar),
                        contentDescription = "Voltar",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
            if (!showSearchBar) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = text,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(Modifier.weight(1f))
                if (isAdmin && (routeName == "exhibition" || routeName == "home")) {
                    IconButton(
                        onClick = {
                            if (routeName == "exhibition") {
                                navController.navigate("add_artwork/$exhibitionId")
                            } else if (routeName == "home") {
                                navController.navigate("add_exhibition")
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.plus_icon),
                            contentDescription = "Adicionar obra"
                        )
                    }
                }
                IconButton(onClick = {
                    if (routeName == "Detalhes da Obra") {
                        onShowQRCode?.invoke()
                    } else {
                        navController.navigate("qr_code_scanner")
                    }
                }, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.qrcode_scanner),
                        contentDescription = "Scan QR Code",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
                if (routeName == "home" || routeName == "exhibition") {
                    IconButton(onClick = { showSearchBar = !showSearchBar }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                }
            } else {
                SearchBar(onSearch = { query ->
                    if (routeName == "home") {
                        exhibitionViewModel.searchExhibitions(query) { results ->
                            onSearchResults(results)
                        }
                    } else if (routeName == "exhibition") {
                        exhibitionId?.let {
                            artViewModel.searchArts(query, it) { results ->
                                onSearchResults(results)
                            }
                        }
                    }
                })
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
private fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
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
fun SearchBar(onSearch: (String) -> Unit) {
    var query by remember { mutableStateOf("") }

    val searchJob = remember { mutableStateOf<Job?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.Gray, CircleShape)
    ) {
        BasicTextField(
            value = query,
            onValueChange = {
                query = it
                searchJob.value?.cancel()
                searchJob.value = CoroutineScope(Dispatchers.Main).launch {
                    onSearch(query)
                }
            },
            singleLine = true,
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = CircleShape)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            decorationBox = { innerTextField ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    innerTextField()

                    IconButton(onClick = { onSearch(query) }) {
                        Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                    }
                }
            }
        )
    }
}






