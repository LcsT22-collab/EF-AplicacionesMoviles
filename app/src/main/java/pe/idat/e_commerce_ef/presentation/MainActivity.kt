package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityMainBinding
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CORREGIR: Usar ViewModelProvider con un Factory
        val repository = AppRepository(applicationContext)
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        setupUI()
        setupObservers()

        // Verificar autenticación
        if (viewModel.getCurrentUser() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupUI() {
        binding.btnProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Mostrar usuario actual
        val user = viewModel.getCurrentUser()
        binding.tvUser.text = "Hola, ${user?.displayName ?: user?.email}"
    }

    private fun setupObservers() {
        viewModel.cartItems.observe(this) { items ->
            // Calcular la cantidad total de items en el carrito
            val totalQuantity = items.sumOf { it.quantity }
            binding.tvCartCount.text = totalQuantity.toString()
            binding.tvCartCount.visibility = if (totalQuantity > 0) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}

// AGREGAR ESTA CLASE EN EL MISMO ARCHIVO O EN UNO SEPARADO
class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}