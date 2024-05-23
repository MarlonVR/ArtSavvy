package com.example.artsavvy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artsavvy.di.AppModule.provideCommentsManager
import com.example.artsavvy.di.AppModule.provideFirebaseDatabase
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
import com.example.artsavvy.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener


class ArtViewModel(private val artManager: ArtManager) : ViewModel() {
    private val _artPieces = MutableLiveData<List<Art>>()
    val artPieces: LiveData<List<Art>> = _artPieces

    private val _artDetails = MutableLiveData<Art?>()
    val artDetails: LiveData<Art?> = _artDetails

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    private val _likes = MutableLiveData<Int>()
    val likes: LiveData<Int> = _likes

    private val commentsManager = provideCommentsManager(provideFirebaseDatabase())

    init {
        checkIfUserIsAdmin()
    }

    fun loadArtsForExhibition(exhibitionId: String, onComplete: () -> Unit = {}) {
        _isLoading.value = true
        artManager.getArtsByExhibitionId(exhibitionId) { arts ->
            _artPieces.postValue(arts)
            _isLoading.value = false
            onComplete()
        }
    }

    fun loadCommentsForArt(artId: String) {
        commentsManager.getComments(artId) { fetchedComments ->
            Log.d("ArtDetails", "Loaded comments: ${fetchedComments.size}")
            _comments.postValue(fetchedComments)
        }
    }

    fun reloadComments(artId: String) {
        loadCommentsForArt(artId)
    }

    private fun checkIfUserIsAdmin() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            FirebaseDatabase.getInstance().reference.child("Users").child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _isAdmin.value = snapshot.child("admin").getValue(Boolean::class.java) ?: false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _isAdmin.value = false
                    }
                })
        } ?: run {
            _isAdmin.value = false
        }
    }


    fun getArtById(artId: String, callback: (Art?) -> Unit) {
        artManager.getArt(artId) { art ->
            _artDetails.postValue(art)
            callback(art)
        }
    }

    fun editArt(artId: String, updatedArt: Art) {
        val updatedFields = mapOf(
            "name" to updatedArt.name,
            "author" to updatedArt.author,
            "description" to updatedArt.description,
            "imageUrl" to updatedArt.imageUrl
        )
        artManager.editArt(artId, updatedFields)
    }

    fun removeArt(artId: String) {
        artManager.removeArt(artId)
    }

    fun updateLikesCount(artId: String) {
        getLikes(artId) { count ->
            _likes.postValue(count)
        }
    }
    private fun getLikes(artId: String, onResult: (Int) -> Unit) {
        val likesRef = FirebaseDatabase.getInstance().getReference("Arts").child(artId).child("likes")
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.getValue(Int::class.java) ?: 0
                _likes.postValue(count) // Assume _likesCount é um MutableLiveData<Int>
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ArtDetails", "Failed to listen for likes changes: ${error.message}")
            }
        })
    }



    fun likeArt(userId: String, artId: String) {
        val artLikesRef = FirebaseDatabase.getInstance().getReference("Arts").child(artId).child("likes")
        val userLikesRef = FirebaseDatabase.getInstance().getReference("LikedArts").child(artId)

        userLikesRef.child(userId).setValue("").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                artLikesRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        var count = mutableData.getValue(Int::class.java) ?: 0
                        mutableData.value = count + 1
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                        if (committed) {
                            println("Art liked successfully and like count incremented for art $artId by user $userId")
                        } else {
                            println("Failed to increment like count: ${databaseError?.message ?: "Unknown error"}")
                        }
                    }
                })
            } else {
                println("Failed to like art: ${task.exception?.message ?: "Unknown error"}")
            }
        }
    }

    fun unlikeArt(userId: String, artId: String) {
        val artLikesRef = FirebaseDatabase.getInstance().getReference("Arts").child(artId).child("likes")
        val userLikesRef = FirebaseDatabase.getInstance().getReference("LikedArts").child(artId)

        userLikesRef.child(userId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                artLikesRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        var count = mutableData.getValue(Int::class.java) ?: 0
                        if (count > 0) {
                            mutableData.value = count - 1
                        }
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                        if (committed) {
                            println("Art unliked successfully and like count decremented for art $artId by user $userId")
                        } else {
                            println("Failed to decrement like count: ${databaseError?.message ?: "Unknown error"}")
                        }
                    }
                })
            } else {
                println("Failed to unlike art: ${task.exception?.message ?: "Unknown error"}")
            }
        }
    }



    fun isArtLikedByUser(userId: String, artId: String, onResult: (Boolean) -> Unit) {
        val likesRef = FirebaseDatabase.getInstance().getReference("LikedArts").child(artId).child(userId)

        likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onResult(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to check if art is liked: ${error.message}")
                onResult(false)
            }
        })
    }

}


