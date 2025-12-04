package pe.idat.e_commerce_ef.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import pe.idat.e_commerce_ef.data.local.dao.ProductDao
import pe.idat.e_commerce_ef.data.local.entity.LocalProduct

@Database(
    entities = [LocalProduct::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "ecommerce_db"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}
