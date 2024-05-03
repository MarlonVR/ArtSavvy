package com.example.artsavvy.manager

import com.example.artsavvy.model.Exhibition
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExhibitionManager(private val database: FirebaseDatabase) {

    private val exhibitionsRef = database.getReference("Exhibitions")

    fun addExhibition(exhibition: Exhibition) {
        val id = exhibitionsRef.push().key ?: return
        exhibitionsRef.child(id).setValue(exhibition.copy(id = id))
    }

    fun editExhibition(exhibitionId: String, updatedFields: Map<String, Any>) {
        exhibitionsRef.child(exhibitionId).updateChildren(updatedFields)
    }

    fun removeExhibition(exhibitionId: String) {
        exhibitionsRef.child(exhibitionId).removeValue()
    }

    fun getExhibition(exhibitionId: String, callback: (Exhibition?) -> Unit) {
        exhibitionsRef.child(exhibitionId).get().addOnSuccessListener { snapshot ->
            callback(snapshot.getValue(Exhibition::class.java))
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun getAllExhibitions(callback: (List<Exhibition>) -> Unit) {
        database.reference.child("Exhibitions").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exhibitions = snapshot.children.mapNotNull { it.getValue(Exhibition::class.java) }
                callback(exhibitions)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible database errors
            }
        })
    }
}
