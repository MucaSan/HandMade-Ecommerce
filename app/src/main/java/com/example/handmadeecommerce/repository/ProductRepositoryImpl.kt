package com.example.handmadeecommerce.repository

import android.net.Uri
import com.example.handmadeecommerce.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProductRepository {


    private val productCollection = firestore.collection("product")

    private val currentUserId: String?
        get() = auth.currentUser?.uid


    override suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            val uid = currentUserId ?: return Result.failure(Exception("Usuário não logado"))

            val fileName = "product_images/$uid/${UUID.randomUUID()}"
            val storageRef = storage.reference.child(fileName)


            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArtisanProducts(): Result<List<Product>> {
        val uid = currentUserId ?: return Result.failure(Exception("Usuário não logado"))
        return try {
            val snapshot = productCollection
                .whereEqualTo("artisanId", uid)
                .get()
                .await()
            val products = snapshot.toObjects(Product::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProduct(product: Product): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Usuário não logado"))
        return try {
            val newDocRef = productCollection.document()
            val productWithIds = product.copy(
                artisanId = uid,
                productId = newDocRef.id
            )
            newDocRef.set(productWithIds).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        if (product.productId.isBlank()) {
            return Result.failure(Exception("ID do produto inválido"))
        }
        return try {
            productCollection.document(product.productId)
                .set(product)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productCollection.document(productId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}