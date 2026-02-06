package com.store.riderfit.domain.usecase.product

import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.Product
import com.store.riderfit.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(
    private val productRepository: IProductRepository
) {
    operator fun invoke() = productRepository.getProducts()
}
