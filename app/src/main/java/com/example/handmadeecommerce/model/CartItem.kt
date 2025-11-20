package com.example.handmadeecommerce.model

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)