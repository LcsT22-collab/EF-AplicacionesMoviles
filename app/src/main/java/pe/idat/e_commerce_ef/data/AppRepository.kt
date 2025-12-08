package pe.idat.e_commerce_ef.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.idat.e_commerce_ef.data.local.AppDatabase
import pe.idat.e_commerce_ef.data.remote.ProductService
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.util.ProductMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AppRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val productDao = db.productDao()
    private val auth = FirebaseAuth.getInstance()
    private val api = ProductService.create()

    suspend fun getProducts(): Result<List<Product>> {
        return try {
            Log.d("AppRepository", "=== OBTAINING PRODUCTS ===")

            try {
                val localProducts = productDao.getAll().map { ProductMapper.entityToDomain(it) }
                Log.d("AppRepository", "Productos obtenidos de BD local: ${localProducts.size}")

                if (localProducts.isNotEmpty()) {
                    Log.d("AppRepository", "✅ Retornando productos de BD local")
                    return Result.success(localProducts)
                }
            } catch (localError: Exception) {
                Log.e("AppRepository", "Error al obtener de BD local: ${localError.message}", localError)
            }

            Log.d("AppRepository", "BD local vacía o con error, intentando obtener de API...")
            val response = api.getProducts()
            Log.d("AppRepository", "Respuesta de la API: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val products = response.body()?.products?.map { ProductMapper.dtoToDomain(it) } ?: emptyList()
                Log.d("AppRepository", "Productos obtenidos de API: ${products.size}")

                updateProductsFromApi(products)
                Log.d("AppRepository", "Productos actualizados en BD local")

                Result.success(products)
            } else {
                Log.d("AppRepository", "Error en respuesta API: ${response.code()} - ${response.message()}")
                Result.failure(Exception("No hay datos disponibles"))
            }
        } catch (e: Exception) {
            Log.e("AppRepository", "Error al obtener productos: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun updateProductsFromApi(apiProducts: List<Product>) {
        return withContext(Dispatchers.IO) {
            try {
                val currentProducts = productDao.getAll()
                Log.d("AppRepository", "Productos actuales en BD: ${currentProducts.size}")

                val currentProductsMap = currentProducts.associateBy { it.id }

                val productsToSave = mutableListOf<pe.idat.e_commerce_ef.data.local.entity.LocalProduct>()

                for (apiProduct in apiProducts) {
                    val currentProduct = currentProductsMap[apiProduct.id]

                    if (currentProduct != null) {
                        val updatedProduct = currentProduct.copy(
                            name = apiProduct.name,
                            price = apiProduct.price,
                            description = apiProduct.description,
                            category = apiProduct.category,
                            image = apiProduct.image
                        )
                        productsToSave.add(updatedProduct)
                        Log.d("AppRepository", "🔄 Manteniendo stock local para producto ID ${apiProduct.id}: Stock local=${currentProduct.stock}, Stock API=${apiProduct.stock}")
                    } else {
                        productsToSave.add(ProductMapper.domainToEntity(apiProduct))
                        Log.d("AppRepository", "➕ Nuevo producto ID ${apiProduct.id} guardado desde API")
                    }
                }

                productDao.insertAll(productsToSave)
                Log.d("AppRepository", "✅ Productos guardados en BD: ${productsToSave.size}")

            } catch (e: Exception) {
                Log.e("AppRepository", "Error al actualizar productos desde API: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun updateProducts(products: List<Product>): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val entities = products.map { ProductMapper.domainToEntity(it) }
                productDao.insertAll(entities)
                Log.d("AppRepository", "✅ ${entities.size} productos actualizados en BD")
                Result.success(true)
            } catch (e: Exception) {
                Log.e("AppRepository", "Error al actualizar productos: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun updateProductStock(productId: Int, newStock: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val products = productDao.getAll()
                val product = products.find { it.id == productId }

                if (product != null) {
                    val updatedProduct = product.copy(stock = newStock)
                    productDao.insertAll(listOf(updatedProduct))
                    Log.d("AppRepository", "✅ Stock actualizado para producto ID $productId: $newStock")
                    Result.success(true)
                } else {
                    Log.d("AppRepository", "❌ Producto ID $productId no encontrado")
                    Result.failure(Exception("Producto no encontrado"))
                }
            } catch (e: Exception) {
                Log.e("AppRepository", "Error al actualizar stock: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    suspend fun clearLocalProducts() {
        productDao.deleteAll()
    }
}