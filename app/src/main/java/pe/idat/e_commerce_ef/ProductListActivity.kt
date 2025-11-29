package pe.idat.e_commerce_ef

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.idat.e_commerce_ef.adapter.ProductAdapter
import pe.idat.e_commerce_ef.model.Product
import pe.idat.e_commerce_ef.network.CartManager
import pe.idat.e_commerce_ef.repository.ProductRepository

class ProductListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var btnBack: ImageButton
    private lateinit var btnCart: ImageButton
    private lateinit var tvCartCount: TextView

    private val productRepository = ProductRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        setupToolbar()
        setupRecyclerView()
        loadProducts()
        updateCartBadge()
    }

    private fun setupToolbar() {
        btnBack = findViewById(R.id.btnBack)
        btnCart = findViewById(R.id.btnCart)
        tvCartCount = findViewById(R.id.tvCartCount)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewProducts)
        productAdapter = ProductAdapter(emptyList()) { product, quantity ->
            addToCart(product, quantity)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProductListActivity)
            adapter = productAdapter
            setPadding(0, 16, 0, 16)
        }
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("DEBUG", "🟡 Iniciando carga de productos...")

                val result = productRepository.getProducts()

                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        val products = result.getOrNull() ?: emptyList()

                        Log.d("DEBUG", "✅ ÉXITO: ${products.size} productos cargados")

                        if (products.isEmpty()) {
                            Toast.makeText(
                                this@ProductListActivity,
                                "⚠️ No hay productos disponibles",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            productAdapter.updateProducts(products)
                        }
                    } else {
                        val exception = result.exceptionOrNull()
                        Log.e("DEBUG", "❌ Error: ${exception?.message}")
                        Toast.makeText(
                            this@ProductListActivity,
                            "❌ Error al cargar productos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("DEBUG", "🔥 Error de conexión: ${e.message}")
                    Toast.makeText(
                        this@ProductListActivity,
                        "🌐 Error de conexión",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun addToCart(product: Product, quantity: Int) {
        CartManager.addToCart(product, quantity)
        updateCartBadge()
        Toast.makeText(this, "✅ ${product.name} (x$quantity) agregado al carrito", Toast.LENGTH_SHORT).show()
    }

    private fun updateCartBadge() {
        val itemCount = CartManager.getItemCount()
        if (itemCount > 0) {
            tvCartCount.text = itemCount.toString()
            tvCartCount.visibility = TextView.VISIBLE
        } else {
            tvCartCount.visibility = TextView.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }
}