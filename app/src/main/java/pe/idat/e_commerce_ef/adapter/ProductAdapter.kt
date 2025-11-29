package pe.idat.e_commerce_ef.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pe.idat.e_commerce_ef.ProductDetailActivity
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.model.Product

class ProductAdapter(
    private var products: List<Product> = emptyList(),
    private val onAddToCart: (Product, Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        private val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)

        // Selector de cantidad
        private val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        private val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        private var currentProduct: Product? = null
        private var currentQuantity: Int = 1

        fun bind(product: Product) {
            currentProduct = product
            currentQuantity = product.selectedQuantity

            tvTitle.text = product.name
            tvPrice.text = "S/. ${String.format("%.2f", product.price)}"
            tvCategory.text = "${product.category} | Stock: ${product.stock}"
            tvStock.text = "Stock: ${product.stock}"

            // Actualizar la cantidad mostrada
            updateQuantityDisplay()

            Glide.with(itemView.context)
                .load(product.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivProduct)

            // Configurar botones de cantidad
            btnDecrease.setOnClickListener {
                if (currentQuantity > 1) {
                    currentQuantity--
                    updateQuantityDisplay()
                }
            }

            btnIncrease.setOnClickListener {
                if (currentQuantity < product.stock) {
                    currentQuantity++
                    updateQuantityDisplay()
                }
            }

            // Click en el item para ver detalles
            itemView.setOnClickListener {
                val productToDetail = product.copy().apply {
                    setQuantity(currentQuantity)
                }
                val intent = Intent(itemView.context, ProductDetailActivity::class.java).apply {
                    putExtra("product", productToDetail)
                }
                itemView.context.startActivity(intent)
            }

            // Botón agregar al carrito
            btnAddToCart.setOnClickListener {
                currentProduct?.let { product ->
                    onAddToCart(product, currentQuantity)
                    // Resetear cantidad después de agregar
                    currentQuantity = 1
                    updateQuantityDisplay()
                }
            }
        }

        private fun updateQuantityDisplay() {
            tvQuantity.text = currentQuantity.toString()

            // Actualizar estado de los botones
            btnDecrease.isEnabled = currentQuantity > 1
            btnIncrease.isEnabled = currentQuantity < (currentProduct?.stock ?: 1)

            // Cambiar color del botón aumentar si está deshabilitado
            if (currentQuantity >= (currentProduct?.stock ?: 1)) {
                btnIncrease.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
            } else {
                btnIncrease.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary))
            }

            // Cambiar color del botón disminuir si está deshabilitado
            if (currentQuantity <= 1) {
                btnDecrease.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
            } else {
                btnDecrease.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    fun getProducts(): List<Product> {
        return products
    }
}