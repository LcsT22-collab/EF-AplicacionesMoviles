package pe.idat.e_commerce_ef.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import pe.idat.e_commerce_ef.data.local.AppDatabase
import java.io.File

object DatabaseDebugHelper {

    fun checkDatabaseStatus(context: Context) {
        try {
            // Verificar archivo físico
            val dbPath = context.getDatabasePath("ecommerce_database.db").absolutePath
            val dbFile = File(dbPath)

            Log.d("DB_DEBUG", "=".repeat(50))
            Log.d("DB_DEBUG", "📁 VERIFICACIÓN DE BASE DE DATOS")
            Log.d("DB_DEBUG", "=".repeat(50))
            Log.d("DB_DEBUG", "📍 Ruta: $dbPath")
            Log.d("DB_DEBUG", "📂 ¿Existe?: ${dbFile.exists()}")

            if (dbFile.exists()) {
                Log.d("DB_DEBUG", "📏 Tamaño: ${dbFile.length()} bytes")
                Log.d("DB_DEBUG", "📅 Última modificación: ${dbFile.lastModified()}")

                // Intentar abrir con SQLite directamente
                val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
                val tables = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)

                val tableList = mutableListOf<String>()
                while (tables.moveToNext()) {
                    tableList.add(tables.getString(0))
                }
                tables.close()
                db.close()

                Log.d("DB_DEBUG", "📊 Tablas encontradas: $tableList")
                Log.d("DB_DEBUG", "✅ ¿Tabla 'products' existe?: ${tableList.contains("products")}")

                if (tableList.contains("products")) {
                    val db2 = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
                    val cursor = db2.rawQuery("SELECT COUNT(*) FROM products", null)
                    cursor.moveToFirst()
                    val count = cursor.getInt(0)
                    cursor.close()
                    db2.close()
                    Log.d("DB_DEBUG", "📈 Registros en 'products': $count")
                }
            } else {
                Log.d("DB_DEBUG", "❌ Archivo de base de datos NO EXISTE")
                createDatabaseManually(context)
            }

        } catch (e: Exception) {
            Log.e("DB_DEBUG", "💥 ERROR en checkDatabaseStatus: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun createDatabaseManually(context: Context) {
        Log.d("DB_DEBUG", "🛠️ Intentando crear base de datos manualmente...")

        try {
            // Forzar creación usando Room
            val tempDb = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "temp_creation"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()

            // Ejecutar consulta simple
            tempDb.query("SELECT 1", null).close()

            Log.d("DB_DEBUG", "✅ Base de datos temporal creada")

            // Crear la real
            val realDb = AppDatabase.getInstance(context)
            Log.d("DB_DEBUG", "✅ Instancia de AppDatabase obtenida")

            tempDb.close()
            context.deleteDatabase("temp_creation")

        } catch (e: Exception) {
            Log.e("DB_DEBUG", "❌ Error creando BD: ${e.message}")
        }
    }

    fun resetDatabase(context: Context) {
        Log.d("DB_DEBUG", "🔄 RESETEO DE BASE DE DATOS")

        try {
            // Cerrar instancia actual
            AppDatabase.forceClose()

            // Eliminar archivo físico
            val dbPath = context.getDatabasePath("ecommerce_database.db")
            if (dbPath.exists()) {
                val deleted = dbPath.delete()
                Log.d("DB_DEBUG", "🗑️ Archivo eliminado: $deleted")
            }

            // Crear nueva instancia
            val newDb = AppDatabase.getInstance(context)
            Log.d("DB_DEBUG", "✅ Nueva base de datos creada")

            Thread.sleep(1000) // Esperar un momento

            // Verificar que funciona - CORREGIDO: usar productDao()
            runBlocking {
                try {
                    val count = newDb.productDao().getAllProducts().size
                    Log.d("DB_DEBUG", "✅ Room funciona! Productos: $count")
                } catch (e: Exception) {
                    Log.e("DB_DEBUG", "❌ Error consultando productos: ${e.message}")
                }
            }

        } catch (e: Exception) {
            Log.e("DB_DEBUG", "❌ Error en reset: ${e.message}")
        }
    }
}