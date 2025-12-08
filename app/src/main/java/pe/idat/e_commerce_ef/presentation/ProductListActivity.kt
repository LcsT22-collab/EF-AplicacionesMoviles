package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityProductListBinding
import pe.idat.e_commerce_ef.presentation.adapter.ProductAdapter
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModelFactory
import pe.idat.e_commerce_ef.util.CartManager

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var viewModel: AppViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AppRepository(applicationContext)
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        setupAdapter()
        setupObservers()
        setupListeners()

        viewModel.loadProducts()
        updateCartCounterFromViewModel()

        checkPurchaseResult()
    }

    private fun setupAdapter() {
        productAdapter = ProductAdapter(
            onItemClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("product", product)
                }
                startActivity(intent)
            },
            onAddToCart = { product ->
                val success = viewModel.addToCart(product, 1)
                if (success) {
                    Toast.makeText(
                        this,
                        getString(R.string.add_to_cart_success, product.name),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "No hay stock suficiente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        binding.recyclerViewProducts.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewProducts.adapter = productAdapter
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            binding.progressBar.visibility = View.GONE
            productAdapter.submitList(products)

            if (products.isEmpty()) {
                binding.tvNoProducts.visibility = View.VISIBLE
                binding.recyclerViewProducts.visibility = View.GONE
            } else {
                binding.tvNoProducts.visibility = View.GONE
                binding.recyclerViewProducts.visibility = View.VISIBLE
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.cartItems.observe(this) { items ->
            updateCartCounter(items)
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
                binding.tvNoProducts.visibility = View.VISIBLE
                binding.recyclerViewProducts.visibility = View.GONE
            }
        }

        viewModel.purchaseResult.observe(this) { result ->
            if (result.first) {
                viewModel.loadProducts()
                Toast.makeText(this, result.second ?: "Compra realizada", Toast.LENGTH_LONG).show()
            } else if (result.second != null) {
                Toast.makeText(this, result.second, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkPurchaseResult() {
        if (intent.getBooleanExtra("purchase_completed", false)) {
            Toast.makeText(this, "Compra realizada exitosamente", Toast.LENGTH_LONG).show()
            viewModel.loadProducts()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun updateCartCounter(items: List<pe.idat.e_commerce_ef.domain.model.Product>) {
        val totalQuantity = items.sumOf { it.quantity }
        if (totalQuantity > 0) {
            binding.tvCartCount.text = totalQuantity.toString()
            binding.tvCartCount.visibility = View.VISIBLE
        } else {
            binding.tvCartCount.visibility = View.GONE
        }
    }

    private fun updateCartCounterFromViewModel() {
        viewModel.updateCart()
    }

    override fun onResume() {
        super.onResume()
        updateCartCounterFromViewModel()
        viewModel.loadProducts()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}