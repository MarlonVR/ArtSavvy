package com.example.artsavvy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artsavvy.model.Art
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArtViewModel : ViewModel() {
    private val _artPieces = MutableLiveData<List<Art>>()
    val artPieces: LiveData<List<Art>> = _artPieces

    init {
        fetchArtworks()
    }

    private fun fetchArtworks() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Obra")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val artworks = snapshot.children.mapNotNull { it.getValue(Art::class.java) }
                _artPieces.value = artworks
            }
            override fun onCancelled(error: DatabaseError) {
                // tratar erros
            }
        })
    }
}
