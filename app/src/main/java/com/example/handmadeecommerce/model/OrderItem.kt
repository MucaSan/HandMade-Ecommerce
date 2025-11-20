package com.example.handmadeecommerce.model

data class OrderItem(
    val id: String = "",
    val orderId: String = "",
    val productId: String = "",
    val artisanId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val quantity: Int = 0,
    val subtotal: Double = 0.0
)