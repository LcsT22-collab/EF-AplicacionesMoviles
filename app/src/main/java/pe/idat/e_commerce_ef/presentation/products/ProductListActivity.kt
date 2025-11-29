package pe.idat.e_commerce_ef.presentation.products

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import pe.idat.e_commerce_ef.databinding.ActivityProductListBinding
import pe.idat.e_commerce_ef.presentation.adapter.ProductAdapter
import pe.idat.e_commerce_ef.presentation.cart.CartActivity
import pe.idat.e_commerce_ef.presentation.main.MainActivity

@AndroidEntryPoint
class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val viewModel: ProductListViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { product, quantity ->
            viewModel.addToCart(product, quantity)
            Toast.makeText(this, "✅ ${product.name} (x$quantity) agregado al carrito", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@ProductListActivity)
            adapter = productAdapter
        }
    }

    private fun setupObservers() {
        viewModel.productsState.observe(this, Observer { state ->
            when (state) {
                is ProductsState.Loading -> {
                    // Mostrar loading si es necesario
                }
                is ProductsState.Success -> {
                    productAdapter.updateProducts(state.products)
                }
                is ProductsState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.cartCount.observe(this, Observer { count ->
            updateCartBadge(count)
        })
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            binding.tvCartCount.text = count.toString()
            binding.tvCartCount.visibility = android.view.View.VISIBLE
        } else {
            binding.tvCartCount.visibility = android.view.View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCartCount()
    }
}