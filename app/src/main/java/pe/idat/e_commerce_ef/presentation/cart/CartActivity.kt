package pe.idat.e_commerce_ef.presentation.cart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pe.idat.e_commerce_ef.databinding.ActivityCartBinding
import pe.idat.e_commerce_ef.presentation.adapter.CartAdapter
import pe.idat.e_commerce_ef.presentation.chekout.CheckoutActivity

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        cartAdapter = CartAdapter(emptyList()) { product ->
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
        viewModel.cartState.observe(this, Observer { state ->
            cartAdapter.updateCartItems(state.items)
            updateTotal(state.total)
            updateEmptyState(state.isEmpty)
        })
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
        if (viewModel.cartState.value?.isEmpty == false) {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCartItems()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}