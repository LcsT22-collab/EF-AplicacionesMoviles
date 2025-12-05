package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityProductListBinding
import pe.idat.e_commerce_ef.presentation.adapter.ProductAdapter
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModelFactory

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var viewModel: AppViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Usar ViewModelProvider con Factory
        val repository = AppRepository(applicationContext)
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Cargar productos al iniciar
        viewModel.loadProducts()

        // Resetear el contador cuando se crea la actividad
        resetCartCounter()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter { product, quantity ->
            viewModel.addToCart(product, quantity)
            Toast.makeText(this, "✅ ${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@ProductListActivity)
            adapter = this@ProductListActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            adapter.submitList(products)
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // IMPORTANTE: Observar los cambios del carrito
        viewModel.cartItems.observe(this) { items ->
            updateCartCounter(items)
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    // Metodo para actualizar el contador del carrito
    private fun updateCartCounter(items: List<pe.idat.e_commerce_ef.domain.model.Product>) {
        val totalQuantity = items.sumOf { it.quantity }
        if (totalQuantity > 0) {
            binding.tvCartCount.text = totalQuantity.toString()
            binding.tvCartCount.visibility = View.VISIBLE
        } else {
            binding.tvCartCount.visibility = View.GONE
        }
    }

    // Metodo para resetear el contador
    private fun resetCartCounter() {
        binding.tvCartCount.visibility = View.GONE
        // También forzamos una actualización del carrito en el ViewModel
        viewModel.updateCart()
    }

    // Sobrescribir onResume para resetear cuando se vuelve a esta actividad
    override fun onResume() {
        super.onResume()
        // Resetear contador cada vez que la actividad se hace visible
        resetCartCounter()
    }
}