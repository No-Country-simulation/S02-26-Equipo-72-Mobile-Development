package com.store.riderfit.data.repository

import com.store.riderfit.data.remote.firebase.FirestoreService
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.Product
import com.store.riderfit.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl(
    private val firestoreService: FirestoreService
) : IProductRepository {

    override fun getProducts(): Flow<AuthResult<List<Product>>> = flow {
        try {
            emitAll(firestoreService.getProducts())
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Error al obtener productos"))
        }
    }

    override fun getProductById(productId: String): Flow<AuthResult<Product>> = flow {
        try {
            emitAll(firestoreService.getProductById(productId))
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Error al obtener producto"))
        }
    }
}
