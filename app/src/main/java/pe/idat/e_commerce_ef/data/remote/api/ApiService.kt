package pe.idat.e_commerce_ef.data.remote.api

import pe.idat.e_commerce_ef.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    // Tu endpoint es products.json
    @GET("products.json")
    suspend fun getProducts(): Response<ProductsResponse>

    @GET("products/{id}") // Si tienes endpoint individual
    suspend fun getProductById(@retrofit2.http.Path("id") id: Int): Response<ProductDto>
}

// Crear un nuevo DTO para la respuesta completa
data class ProductsResponse(
    val products: List<ProductDto>
)