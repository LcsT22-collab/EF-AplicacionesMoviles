package pe.idat.e_commerce_ef.data

import android.content.Context
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

    // Funciones de productos
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = api.getProducts()
            if (response.isSuccessful) {
                val products = response.body()?.products?.map { ProductMapper.dtoToDomain(it) } ?: emptyList()
                productDao.insertAll(products.map { ProductMapper.domainToEntity(it) })
                Result.success(products)
            } else {
                val localProducts = productDao.getAll().map { ProductMapper.entityToDomain(it) }
                if (localProducts.isNotEmpty()) {
                    Result.success(localProducts)
                } else {
                    Result.failure(Exception("No hay datos disponibles"))
                }
            }
        } catch (e: Exception) {
            try {
                val localProducts = productDao.getAll().map { ProductMapper.entityToDomain(it) }
                Result.success(localProducts)
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    // Funciones de autenticación
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

    // AGREGADO: Metodo logout
    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    // Limpiar datos locales
    suspend fun clearLocalProducts() {
        productDao.deleteAll()
    }
}