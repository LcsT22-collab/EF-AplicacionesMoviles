package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityCartBinding
import pe.idat.e_commerce_ef.presentation.adapter.CartAdapter
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel
import pe.idat.e_commerce_ef.util.CartManager

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: AppViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CORREGIR: Usar ViewModelProvider con Factory
        val repository = AppRepository(applicationContext)
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        setupViews()
        setupObservers()
    }
    private fun setupViews() {
        cartAdapter = CartAdapter(emptyList()) { product ->
            // CORREGIR: Usar viewModel.removeFromCart
            viewModel.removeFromCart(product)
            Toast.makeText(this, "❌ ${product.name} removido", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }

        binding.btnBackCart.setOnClickListener {
            onBackPressed()
        }

        binding.btnCheckout.setOnClickListener {
            checkout()
        }
    }

    private fun setupObservers() {
        // CORREGIR: Observar cartItems del AppViewModel
        viewModel.cartItems.observe(this) { items ->
            cartAdapter.updateCartItems(items)
            updateTotal(CartManager.total)
            updateEmptyState(items.isEmpty())
        }
    }

    private fun updateTotal(total: Double) {
        binding.tvTotal.text = "Total: S/. ${String.format("%.2f", total)}"
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvEmptyCart.visibility = android.view.View.VISIBLE
            binding.recyclerViewCart.visibility = android.view.View.GONE
            binding.tvTotal.visibility = android.view.View.GONE
            binding.btnCheckout.visibility = android.view.View.GONE
        } else {
            binding.tvEmptyCart.visibility = android.view.View.GONE
            binding.recyclerViewCart.visibility = android.view.View.VISIBLE
            binding.tvTotal.visibility = android.view.View.VISIBLE
            binding.btnCheckout.visibility = android.view.View.VISIBLE
        }
    }

    private fun checkout() {
        if (CartManager.items.isNotEmpty()) {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar carrito cuando vuelve a la actividad
        viewModel.updateCart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}