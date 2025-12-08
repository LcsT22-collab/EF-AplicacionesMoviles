package pe.idat.e_commerce_ef.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pe.idat.e_commerce_ef.data.local.AppDatabase
import pe.idat.e_commerce_ef.data.remote.ProductService
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.util.ProductMapper

class AppRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val productDao = db.productDao()
    private val auth = FirebaseAuth.getInstance()
    private val api = ProductService.create()

    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val localProducts = productDao.getAll().map { ProductMapper.entityToDomain(it) }
            if (localProducts.isNotEmpty()) {
                return Result.success(localProducts)
            }

            val response = api.getProducts()

            if (response.isSuccessful) {
                val products = response.body()?.products?.map { ProductMapper.dtoToDomain(it) } ?: emptyList()
                updateProductsFromApi(products)
                Result.success(products)
            } else {
                Result.failure(Exception("No hay datos disponibles"))
            }
        } catch (e: Exception) {
            Log.e("AppRepository", "Error al obtener productos", e)
            Result.failure(e)
        }
    }

    private suspend fun updateProductsFromApi(apiProducts: List<Product>) {
        return withContext(Dispatchers.IO) {
            try {
                val currentProducts = productDao.getAll()
                val currentProductsMap = currentProducts.associateBy { it.id }

                val productsToSave = apiProducts.map { apiProduct ->
                    currentProductsMap[apiProduct.id]?.let { currentProduct ->
                        currentProduct.copy(
                            name = apiProduct.name,
                            price = apiProduct.price,
                            description = apiProduct.description,
                            category = apiProduct.category,
                            image = apiProduct.image
                        )
                    } ?: ProductMapper.domainToEntity(apiProduct)
                }

                productDao.insertAll(productsToSave)
            } catch (e: Exception) {
                Log.e("AppRepository", "Error al actualizar productos", e)
                throw e
            }
        }
    }

    suspend fun updateProducts(products: List<Product>): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val entities = products.map { ProductMapper.domainToEntity(it) }
                productDao.insertAll(entities)
                Result.success(true)
            } catch (e: Exception) {
                Log.e("AppRepository", "Error al actualizar productos", e)
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
}