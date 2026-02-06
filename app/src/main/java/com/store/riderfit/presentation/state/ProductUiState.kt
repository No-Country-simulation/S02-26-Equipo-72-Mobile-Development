package com.store.riderfit.presentation.state

import com.store.riderfit.domain.model.Product

/**
 * Estado de productos para la UI
 * Maneja listados, filtrado y detalles de productos
 */
data class ProductUiState(
    // Listado general
    val products: List<Product> = emptyList(),
    val productsState: UiState<List<Product>> = UiState.Idle(),
    
    // Filtrado y búsqueda
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val sortBy: SortOption = SortOption.NEWEST,
    val filteredProducts: List<Product> = emptyList(),
    
    // Paginación
    val currentPage: Int = 0,
    val pageSize: Int = 20,
    val totalPages: Int = 0,
    val hasNextPage: Boolean = false,
    
    // Producto individual
    val selectedProduct: Product? = null,
    val selectedProductState: UiState<Product> = UiState.Idle(),
    
    // Carrito
    val cartItems: List<Product> = emptyList(),
    val cartCount: Int = 0,
    
    // Estados generales
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
) {
    val hasProducts: Boolean = products.isNotEmpty()
    val hasFilters: Boolean = searchQuery.isNotEmpty() || selectedCategory != null
    val displayedProducts: List<Product> = 
        if (hasFilters) filteredProducts else products
}

enum class SortOption {
    NEWEST,
    OLDEST,
    PRICE_LOW_HIGH,
    PRICE_HIGH_LOW,
    RATING,
    POPULARITY
}
