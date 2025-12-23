package com.example.shopper.models

data class Product(
    // ✅ Double để đồng bộ với Firestore
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val categoryId: String = ""
)

