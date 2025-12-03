package pe.idat.e_commerce_ef.data.repository

import pe.idat.e_commerce_ef.data.local.AppDatabase
import pe.idat.e_commerce_ef.data.mapper.ProductMapper
import pe.idat.e_commerce_ef.data.remote.RetrofitClient
import pe.idat.e_commerce_ef.data.remote.api.ProductsResponse
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : ProductRepository {

    private val apiService = RetrofitClient.apiService

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            println("🔄 Intentando obtener productos de API propia...")

            val response = apiService.getProducts()
            if (response.isSuccessful) {
                println("✅ API propia exitosa")
                val apiResponse: ProductsResponse? = response.body()
                val apiProducts = apiResponse?.products ?: emptyList()
                val products = apiProducts.map { ProductMapper.apiToDomain(it) }

                // Guardar en base de datos local
                saveProductsToLocal(products)
                println("💾 Productos guardados en Room: ${products.size} items")

                Result.success(products)
            } else {
                println("⚠️ API falló, código: ${response.code()}")
                // Si falla la API, obtener de la base de datos local
                val localProducts = getProductsFromLocal()
                println("📁 Productos desde Room: ${localProducts.size} items")

                if (localProducts.isNotEmpty()) {
                    Result.success(localProducts)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            println("❌ Error en API: ${e.message}")
            val localProducts = getProductsFromLocal()
            println("📁 Productos desde Room (catch): ${localProducts.size} items")

            if (localProducts.isNotEmpty()) {
                Result.success(localProducts)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getProductsFromLocal(): List<Product> {
        return database.productDao().getAllProducts().map { ProductMapper.localToDomain(it) }
    }

    override suspend fun saveProductsToLocal(products: List<Product>) {
        val localProducts = products.map { ProductMapper.domainToLocal(it) }
        database.productDao().insertProducts(localProducts)
    }

    override suspend fun clearLocalProducts() {
        database.productDao().clearAllProducts()
    }
}