package com.example.handmadeecommerce.repository

import android.net.Uri // <-- IMPORTAR
import com.example.handmadeecommerce.model.Product

interface ProductRepository {
    suspend fun getArtisanProducts(): Result<List<Product>>
    suspend fun addProduct(product: Product): Result<Unit>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>

    suspend fun uploadImage(imageUri: Uri): Result<String>
}