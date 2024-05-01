package com.example.artsavvy.manager

import com.example.artsavvy.model.Art
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArtManager(private val database: FirebaseDatabase) {

    private val artsRef = database.getReference("Arts")

    fun addArt(art: Art) {
        val id = artsRef.push().key ?: return
        artsRef.child(id).setValue(art.copy(id = id))
    }

    fun getAllArts(callback: (List<Art>) -> Unit) {
        val myRef = database.getReference("Arts")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val arts = snapshot.children.mapNotNull { it.getValue(Art::class.java) }
                callback(arts)
            }
            override fun onCancelled(error: DatabaseError) {
                // tratar erros
            }
        })
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
