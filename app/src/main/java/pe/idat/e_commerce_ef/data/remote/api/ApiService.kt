package pe.idat.e_commerce_ef.data.remote.api

import pe.idat.e_commerce_ef.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @GET("products/{id}")
    suspend fun getProductById(@retrofit2.http.Path("id") id: Int): Response<ProductDto>
}