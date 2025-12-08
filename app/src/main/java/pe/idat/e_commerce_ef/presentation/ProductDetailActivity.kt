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
import kotlin.random.Random

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var currentProduct: Product? = null
    private lateinit var viewModel: AppViewModel
    private var currentRating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AppRepository(applicationContext)
        viewModel = ViewModelProvider(this, AppViewModelFactory(repository))[AppViewModel::class.java]

        setupListeners()
        loadProductDetails()
        setupRatingStars()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            currentProduct?.let { oldProduct ->
                products.find { it.id == oldProduct.id }?.let {
                    currentProduct = it
                    updateStockDisplay(it.stock)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBackDetail.setOnClickListener { onBackPressed() }
        binding.btnAddToCartDetail.setOnClickListener { currentProduct?.let { addToCart(it) } }

        // Listeners para las estrellas
        binding.star1.setOnClickListener { setRating(1) }
        binding.star2.setOnClickListener { setRating(2) }
        binding.star3.setOnClickListener { setRating(3) }
        binding.star4.setOnClickListener { setRating(4) }
        binding.star5.setOnClickListener { setRating(5) }
    }

    private fun setupRatingStars() {
        // Generar una valoración aleatoria entre 3 y 5 estrellas
        currentRating = Random.nextInt(3, 6)
        updateStarsDisplay()
    }

    private fun setRating(rating: Int) {
        currentRating = rating
        updateStarsDisplay()
        Toast.makeText(this, "Valoración: $rating estrellas", Toast.LENGTH_SHORT).show()
    }

    private fun updateStarsDisplay() {
        // Actualizar el color de las estrellas según la valoración actual
        val stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)

        stars.forEachIndexed { index, star ->
            if (index < currentRating) {
                star.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                star.setTextColor(ContextCompat.getColor(this, R.color.light_gray))
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
            binding.ivProductDetail.load(product.image) { crossfade(true) }
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
            binding.btnAddToCartDetail.text = getString(R.string.add_to_cart_button)
        } else {
            binding.tvProductStock.text = "Sin stock"
            binding.tvProductStock.setTextColor(ContextCompat.getColor(this, R.color.error_red))
            binding.btnAddToCartDetail.isEnabled = false
            binding.btnAddToCartDetail.text = "SIN STOCK"
        }
    }

    private fun addToCart(product: Product) {
        if (product.stock > 0) {
            if (viewModel.addToCart(product, 1)) {
                Toast.makeText(this, getString(R.string.add_to_cart_success, product.name), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No hay stock suficiente", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No hay stock disponible", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProducts()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}