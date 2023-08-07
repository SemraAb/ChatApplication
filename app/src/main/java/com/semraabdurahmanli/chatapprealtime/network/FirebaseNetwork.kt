package com.semraabdurahmanli.chatapprealtime.network

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseNetwork {
    companion object{
        val auth = Firebase.auth
        val databaseReference = Firebase.database.reference
    }
}