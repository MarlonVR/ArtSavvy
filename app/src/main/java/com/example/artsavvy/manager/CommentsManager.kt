package com.example.artsavvy.manager

import com.example.artsavvy.model.Comment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentsManager(private val database: FirebaseDatabase) {
    private val commentsRef = database.reference.child("Comments")

    fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Comments")
        val commentId = databaseReference.push().key ?: run {
            onFailure()
            return
        }

        comment.id = commentId

        databaseReference.child(commentId).setValue(comment)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun getComments(artId: String, callback: (List<Comment>) -> Unit) {
        FirebaseDatabase.getInstance().getReference("Comments")
            .orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments = snapshot.children.mapNotNull { it.getValue(Comment::class.java) }.sortedByDescending { it.timestamp }
                    callback(comments)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }


    fun removeComment(commentId: String) {
        if (commentId.isNotEmpty()) {
            commentsRef.child(commentId).removeValue()
        }
    }
}
