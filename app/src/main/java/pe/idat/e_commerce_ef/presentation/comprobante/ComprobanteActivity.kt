package pe.idat.e_commerce_ef.presentation.comprobante

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import pe.idat.e_commerce_ef.databinding.ActivityComprobanteBinding
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.presentation.products.ProductListActivity
import pe.idat.e_commerce_ef.util.CartManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ComprobanteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComprobanteBinding
    private var tipoComprobante: String = "BOLETA"
    private var razonSocial: String = ""
    private var ruc: String = ""
    private var direccionFiscal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComprobanteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        tipoComprobante = intent.getStringExtra("tipoComprobante") ?: "BOLETA"
        razonSocial = intent.getStringExtra("razonSocial") ?: ""
        ruc = intent.getStringExtra("ruc") ?: ""
        direccionFiscal = intent.getStringExtra("direccionFiscal") ?: ""

        setupToolbar()
        setupComprobante()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.btnBackComprobante.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupComprobante() {
        // Configurar tipo de comprobante
        binding.tvTituloComprobante.text = when (tipoComprobante) {
            "FACTURA" -> "Factura"
            else -> "Boleta"
        }

        binding.tvTipoComprobante.text = when (tipoComprobante) {
            "FACTURA" -> "FACTURA ELECTRÓNICA"
            else -> "BOLETA DE VENTA ELECTRÓNICA"
        }

        // Generar número de comprobante
        val numeroComprobante = when (tipoComprobante) {
            "FACTURA" -> "F${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
            else -> "B${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
        }
        binding.tvNumeroComprobante.text = numeroComprobante

        // Fecha actual
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        binding.tvFecha.text = fecha

        // Mostrar información del cliente si es factura
        if (tipoComprobante == "FACTURA") {
            binding.layoutInfoCliente.visibility = LinearLayout.VISIBLE
            binding.tvRazonSocial.text = "Razón Social: $razonSocial"
            binding.tvRuc.text = "RUC: $ruc"
            binding.tvDireccionFiscal.text = "Dirección: $direccionFiscal"
        }

        // Cargar productos
        cargarProductos()

        // Calcular totales
        calcularTotales()
    }

    private fun cargarProductos() {
        val layoutProductos = binding.layoutProductos
        layoutProductos.removeAllViews()

        CartManager.cartItems.forEach { product ->
            val productoView = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(8)
                }
            }

            // Nombre del producto
            val tvNombre = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f)
                text = product.name
                textSize = 12f
                maxLines = 2
                setTextColor(ContextCompat.getColor(this@ComprobanteActivity, android.R.color.black))
            }

            // Cantidad
            val tvCantidad = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = product.selectedQuantity.toString()
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@ComprobanteActivity, android.R.color.black))
                gravity = android.view.Gravity.CENTER
            }

            // Precio unitario
            val tvPrecioUnitario = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "S/. ${String.format("%.2f", product.price)}"
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@ComprobanteActivity, android.R.color.black))
                gravity = android.view.Gravity.CENTER
            }

            // Total
            val tvTotal = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "S/. ${String.format("%.2f", product.getTotalPrice())}"
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@ComprobanteActivity, android.R.color.black))
                gravity = android.view.Gravity.END
            }

            productoView.addView(tvNombre)
            productoView.addView(tvCantidad)
            productoView.addView(tvPrecioUnitario)
            productoView.addView(tvTotal)

            layoutProductos.addView(productoView)
        }
    }

    private fun calcularTotales() {
        val subtotal = CartManager.getTotal()
        val igv = subtotal * 0.18
        val total = subtotal + igv

        binding.tvSubtotal.text = "S/. ${String.format("%.2f", subtotal)}"
        binding.tvIgv.text = "S/. ${String.format("%.2f", igv)}"
        binding.tvTotalComprobante.text = "S/. ${String.format("%.2f", total)}"
    }

    private fun setupListeners() {
        binding.btnCompartir.setOnClickListener {
            compartirComprobante()
        }

        binding.btnFinalizar.setOnClickListener {
            finalizarCompra()
        }
    }

    private fun compartirComprobante() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, generarTextoComprobante())
            putExtra(Intent.EXTRA_SUBJECT, "Comprobante de Compra - E-Commerce EF")
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir comprobante"))
    }

    private fun generarTextoComprobante(): String {
        val builder = StringBuilder()
        builder.append("${binding.tvTipoComprobante.text}\n")
        builder.append("Número: ${binding.tvNumeroComprobante.text}\n")
        builder.append("Fecha: ${binding.tvFecha.text}\n\n")

        if (tipoComprobante == "FACTURA") {
            builder.append("Cliente: $razonSocial\n")
            builder.append("RUC: $ruc\n")
            builder.append("Dirección: $direccionFiscal\n\n")
        }

        builder.append("DETALLES DE LA COMPRA:\n")
        CartManager.cartItems.forEach { product ->
            builder.append("• ${product.name} x${product.selectedQuantity} - S/. ${String.format("%.2f", product.getTotalPrice())}\n")
        }

        builder.append("\n")
        builder.append("Subtotal: ${binding.tvSubtotal.text}\n")
        builder.append("IGV: ${binding.tvIgv.text}\n")
        builder.append("TOTAL: ${binding.tvTotalComprobante.text}\n\n")
        builder.append("¡Gracias por su compra!")

        return builder.toString()
    }

    private fun finalizarCompra() {
        CartManager.clearCart()

        val intent = Intent(this, ProductListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}