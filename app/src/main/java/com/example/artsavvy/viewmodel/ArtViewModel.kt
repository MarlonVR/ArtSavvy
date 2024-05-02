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

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    init {
        loadArtworks()
        checkIfUserIsAdmin()
    }

    private fun loadArtworks() {
        artManager.getAllArts { artworks ->
            _artPieces.postValue(artworks)
        }
    }

    private fun checkIfUserIsAdmin() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            provideFirebaseDatabase().reference.child("Users").child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _isAdmin.value = snapshot.child("isAdmin").getValue(Boolean::class.java) ?: false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Log error
                        _isAdmin.value = false
                    }
                })
        } ?: run {
            _isAdmin.value = false
        }
    }
}


