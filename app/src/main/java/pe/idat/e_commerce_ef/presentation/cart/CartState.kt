package pe.idat.e_commerce_ef.presentation.cart

import pe.idat.e_commerce_ef.domain.model.Product

data class CartState(
    val items: List<Product> = emptyList(),
    val total: Double = 0.0,
    val isEmpty: Boolean = true
)