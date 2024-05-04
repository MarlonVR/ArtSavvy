package com.example.artsavvy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artsavvy.di.AppModule.provideCommentsManager
import com.example.artsavvy.di.AppModule.provideFirebaseDatabase
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.manager.CommentsManager
import com.example.artsavvy.model.Art
import com.example.artsavvy.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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
            "author" to updatedArt.author
        )
        artManager.editArt(artId, updatedFields)
    }

    fun removeArt(artId: String) {
        artManager.removeArt(artId)
    }
}


