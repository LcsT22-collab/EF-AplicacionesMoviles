package pe.idat.e_commerce_ef.domain.repository

import pe.idat.e_commerce_ef.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductsFromLocal(): List<Product>
    suspend fun saveProductsToLocal(products: List<Product>)
    suspend fun clearLocalProducts()
}