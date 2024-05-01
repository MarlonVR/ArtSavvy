package com.example.artsavvy.manager

import com.example.artsavvy.model.Art
import com.google.firebase.database.FirebaseDatabase

class ArtManager(private val database: FirebaseDatabase) {

    private val artsRef = database.getReference("Arts")

    fun addArt(art: Art) {
        val id = artsRef.push().key ?: return // Use a push key or your own logic for a unique ID
        artsRef.child(id).setValue(art.copy(id = id))
    }

    fun editArt(artId: String, updatedFields: Map<String, Any>) {
        artsRef.child(artId).updateChildren(updatedFields)
    }

    fun removeArt(artId: String) {
        artsRef.child(artId).removeValue()
    }

    fun getArt(artId: String, callback: (Art?) -> Unit) {
        artsRef.child(artId).get().addOnSuccessListener { snapshot ->
            callback(snapshot.getValue(Art::class.java))
        }.addOnFailureListener {
            callback(null)
        }
    }
}
