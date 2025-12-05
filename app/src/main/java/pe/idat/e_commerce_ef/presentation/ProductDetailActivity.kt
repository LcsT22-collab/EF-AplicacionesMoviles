package pe.idat.e_commerce_ef.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.databinding.ActivityProductDetailBinding
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.util.CartManager

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        loadProductDetails()
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
            binding.tvProductStock.text = getString(R.string.stock_label, product.stock)

            binding.ivProductDetail.load(product.image) {
                crossfade(true)
            }
        } ?: run {
            Toast.makeText(this, getString(R.string.error_load_product), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun addToCart(product: Product) {
        CartManager.addToCart(product)
        Toast.makeText(
            this,
            getString(R.string.add_to_cart_success, product.name),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }
}