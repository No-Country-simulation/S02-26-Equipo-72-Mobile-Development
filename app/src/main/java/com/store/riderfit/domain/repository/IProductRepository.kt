package com.store.riderfit.domain.repository

import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    fun getProducts(): Flow<AuthResult<List<Product>>>
    fun getProductById(productId: String): Flow<AuthResult<Product>>
}
