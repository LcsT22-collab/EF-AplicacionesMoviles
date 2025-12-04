package pe.idat.e_commerce_ef.presentation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.idat.e_commerce_ef.databinding.ActivityCheckoutBinding
import pe.idat.e_commerce_ef.databinding.DialogComprobanteSelectionBinding
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
            showComprobanteSelectionDialog()
        }
    }

    private fun showComprobanteSelectionDialog() {
        val dialogBinding = DialogComprobanteSelectionBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Continuar") { dialog, _ ->
                val tipoComprobante = if (dialogBinding.radioFactura.isChecked) "FACTURA" else "BOLETA"

                if (tipoComprobante == "FACTURA") {
                    val razonSocial = dialogBinding.etRazonSocial.text.toString().trim()
                    val ruc = dialogBinding.etRuc.text.toString().trim()
                    val direccion = dialogBinding.etDireccionFiscal.text.toString().trim()

                    if (razonSocial.isEmpty() || ruc.isEmpty() || direccion.isEmpty()) {
                        Toast.makeText(this, "Complete todos los campos para factura", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    if (ruc.length != 11) {
                        Toast.makeText(this, "El RUC debe tener 11 dígitos", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    generarComprobante(tipoComprobante, razonSocial, ruc, direccion)
                } else {
                    generarComprobante(tipoComprobante, "", "", "")
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogBinding.radioGroupComprobante.setOnCheckedChangeListener { group, checkedId ->
            dialogBinding.layoutFactura.visibility = if (checkedId == dialogBinding.radioFactura.id) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }

        dialog.show()
    }

    private fun generarComprobante(tipoComprobante: String, razonSocial: String, ruc: String, direccionFiscal: String) {
        val intent = Intent(this, ComprobanteActivity::class.java).apply {
            putExtra("tipoComprobante", tipoComprobante)
            putExtra("razonSocial", razonSocial)
            putExtra("ruc", ruc)
            putExtra("direccionFiscal", direccionFiscal)
        }
        startActivity(intent)
    }

    private fun loadOrderSummary() {
        val total = CartManager.total
        val itemCount = CartManager.itemCount

        binding.tvCheckoutTotal.text = "Total: S/. ${String.format("%.2f", total)}"
        binding.tvItemCount.text = "Items: $itemCount"

        val summary = StringBuilder()
        CartManager.items.forEach { product ->
            val productTotal = product.price * product.quantity // CORREGIDO: quantity
            summary.append("• ${product.name} - S/. ${String.format("%.2f", product.price)} x${product.quantity} = S/. ${String.format("%.2f", productTotal)}\n")
        }
        binding.tvOrderSummary.text = summary.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}