package pe.idat.e_commerce_ef.presentation.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.data.local.AppDatabase
import pe.idat.e_commerce_ef.presentation.login.LoginActivity
import pe.idat.e_commerce_ef.presentation.products.ProductListActivity
import pe.idat.e_commerce_ef.util.DatabaseDebugHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnGoToStore: Button
    private lateinit var auth: FirebaseAuth

    private lateinit var btnDebugDB: Button


    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MAIN_ACTIVITY", "=".repeat(50))
        Log.d("MAIN_ACTIVITY", "🚀 INICIANDO MAIN ACTIVITY")
        Log.d("MAIN_ACTIVITY", "=".repeat(50))

        // Inicializar Firebase
        auth = Firebase.auth

        // Enlazar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        tvEmail = findViewById(R.id.tvEmail)
        btnLogout = findViewById(R.id.btnLogout)
        btnGoToStore = findViewById(R.id.btnGoToStore)

        btnDebugDB = findViewById(R.id.btnDebugDB)
        btnDebugDB.setOnClickListener {
            DatabaseDebugHelper.resetDatabase(this)
            Toast.makeText(this, "Base de datos reseteada", Toast.LENGTH_SHORT).show()
        }

        // Verificar autenticación
        checkAuthentication()

        // Configurar botones
        setupButtons()

        // DEBUG: Verificar estado de la base de datos
        debugDatabase()
    }



    private fun debugDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("DB_TEST", "🔍 Iniciando pruebas de Room...")

                // 1. Verificar estado
                DatabaseDebugHelper.checkDatabaseStatus(this@MainActivity)

                // 2. Probar consulta directa
                val products = database.productDao().getAllProducts()
                Log.d("DB_TEST", "✅ Consulta Room exitosa! Productos: ${products.size}")

                // 3. Mostrar detalles si hay productos
                if (products.isNotEmpty()) {
                    products.take(3).forEach { product ->
                        Log.d("DB_TEST", "   📦 ${product.id}: ${product.name}")
                    }
                }

                // 4. Probar inserción de prueba
                if (products.isEmpty()) {
                    Log.d("DB_TEST", "ℹ️ Base de datos vacía - esto es normal en primera ejecución")

                    // Puedes agregar un producto de prueba si quieres
                    val testProduct = pe.idat.e_commerce_ef.data.local.entity.LocalProduct(
                        id = 999,
                        name = "Producto de prueba",
                        price = 9.99,
                        description = "Producto para verificar la BD",
                        category = "test",
                        image = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                        stock = 10,
                        lastUpdated = System.currentTimeMillis()
                    )

                    database.productDao().insertProducts(listOf(testProduct))
                    Log.d("DB_TEST", "✅ Producto de prueba insertado")

                    // Verificar que se guardó
                    val newCount = database.productDao().getAllProducts().size
                    Log.d("DB_TEST", "📈 Nuevo total de productos: $newCount")
                }

            } catch (e: Exception) {
                Log.e("DB_TEST", "❌ ERROR CRÍTICO en Room: ${e.message}")
                e.printStackTrace()

                // Mostrar en UI
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error en base de datos: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Intentar resetear
                DatabaseDebugHelper.resetDatabase(this@MainActivity)
            }
        }
    }

    private fun checkAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            goToLoginActivity()
        } else {
            showUserInfo(currentUser)
        }
    }

    private fun showUserInfo(user: FirebaseUser) {
        val welcomeText = if (user.displayName?.isNotEmpty() == true) {
            "¡Bienvenido, ${user.displayName}!"
        } else {
            "¡Bienvenido!"
        }

        tvWelcome.text = welcomeText
        tvEmail.text = "Email: ${user.email}"
    }

    private fun setupButtons() {
        btnLogout.setOnClickListener {
            auth.signOut()
            showToast("Sesión cerrada")
            goToLoginActivity()
        }

        btnGoToStore.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
}