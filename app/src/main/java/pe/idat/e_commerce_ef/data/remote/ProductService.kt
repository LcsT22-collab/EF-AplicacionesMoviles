package pe.idat.e_commerce_ef.data.remote

import pe.idat.e_commerce_ef.data.remote.dto.ProductsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ProductService {
    @GET("products.json")
    suspend fun getProducts(): Response<ProductsResponse>

    companion object {
        private const val BASE_URL = "https://json-tienda.vercel.app/"
        fun create(): ProductService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProductService::class.java)
        }
    }
}