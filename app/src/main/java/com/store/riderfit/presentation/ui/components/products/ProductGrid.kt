package com.store.riderfit.presentation.ui.components.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.store.riderfit.domain.model.Product

@Composable
fun ProductGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEmpty: Boolean = false
) {
    if (isEmpty || products.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hay productos disponibles")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductGridPreview() {
    val mockProducts = listOf(
        Product(
            id = "1",
            name = "Producto 1",
            description = "Descripción 1",
            price = 99.99,
            imageUrl = "",
            category = "Categoría 1",
            inStock = true,
            rating = 4.5
        ),
        Product(
            id = "2",
            name = "Producto 2",
            description = "Descripción 2",
            price = 149.99,
            imageUrl = "",
            category = "Categoría 2",
            inStock = true,
            rating = 4.0
        ),
        Product(
            id = "3",
            name = "Producto 3",
            description = "Descripción 3",
            price = 199.99,
            imageUrl = "",
            category = "Categoría 1",
            inStock = false,
            rating = 3.5
        )
    )

    ProductGrid(
        products = mockProducts,
        onProductClick = {},
        onAddToCart = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ProductGridEmptyPreview() {
    ProductGrid(
        products = emptyList(),
        onProductClick = {},
        onAddToCart = {},
        isEmpty = true
    )
}
