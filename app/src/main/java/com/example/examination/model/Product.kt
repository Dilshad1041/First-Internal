package com.example.cloud.model

data class Product(
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    var timestamp: Any? = null // Add a field for timestamp
)

