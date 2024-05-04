package com.example.artsavvy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artsavvy.di.AppModule.provideFirebaseDatabase
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
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

    init {
        checkIfUserIsAdmin()
    }

    fun loadArtsForExhibition(exhibitionId: String) {
        artManager.getArtsByExhibitionId(exhibitionId) { arts ->
            _artPieces.postValue(arts)
        }
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


