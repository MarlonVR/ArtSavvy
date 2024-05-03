package com.example.artsavvy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.artsavvy.model.Exhibition
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ExhibitionViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val _exhibitions = MutableLiveData<List<Exhibition>>()
    val exhibitions: LiveData<List<Exhibition>> = _exhibitions

    init {
        loadExhibitions()
    }

    fun loadExhibitions() {
        try {
            database.child("Exhibitions").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newExhibitions = snapshot.children.mapNotNull { it.getValue(Exhibition::class.java) }
                    _exhibitions.postValue(newExhibitions)
                }

                override fun onCancelled(error: DatabaseError) {
                    _exhibitions.postValue(emptyList())
                }
            })
        } catch (e: Exception) {
            _exhibitions.postValue(emptyList())
        }
    }
}
