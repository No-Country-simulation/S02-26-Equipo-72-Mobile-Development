package com.store.riderfit.data.model

import com.store.riderfit.domain.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val inStock: Boolean = true,
    val rating: Double = 0.0
)

// Mapeos
fun ProductDto.toDomain() = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    category = category,
    inStock = inStock,
    rating = rating
)

fun Product.toDto() = ProductDto(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    category = category,
    inStock = inStock,
    rating = rating
)
