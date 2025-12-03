package pe.idat.e_commerce_ef.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import pe.idat.e_commerce_ef.data.local.dao.ProductDao
import pe.idat.e_commerce_ef.data.local.entity.LocalProduct

@Database(
    entities = [LocalProduct::class],
    version = 2, // ¡INCREMENTAR VERSIÓN!
    exportSchema = true // Cambiar a true para debugging
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.d("ROOM_DEBUG", "🔄 Creando/Actualizando base de datos Room...")

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ecommerce_database.db" // Añadir extensión .db
                )
                    .fallbackToDestructiveMigration() // Esto destruye y recrea si hay problemas
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("ROOM_DEBUG", "✅ Base de datos creada exitosamente")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.d("ROOM_DEBUG", "✅ Base de datos abierta exitosamente")
                        }
                    })
                    .build()

                Log.d("ROOM_DEBUG", "✅ Base de datos lista")
                INSTANCE = instance
                instance
            }
        }

        // Método para debug
        fun forceClose() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}