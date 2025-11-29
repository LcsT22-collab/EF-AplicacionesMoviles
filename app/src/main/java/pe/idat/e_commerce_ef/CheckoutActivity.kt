package pe.idat.e_commerce_ef

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.idat.e_commerce_ef.network.CartManager

class CheckoutActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnConfirmPurchase: Button
    private lateinit var tvTotal: TextView
    private lateinit var tvItemCount: TextView
    private lateinit var tvOrderSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Inicializar todas las vistas primero
        initViews()
        setupToolbar()
        setupClickListeners()
        loadOrderSummary()
    }

    private fun initViews() {
        // Buscar las vistas por sus IDs
        btnBack = findViewById(R.id.btnBackCheckout)
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase)
        tvTotal = findViewById(R.id.tvCheckoutTotal)
        tvItemCount = findViewById(R.id.tvItemCount)
        tvOrderSummary = findViewById(R.id.tvOrderSummary)
    }

    private fun setupToolbar() {
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupClickListeners() {
        btnConfirmPurchase.setOnClickListener {
            confirmPurchase()
        }
    }

    private fun loadOrderSummary() {
        val total = CartManager.getTotal()
        val itemCount = CartManager.getItemCount()

        tvTotal.text = "Total: S/. ${String.format("%.2f", total)}"
        tvItemCount.text = "Items: $itemCount"

        // Crear resumen de productos
        val summary = StringBuilder()
        CartManager.cartItems.forEach { product ->
            val productTotal = product.price * product.selectedQuantity
            summary.append("• ${product.name} - S/. ${String.format("%.2f", product.price)} x${product.selectedQuantity} = S/. ${String.format("%.2f", productTotal)}\n")
        }
        tvOrderSummary.text = summary.toString()
    }

    private fun confirmPurchase() {
        if (CartManager.cartItems.isNotEmpty()) {
            val total = CartManager.getTotal()

            // Simular proceso de pago
            Toast.makeText(this, "✅ Compra realizada! Total: S/. ${String.format("%.2f", total)}", Toast.LENGTH_LONG).show()

            // Limpiar carrito
            CartManager.clearCart()

            // Regresar a la actividad principal
            val intent = Intent(this, ProductListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}