package com.example.handmadeecommerce

import android.app.Application
import com.example.handmadeecommerce.repository.ProductRepository
import com.example.handmadeecommerce.repository.ProductRepositoryImpl
import com.example.handmadeecommerce.repository.UserRepository
import com.example.handmadeecommerce.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HandmadeEcommerceApp : Application() {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    val userRepository: UserRepository by lazy { UserRepositoryImpl(auth, firestore) }
    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(auth, firestore, storage)
    }
}