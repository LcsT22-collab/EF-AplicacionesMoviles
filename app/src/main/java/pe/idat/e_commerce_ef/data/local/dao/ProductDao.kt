package pe.idat.e_commerce_ef.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pe.idat.e_commerce_ef.data.local.entity.LocalProduct

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<LocalProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<LocalProduct>)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): LocalProduct?
}