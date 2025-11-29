package pe.idat.e_commerce_ef.presentation.chekout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.idat.e_commerce_ef.databinding.ActivityCheckoutBinding
import pe.idat.e_commerce_ef.presentation.products.ProductListActivity
import pe.idat.e_commerce_ef.util.CartManager

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        loadOrderSummary()
    }

    private fun setupToolbar() {
        binding.btnBackCheckout.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupClickListeners() {
        binding.btnConfirmPurchase.setOnClickListener {
            confirmPurchase()
        }
    }

    private fun loadOrderSummary() {
        val total = CartManager.getTotal()
        val itemCount = CartManager.getItemCount()

        binding.tvCheckoutTotal.text = "Total: S/. ${String.format("%.2f", total)}"
        binding.tvItemCount.text = "Items: $itemCount"

        val summary = StringBuilder()
        CartManager.cartItems.forEach { product ->
            val productTotal = product.price * product.selectedQuantity
            summary.append("• ${product.name} - S/. ${String.format("%.2f", product.price)} x${product.selectedQuantity} = S/. ${String.format("%.2f", productTotal)}\n")
        }
        binding.tvOrderSummary.text = summary.toString()
    }

    private fun confirmPurchase() {
        if (CartManager.cartItems.isNotEmpty()) {
            val total = CartManager.getTotal()
            Toast.makeText(this, "✅ Compra realizada! Total: S/. ${String.format("%.2f", total)}", Toast.LENGTH_LONG).show()
            CartManager.clearCart()

            val intent = Intent(this, ProductListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }
    }
}