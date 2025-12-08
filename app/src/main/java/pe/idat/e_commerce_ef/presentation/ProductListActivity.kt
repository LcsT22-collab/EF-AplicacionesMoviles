package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
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
        viewModel = ViewModelProvider(this, AppViewModelFactory(repository))[AppViewModel::class.java]

        setupAdapter()
        setupObservers()
        setupListeners()

        viewModel.loadProducts()
        updateCartCounter()

        checkPurchaseResult()
    }

    private fun setupAdapter() {
        productAdapter = ProductAdapter(
            onItemClick = { product ->
                startActivity(Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("product", product)
                })
            },
            onAddToCart = { product ->
                if (viewModel.addToCart(product, 1)) {
                    Toast.makeText(this, getString(R.string.add_to_cart_success, product.name), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No hay stock suficiente", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.recyclerViewProducts.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewProducts.adapter = productAdapter
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            binding.progressBar.visibility = android.view.View.GONE
            productAdapter.submitList(products)

            val isEmpty = products.isEmpty()
            binding.tvNoProducts.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
            binding.recyclerViewProducts.visibility = if (isEmpty) android.view.View.GONE else android.view.View.VISIBLE
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.cartItems.observe(this) { updateCartCounter() }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = android.view.View.GONE
                binding.tvNoProducts.visibility = android.view.View.VISIBLE
                binding.recyclerViewProducts.visibility = android.view.View.GONE
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
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnCart.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
    }

    private fun updateCartCounter() {
        val totalQuantity = CartManager.items.sumOf { it.quantity }
        if (totalQuantity > 0) {
            binding.tvCartCount.text = totalQuantity.toString()
            binding.tvCartCount.visibility = android.view.View.VISIBLE
        } else {
            binding.tvCartCount.visibility = android.view.View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartCounter()
        viewModel.loadProducts()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}