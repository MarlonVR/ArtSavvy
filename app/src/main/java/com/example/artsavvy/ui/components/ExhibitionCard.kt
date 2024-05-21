package com.example.artsavvy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.artsavvy.model.Exhibition

private val LightGray = Color(0xFFF5F5F5)
@Composable
fun ExhibitionCard(
    exhibition: Exhibition,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        elevation = 4.dp,
        backgroundColor = LightGray
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberImagePainter(data = exhibition.imgUrl),
                contentDescription = "Imagem da exposição",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = exhibition.name, style = MaterialTheme.typography.titleMedium)
            Text(text = exhibition.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "De ${exhibition.start} até ${exhibition.end}", style = MaterialTheme.typography.bodySmall)
            if (isAdmin) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EditableTextLink(text = "Editar", onClick = onEdit)
                    EditableTextLink(text = "Excluir", onClick = onDelete)
                }
            }
        }
    }
}

@Composable
private fun EditableTextLink(text: String, onClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        pushStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.None))
        append(text)
        pop()
    }
    ClickableText(
        text = annotatedText,
        onClick = { onClick() },
        modifier = Modifier.padding(4.dp)
    )
}
