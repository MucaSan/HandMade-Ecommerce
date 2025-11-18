package com.example.handmadeecommerce.repository

import com.example.handmadeecommerce.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun registerUser(user: User): Result<AuthResult> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(user.email, user.password).await()

            authResult.user?.let { firebaseUser ->
                val user = User(
                    id = firebaseUser.uid,
                    email = user.email,
                    profile = user.profile,
                    password = user.password
                )
                firestore.collection("user").document(firebaseUser.uid).set(user).await()
            }

            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<AuthResult> {
        return try {
            // Apenas faz o login no Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 👇 ADICIONE A IMPLEMENTAÇÃO PARA BUSCAR O PERFIL
    override suspend fun getUserProfile(uid: String): Result<User> {
        return try {
            // Busca o documento do usuário no Firestore
            val document = firestore.collection("user").document(uid).get().await()
            // Converte o documento de volta para seu objeto User
            val user = document.toObject(User::class.java)

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Perfil de usuário não encontrado no Firestore."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}