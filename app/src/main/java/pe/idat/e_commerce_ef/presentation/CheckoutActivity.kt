package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.idat.e_commerce_ef.databinding.ActivityCheckoutBinding
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

    private fun confirmPurchase() {
        CartManager.clearCart()
        Toast.makeText(this, "✅ Compra realizada exitosamente", Toast.LENGTH_LONG).show()

        val intent = Intent(this, ProductListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun loadOrderSummary() {
        val total = CartManager.total
        val itemCount = CartManager.itemCount

        binding.tvCheckoutTotal.text = "Total: S/. ${String.format("%.2f", total)}"
        binding.tvItemCount.text = "Items: $itemCount"

        val summary = StringBuilder()
        CartManager.items.forEach { product ->
            val productTotal = product.price * product.quantity
            summary.append(
                "• ${product.name} - S/. ${
                    String.format(
                        "%.2f",
                        product.price
                    )
                } x${product.quantity} = S/. ${String.format("%.2f", productTotal)}\n"
            )
        }
        binding.tvOrderSummary.text = summary.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}