package com.store.riderfit.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.store.riderfit.data.model.ProductDto
import com.store.riderfit.data.model.toDomain
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val firestore: FirebaseFirestore
) {

    fun getProducts(): Flow<AuthResult<List<Product>>> = callbackFlow {
        try {
            trySend(AuthResult.Loading())
            
            val snapshot = firestore.collection("products")
                .get()
                .await()
            
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProductDto::class.java)?.toDomain()
            }
            
            trySend(AuthResult.Success(products))
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Error al obtener productos"))
        }
        awaitClose()
    }

    fun getProductById(productId: String): Flow<AuthResult<Product>> = callbackFlow {
        try {
            trySend(AuthResult.Loading())
            
            val snapshot = firestore.collection("products")
                .document(productId)
                .get()
                .await()
            
            if (snapshot.exists()) {
                val productDto = snapshot.toObject(ProductDto::class.java)
                if (productDto != null) {
                    trySend(AuthResult.Success(productDto.toDomain()))
                } else {
                    trySend(AuthResult.Error("No se pudo convertir el documento"))
                }
            } else {
                trySend(AuthResult.Error("Producto no encontrado"))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Error al obtener producto"))
        }
        awaitClose()
    }
}
