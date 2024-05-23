package com.example.artsavvy.model

data class Art(
    val id: String = "",
    val name: String = "",
    val author: String = "",
    val exhibitionId: String = "",
    val imageUrl: String = "",
    val description: String = "",
    var likes: Int = 0
)
