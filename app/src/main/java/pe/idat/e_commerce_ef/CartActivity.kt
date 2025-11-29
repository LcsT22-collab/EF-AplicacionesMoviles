package pe.idat.e_commerce_ef

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pe.idat.e_commerce_ef.adapter.CartAdapter
import pe.idat.e_commerce_ef.network.CartManager

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var btnCheckout: Button
    private lateinit var tvTotal: TextView
    private lateinit var layoutEmpty: LinearLayout // Cambiado de TextView a LinearLayout

    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        setupToolbar()
        setupViews()
        loadCartItems()
        updateTotal()
    }

    private fun setupToolbar() {
        btnBack = findViewById(R.id.btnBackCart)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerViewCart)
        btnCheckout = findViewById(R.id.btnCheckout)
        tvTotal = findViewById(R.id.tvTotal)
        layoutEmpty = findViewById(R.id.tvEmptyCart) // Ahora es un LinearLayout

        cartAdapter = CartAdapter(CartManager.cartItems) { product ->
            removeFromCart(product)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }

        btnCheckout.setOnClickListener {
            checkout()
        }
    }

    private fun loadCartItems() {
        cartAdapter.updateCartItems(CartManager.cartItems)
        updateEmptyState()
    }

    private fun removeFromCart(product: pe.idat.e_commerce_ef.model.Product) {
        CartManager.removeFromCart(product)
        cartAdapter.updateCartItems(CartManager.cartItems)
        updateTotal()
        updateEmptyState()
        Toast.makeText(this, "❌ ${product.name} removido", Toast.LENGTH_SHORT).show()
    }

    private fun updateTotal() {
        val total = CartManager.getTotal()
        tvTotal.text = "Total: S/. ${String.format("%.2f", total)}"
    }

    private fun updateEmptyState() {
        if (CartManager.cartItems.isEmpty()) {
            layoutEmpty.visibility = LinearLayout.VISIBLE // Cambiado de TextView
            recyclerView.visibility = RecyclerView.GONE
            tvTotal.visibility = TextView.GONE
            btnCheckout.visibility = Button.GONE
        } else {
            layoutEmpty.visibility = LinearLayout.GONE // Cambiado de TextView
            recyclerView.visibility = RecyclerView.VISIBLE
            tvTotal.visibility = TextView.VISIBLE
            btnCheckout.visibility = Button.VISIBLE
        }
    }

    private fun checkout() {
        if (CartManager.cartItems.isNotEmpty()) {
            // Navegar a la actividad de checkout
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la vista si hay cambios
        loadCartItems()
        updateTotal()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Animación de transición
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}