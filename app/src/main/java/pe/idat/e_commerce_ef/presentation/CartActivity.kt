package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityCartBinding
import pe.idat.e_commerce_ef.presentation.adapter.CartAdapter
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModelFactory
import pe.idat.e_commerce_ef.util.CartManager

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: AppViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AppRepository(applicationContext)
        viewModel = ViewModelProvider(this, AppViewModelFactory(repository))[AppViewModel::class.java]

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        cartAdapter = CartAdapter(emptyList()) { product ->
            viewModel.removeFromCart(product)
            Toast.makeText(this, getString(R.string.remove_from_cart_success, product.name), Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }

        binding.btnBackCart.setOnClickListener { onBackPressed() }
        binding.btnCheckout.setOnClickListener { checkout() }
    }

    private fun setupObservers() {
        viewModel.cartItems.observe(this) { items ->
            cartAdapter.updateCartItems(items)
            binding.tvTotal.text = getString(R.string.format_price, CartManager.total)

            val isEmpty = items.isEmpty()
            binding.tvEmptyCart.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
            binding.recyclerViewCart.visibility = if (isEmpty) android.view.View.GONE else android.view.View.VISIBLE
            binding.btnCheckout.visibility = if (isEmpty) android.view.View.GONE else android.view.View.VISIBLE
        }
    }

    private fun checkout() {
        if (CartManager.items.isNotEmpty()) {
            startActivity(Intent(this, CheckoutActivity::class.java))
        } else {
            Toast.makeText(this, getString(R.string.empty_cart_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}