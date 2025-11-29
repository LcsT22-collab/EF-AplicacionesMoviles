package pe.idat.e_commerce_ef.presentation.products

import pe.idat.e_commerce_ef.domain.model.Product

sealed class ProductsState {
    object Loading : ProductsState()
    data class Success(val products: List<Product>) : ProductsState()
    data class Error(val message: String) : ProductsState()
}