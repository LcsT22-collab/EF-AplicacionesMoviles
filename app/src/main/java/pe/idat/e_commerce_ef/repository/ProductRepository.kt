// CORREGIR en ProductRepository.kt
package pe.idat.e_commerce_ef.repository

import pe.idat.e_commerce_ef.model.Product
import pe.idat.e_commerce_ef.network.RetrofitClient
import kotlin.Result

class ProductRepository {
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = RetrofitClient.apiService.getProducts()
            if (response.isSuccessful) {
                val products = response.body()?.products ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}