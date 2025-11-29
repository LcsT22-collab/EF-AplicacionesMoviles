package pe.idat.e_commerce_ef.network

import pe.idat.e_commerce_ef.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("products.json")
    suspend fun getProducts(): Response<ProductResponse>
}