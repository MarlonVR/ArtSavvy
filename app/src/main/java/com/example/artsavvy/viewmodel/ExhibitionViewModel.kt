package com.example.artsavvy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.artsavvy.model.Exhibition
import com.example.artsavvy.manager.ExhibitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExhibitionViewModel : ViewModel() {
    private val exhibitionManager = ExhibitionManager(FirebaseDatabase.getInstance())
    private val _exhibitions = MutableLiveData<List<Exhibition>>()
    val exhibitions: LiveData<List<Exhibition>> = _exhibitions

    private val _exhibitionDetails = MutableLiveData<Exhibition?>()
    val exhibitionDetails: LiveData<Exhibition?> = _exhibitionDetails

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    init {
        loadExhibitions()
        checkIfUserIsAdmin()
    }

    fun loadExhibitions() {
        exhibitionManager.getAllExhibitions { exhibitions ->
            _exhibitions.postValue(exhibitions)
        }
    }
    fun addExhibition(exhibition: Exhibition) {
        exhibitionManager.addExhibition(exhibition)
    }

    fun updateExhibition(exhibitionId: String, updatedFields: Map<String, Any>) {
        exhibitionManager.editExhibition(exhibitionId, updatedFields)
    }

    fun deleteExhibition(exhibitionId: String) {
        exhibitionManager.removeExhibition(exhibitionId)
    }

    fun getExhibitionById(exhibitionId: String) {
        exhibitionManager.getExhibition(exhibitionId) { exhibition ->
            _exhibitionDetails.postValue(exhibition)
        }
    }


    private fun checkIfUserIsAdmin() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("admin")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _isAdmin.value = snapshot.getValue(Boolean::class.java) ?: false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _isAdmin.value = false
                    }
                })
        } ?: run {
            _isAdmin.value = false
        }
    }

}
