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

        databaseReference.child(commentId).setValue(comment)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun getComments(artId: String, callback: (List<Comment>) -> Unit) {
        database.getReference("Comments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments = mutableListOf<Comment>()
                    for (childSnapshot in snapshot.children) {
                        val comment = childSnapshot.getValue(Comment::class.java)
                        if (comment != null && comment.artId == artId) {
                            comments.add(comment)
                        }
                    }
                    callback(comments)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Trate a falha de carregamento aqui
                }
            })
    }

    fun removeComment(artId: String, commentId: String) {
        commentsRef.child(artId).child(commentId).removeValue()
    }
}
