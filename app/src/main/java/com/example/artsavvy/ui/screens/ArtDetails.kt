package com.example.artsavvy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.artsavvy.di.AppModule
import com.example.artsavvy.di.AppModule.provideCommentsManager
import com.example.artsavvy.di.AppModule.provideFirebaseDatabase
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
import com.example.artsavvy.ui.components.TopBar
import com.example.artsavvy.viewmodel.ArtViewModel
import com.example.artsavvy.model.Comment
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.Duration
import androidx.compose.ui.Alignment

class ArtDetails {
    companion object{
        @Composable
        fun Screen(navController: NavController, artId: String) {
            val artManager: ArtManager = AppModule.provideArtManager(AppModule.provideFirebaseDatabase())
            val artViewModel = remember { ArtViewModel(artManager) }

            var art by remember { mutableStateOf<Art?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(artId) {
                artViewModel.getArtById(artId) { fetchedArt ->
                    art = fetchedArt
                    isLoading = fetchedArt == null
                }
                artViewModel.loadCommentsForArt(artId)
            }

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (art != null) {
                    ArtDetailsView(art!!, navController, artViewModel)
                } else {
                    Text("Obra não encontrada", modifier = Modifier.padding(16.dp))
                }
            }
        }

        @Composable
        fun ArtDetailsView(art: Art, navController: NavController, artViewModel: ArtViewModel) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val isLiked = remember { mutableStateOf(false) }
            val likes = artViewModel.likes.observeAsState(0)
            val comments = artViewModel.comments.observeAsState(listOf())

            LaunchedEffect(key1 = art.id) {
                artViewModel.isArtLikedByUser(userId, art.id) { liked ->
                    isLiked.value = liked
                }
                artViewModel.updateLikesCount(art.id)
            }

            Scaffold(
                topBar = {
                    TopBar(
                        routeName = "Detalhes da Obra",
                        navController = navController,
                        onSearchResults = { /**/ },
                        onShowQRCode = {
                            navController.navigate("qrcode_screen/${art.id}")
                        }
                    )
                },
                bottomBar = {
                    CommentInputSection(art.id, artViewModel)
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Image(
                            painter = rememberImagePainter(data = art.imageUrl),
                            contentDescription = "Imagem da Obra",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shadow(4.dp, RoundedCornerShape(8.dp))
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = art.name,
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                if (isLiked.value) {
                                    artViewModel.unlikeArt(userId, art.id)
                                } else {
                                    artViewModel.likeArt(userId, art.id)
                                }
                                isLiked.value = !isLiked.value
                            }) {
                                Icon(
                                    imageVector = if (isLiked.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = if (isLiked.value) "Descurtir" else "Curtir",
                                    tint = if (isLiked.value) Color.Red else Color.Gray
                                )
                            }
                            Text("${likes.value}", style = MaterialTheme.typography.subtitle1)
                        }
                        Text(text = "por ${art.author}", style = MaterialTheme.typography.body1)
                        Spacer(Modifier.height(8.dp))
                        Text(text = art.description, style = MaterialTheme.typography.body2)
                        Spacer(Modifier.height(16.dp))
                    }
                    items(comments.value) { comment ->
                        CommentItem(comment, artViewModel)
                        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08F))
                    }
                    if (comments.value.isEmpty()) {
                        item {
                            Text("Sem comentários.", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }








        @Composable
        private fun CommentInputSection(artId: String, artViewModel: ArtViewModel) {
            var commentText by remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Adicione um comentário") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        onCommentPosted(artId, commentText, artViewModel)
                        commentText = ""
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar Comentário")
                }
            }
        }

        @Composable
        private fun CommentItem(comment: Comment, artViewModel: ArtViewModel) {
            var elapsedTime by remember { mutableStateOf("") }
            val isAdmin by artViewModel.isAdmin.observeAsState(initial = false)

            LaunchedEffect(key1 = comment, key2 = true) {
                while (true) {
                    elapsedTime = calculateElapsedTime(comment.timestamp)
                    kotlinx.coroutines.delay(60000) // Atualiza a cada minuto
                }
            }

            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.onBackground.copy(alpha = 0.05f))
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "@${comment.userName}",
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onSurface
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = elapsedTime,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    Text(
                        comment.text,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface
                    )
                }
                if (isAdmin) {  // botao de deletar comentarios
                    IconButton(onClick = {
                        val commentsManager = provideCommentsManager(provideFirebaseDatabase())
                        commentsManager.removeComment(comment.id)
                        artViewModel.reloadComments(comment.artId)
                    }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir Comentário", tint = MaterialTheme.colors.error)
                    }
                }
            }
        }






        private fun onCommentPosted(artId: String, commentText: String, artViewModel: ArtViewModel) {
            if (commentText.isBlank()) {
                return
            }
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            val currentTimeMillis = System.currentTimeMillis()
            val comment = Comment(
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "Anônimo",
                text = commentText,
                artId = artId,
                timestamp = currentTimeMillis
            )
            val commentsManager = provideCommentsManager(provideFirebaseDatabase())
            commentsManager.addComment(comment, onSuccess = {
                artViewModel.reloadComments(artId)
            }, onFailure = {
                //
            })
        }


        private fun calculateElapsedTime(timestamp: Long): String {
            val commentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
            val currentTime = LocalDateTime.now(ZoneOffset.UTC)
            val duration = Duration.between(commentTime, currentTime)

            val days = duration.toDays()
            val hours = duration.toHours() % 24
            val minutes = duration.toMinutes() % 60
            val seconds = duration.seconds % 60

            return when {
                days > 0 -> "$days dias atrás"
                hours > 0 -> "$hours horas atrás"
                minutes > 0 -> "$minutes minutos atrás"
                else -> "$seconds segundos atrás"
            }
        }

    }
}