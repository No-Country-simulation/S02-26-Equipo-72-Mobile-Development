package com.store.riderfit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.usecase.product.GetProductsUseCase
import com.store.riderfit.presentation.state.ProductUiState
import com.store.riderfit.presentation.state.SortOption
import com.store.riderfit.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    // ==================== CARGA DE PRODUCTOS ====================

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getProductsUseCase().collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                products = result.data,
                                productsState = UiState.Success(result.data),
                                isLoading = false,
                                isEmpty = result.data.isEmpty(),
                                totalPages = (result.data.size + it.pageSize - 1) / it.pageSize
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                productsState = UiState.Error(result.message),
                                error = result.message,
                                isLoading = false,
                                isEmpty = true
                            )
                        }
                    }
                    is AuthResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                productsState = UiState.Loading(),
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== FILTRADO Y BÚSQUEDA ====================

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 0) }
        applyFilters()
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category, currentPage = 0) }
        applyFilters()
    }

    fun onSortOptionChanged(sortOption: SortOption) {
        _uiState.update { it.copy(sortBy = sortOption, currentPage = 0) }
        applyFilters()
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = null,
                sortBy = SortOption.NEWEST,
                currentPage = 0
            )
        }
        applyFilters()
    }

    private fun applyFilters() {
        _uiState.update { state ->
            var filtered = state.products

            // Filtro por búsqueda
            if (state.searchQuery.isNotEmpty()) {
                filtered = filtered.filter { product ->
                    product.name.contains(state.searchQuery, ignoreCase = true) ||
                            product.description.contains(state.searchQuery, ignoreCase = true)
                }
            }

            // Filtro por categoría
            if (state.selectedCategory != null) {
                filtered = filtered.filter { it.category == state.selectedCategory }
            }

            // Ordenamiento
            filtered = when (state.sortBy) {
                SortOption.NEWEST -> filtered.sortedByDescending { it.id }
                SortOption.OLDEST -> filtered.sortedBy { it.id }
                SortOption.PRICE_LOW_HIGH -> filtered.sortedBy { it.price }
                SortOption.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.price }
                SortOption.RATING -> filtered.sortedByDescending { it.rating }
                SortOption.POPULARITY -> filtered.sortedByDescending { it.rating }
            }

            state.copy(filteredProducts = filtered)
        }
    }

    // ==================== PAGINACIÓN ====================

    fun goToNextPage() {
        _uiState.update { state ->
            if (state.currentPage < state.totalPages - 1) {
                state.copy(currentPage = state.currentPage + 1)
            } else {
                state
            }
        }
    }

    fun goToPreviousPage() {
        _uiState.update { state ->
            if (state.currentPage > 0) {
                state.copy(currentPage = state.currentPage - 1)
            } else {
                state
            }
        }
    }

    // ==================== SELECCIÓN DE PRODUCTO ====================

    fun selectProduct(productId: String) {
        _uiState.update { state ->
            val product = state.displayedProducts.find { it.id == productId }
            state.copy(
                selectedProduct = product,
                selectedProductState = if (product != null) {
                    UiState.Success(product)
                } else {
                    UiState.Error("Producto no encontrado")
                }
            )
        }
    }

    fun clearSelectedProduct() {
        _uiState.update { state -> state.copy(selectedProduct = null, selectedProductState = UiState.Idle()) }
    }

    // ==================== CARRITO ====================

    fun addToCart(productId: String) {
        _uiState.update { state ->
            val product = state.displayedProducts.find { it.id == productId }
            if (product != null && !state.cartItems.contains(product)) {
                state.copy(
                    cartItems = state.cartItems + product,
                    cartCount = state.cartCount + 1
                )
            } else {
                state
            }
        }
    }

    fun removeFromCart(productId: String) {
        _uiState.update { state ->
            val product = state.cartItems.find { it.id == productId }
            if (product != null) {
                state.copy(
                    cartItems = state.cartItems - product,
                    cartCount = (state.cartCount - 1).coerceAtLeast(0)
                )
            } else {
                state
            }
        }
    }

    fun clearCart() {
        _uiState.update { state -> state.copy(cartItems = emptyList(), cartCount = 0) }
    }
}
