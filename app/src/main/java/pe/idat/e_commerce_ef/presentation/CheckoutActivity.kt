package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityCheckoutBinding
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModelFactory
import pe.idat.e_commerce_ef.util.CartManager

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AppRepository(applicationContext)
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        setupToolbar()
        setupClickListeners()
        loadOrderSummary()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.btnBackCheckout.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupObservers() {
        viewModel.purchaseResult.observe(this) { result ->
            if (result.first) {
                Toast.makeText(this, result.second ?: "Compra realizada", Toast.LENGTH_LONG).show()

                val intent = Intent(this, ProductListActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("purchase_completed", true)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, result.second ?: "Error en la compra", Toast.LENGTH_LONG).show()
            }
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

        viewModel.processPurchase()
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