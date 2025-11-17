package com.example.handmadeecommerce

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class HandmadeEcommerceApp : Application() {
    val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    val firestore: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    override fun onCreate() {
        super.onCreate()
    }
}
    