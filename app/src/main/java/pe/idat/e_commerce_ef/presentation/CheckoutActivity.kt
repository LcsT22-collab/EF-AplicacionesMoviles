package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.idat.e_commerce_ef.R
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
        if (CartManager.items.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_cart_error), Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, getString(R.string.purchase_success_message), Toast.LENGTH_LONG).show()

        val purchasedItems = CartManager.items.toList()
        CartManager.clearCart()

        val intent = Intent(this, ProductListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("purchase_completed", true)
            putExtra("items_count", purchasedItems.size)
            putExtra("total_amount", CartManager.total)
        }
        startActivity(intent)
        finish()
    }

    private fun loadOrderSummary() {
        val total = CartManager.total
        val itemCount = CartManager.itemCount

        binding.tvCheckoutTotal.text = getString(R.string.format_price, total)
        binding.tvItemCount.text = itemCount.toString()

        val summary = StringBuilder()
        CartManager.items.forEachIndexed { index, product ->
            val productTotal = product.price * product.quantity
            val priceFormatted = getString(R.string.format_price, product.price)
            val subtotalFormatted = getString(R.string.format_price, productTotal)

            summary.append(
                getString(
                    R.string.order_summary_format,
                    index + 1,
                    product.name,
                    product.quantity,
                    priceFormatted,
                    subtotalFormatted
                )
            )
        }

        if (summary.isNotEmpty()) {
            binding.tvOrderSummary.text = summary.toString()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}