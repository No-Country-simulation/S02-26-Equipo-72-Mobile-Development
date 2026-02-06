package com.store.riderfit.domain.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val inStock: Boolean,
    val rating: Double
)
