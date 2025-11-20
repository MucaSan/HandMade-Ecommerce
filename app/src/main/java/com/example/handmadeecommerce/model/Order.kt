package com.example.handmadeecommerce.model

import java.util.Date

data class Order(
    val id: String = "",
    val userId: String = "", // ID do cliente que comprou
    val totalAmount: Double = 0.0,
    val status: String = "PENDING", // Ex: PENDING, PAID, SHIPPED
    val createdAt: Date = Date()
)