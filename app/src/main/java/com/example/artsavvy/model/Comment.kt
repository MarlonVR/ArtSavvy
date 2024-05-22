package com.example.artsavvy.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment(
    var id: String = "",
    var userId: String = "",
    var userName: String = "",
    var text: String = "",
    var artId: String = "",
    var timestamp: Long = 0L
)

