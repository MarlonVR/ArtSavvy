package com.example.artsavvy.manager

import com.example.artsavvy.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserManager(private val database: FirebaseDatabase) {

    private val usersRef = database.getReference("Users")

    fun addUser(uid: String, user: User) {
        usersRef.child(uid).setValue(user.copy(id = uid))
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        val myRef = database.getReference("Users")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                callback(users)
            }
            override fun onCancelled(error: DatabaseError) {
                // Tratar erros
            }
        })
    }

    fun editUser(userId: String, updatedFields: Map<String, Any>) {
        usersRef.child(userId).updateChildren(updatedFields)
    }

    fun removeUser(userId: String) {
        usersRef.child(userId).removeValue()
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        usersRef.child(userId).get().addOnSuccessListener { snapshot ->
            callback(snapshot.getValue(User::class.java))
        }.addOnFailureListener {
            callback(null)
        }
    }
}
