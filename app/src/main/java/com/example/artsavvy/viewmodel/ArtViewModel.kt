package com.example.artsavvy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.model.Art
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ArtViewModel(private val artManager: ArtManager) : ViewModel() {
    private val _artPieces = MutableLiveData<List<Art>>()
    val artPieces: LiveData<List<Art>> = _artPieces

    init {
        loadArtworks()
    }

    private fun loadArtworks() {
        artManager.getAllArts { artworks ->
            _artPieces.postValue(artworks)
        }
    }
}


