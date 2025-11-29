// ACTUALIZAR ProductDetailActivity.kt
package pe.idat.e_commerce_ef

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import pe.idat.e_commerce_ef.model.Product
import pe.idat.e_commerce_ef.network.CartManager

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var ivProduct: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvStock: TextView
    private lateinit var btnAddToCart: Button
    private lateinit var btnBack: ImageView

    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        setupViews()
        loadProductDetails()
    }

    private fun setupViews() {
        ivProduct = findViewById(R.id.ivProductDetail)
        tvTitle = findViewById(R.id.tvProductTitle)
        tvPrice = findViewById(R.id.tvProductPrice)
        tvCategory = findViewById(R.id.tvProductCategory)
        tvDescription = findViewById(R.id.tvProductDescription)
        tvStock = findViewById(R.id.tvProductStock)
        btnAddToCart = findViewById(R.id.btnAddToCartDetail)
        btnBack = findViewById(R.id.btnBackDetail)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnAddToCart.setOnClickListener {
            currentProduct?.let { product ->
                addToCart(product)
            }
        }
    }

    private fun loadProductDetails() {
        currentProduct = intent.getSerializableExtra("product") as? Product

        currentProduct?.let { product ->
            tvTitle.text = product.name
            tvPrice.text = "S/. ${String.format("%.2f", product.price)}"
            tvCategory.text = product.category
            tvDescription.text = product.description
            tvStock.text = "Stock: ${product.stock}"

            Glide.with(this)
                .load(product.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivProduct)
        }
    }

    private fun addToCart(product: Product) {
        CartManager.addToCart(product)
        Toast.makeText(this, "✅ ${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
    }
}