package com.example.handmadeecommerce.model

data class User(
    val id: String = "",
    val email: String = "",
    val profile: String = "", // "Artesão" ou "Cliente"
    val password: String = ""
)
