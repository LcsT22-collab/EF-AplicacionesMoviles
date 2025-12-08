package pe.idat.e_commerce_ef.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil.load
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityProductDetailBinding
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModelFactory
import pe.idat.e_commerce_ef.util.CartManager

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var currentProduct: Product? = null
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AppRepository(applicationContext)
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        setupListeners()
        loadProductDetails()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            currentProduct?.let { oldProduct ->
                val updatedProduct = products.find { it.id == oldProduct.id }
                updatedProduct?.let {
                    currentProduct = it
                    updateStockDisplay(it.stock)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBackDetail.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddToCartDetail.setOnClickListener {
            currentProduct?.let { product ->
                addToCart(product)
            }
        }
    }

    private fun loadProductDetails() {
        currentProduct = intent.getSerializableExtra("product") as? Product

        currentProduct?.let { product ->
            binding.tvProductTitle.text = product.name
            binding.tvProductPrice.text = getString(R.string.format_price, product.price)
            binding.tvProductCategory.text = product.category
            binding.tvProductDescription.text = product.description

            updateStockDisplay(product.stock)

            binding.ivProductDetail.load(product.image) {
                crossfade(true)
            }
        } ?: run {
            Toast.makeText(this, getString(R.string.error_load_product), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateStockDisplay(stock: Int) {
        if (stock > 0) {
            binding.tvProductStock.text = getString(R.string.stock_label, stock)
            binding.tvProductStock.setTextColor(ContextCompat.getColor(this, R.color.success_green))
            binding.btnAddToCartDetail.isEnabled = true
        } else {
            binding.tvProductStock.text = "Sin stock"
            binding.tvProductStock.setTextColor(ContextCompat.getColor(this, R.color.error_red))
            binding.btnAddToCartDetail.isEnabled = false
        }
    }

    private fun addToCart(product: Product) {
        if (product.stock > 0) {
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
        } else {
            Toast.makeText(
                this,
                "No hay stock disponible para ${product.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProducts()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }
}