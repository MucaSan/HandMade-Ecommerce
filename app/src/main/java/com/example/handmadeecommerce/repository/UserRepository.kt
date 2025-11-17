package com.example.handmadeecommerce.repository

import com.google.firebase.auth.AuthResult
import com.example.handmadeecommerce.model.User

interface UserRepository {
    suspend fun registerUser(user: User): Result<AuthResult>
    suspend fun loginUser(email: String, password: String): Result<AuthResult>
    suspend fun getUserProfile(uid: String): Result<User>
}