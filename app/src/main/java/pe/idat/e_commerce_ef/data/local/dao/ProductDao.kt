package pe.idat.e_commerce_ef.data.local.dao

import androidx.room.*
import pe.idat.e_commerce_ef.data.local.entity.LocalProduct

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<LocalProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<LocalProduct>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}