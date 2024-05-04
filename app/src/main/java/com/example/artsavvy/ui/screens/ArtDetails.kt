package com.example.artsavvy.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.artsavvy.di.AppModule
import com.example.artsavvy.di.AppModule.provideCommentsManager
import com.example.artsavvy.di.AppModule.provideFirebaseDatabase
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.manager.CommentsManager
import com.example.artsavvy.model.Art
import com.example.artsavvy.ui.components.TopBar
import com.example.artsavvy.viewmodel.ArtViewModel
import com.example.artsavvy.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ArtDetails {
    companion object{
        @Composable
        fun Screen(navController: NavController, artId: String) {
            val artManager: ArtManager = AppModule.provideArtManager(AppModule.provideFirebaseDatabase())
            val artViewModel = remember { ArtViewModel(artManager) }

            var art by remember { mutableStateOf<Art?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            val comments by artViewModel.comments.observeAsState(initial = emptyList())

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
                    ArtDetailsView(art!!, navController, comments, artViewModel)
                } else {
                    Text("Obra não encontrada", modifier = Modifier.padding(16.dp))
                }
            }
        }

        @Composable
        private fun ArtDetailsView(art: Art, navController: NavController, comments: List<Comment>, artViewModel: ArtViewModel) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    TopBar(routeName = "Detalhes da Obra", navController = navController)
                    Spacer(Modifier.height(16.dp)) // Ajuste de espaçamento após a barra superior
                }
                item {
                    Image(
                        painter = rememberImagePainter(data = art.imageUrl),
                        contentDescription = "Imagem da Obra",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                item {
                    Text(text = art.name, style = MaterialTheme.typography.h5, modifier = Modifier.padding(horizontal = 16.dp))
                    Text(text = "por ${art.author}", style = MaterialTheme.typography.body1, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(text = art.description, style = MaterialTheme.typography.body2, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(16.dp))
                }
                item {
                    CommentInputSection(art.id, artViewModel)
                }
                this.items(comments) { comment ->
                    CommentItem(comment)
                }
                if (comments.isEmpty()) {
                    item {
                        Text("Sem comentários ainda.", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
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
        private fun CommentItem(comment: Comment) {
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .background(color = Color.LightGray) // Fundo cinza para facilitar a visualização
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(48.dp) // Tamanho maior para o ícone
                )
                Column(modifier = Modifier.padding(start = 8.dp).fillMaxWidth()) {
                    Text(
                        comment.userId,
                        style = MaterialTheme.typography.subtitle2,
                        color = Color.Black, // Cor preta para garantir visibilidade
                        modifier = Modifier.padding(bottom = 2.dp) // Espaço adicional abaixo do nome do usuário
                    )
                    Text(
                        comment.text,
                        style = MaterialTheme.typography.body1, // Use um estilo de texto maior
                        color = Color.Black // Cor preta para garantir visibilidade
                    )
                }
            }
        }

        private fun onCommentPosted(artId: String, commentText: String, artViewModel: ArtViewModel) {
            if (commentText.isBlank()) {
                return
            }
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            val comment = Comment(
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "Anônimo",
                text = commentText,
                artId = artId,
                timestamp = System.currentTimeMillis()
            )
            val commentsManager = provideCommentsManager(provideFirebaseDatabase())
            commentsManager.addComment(comment, onSuccess = {
                artViewModel.reloadComments(artId)
            }, onFailure = {
                // tratar erros
            })
        }

    }
}