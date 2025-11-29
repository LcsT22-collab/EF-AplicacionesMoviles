package pe.idat.e_commerce_ef.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.databinding.ItemProductBinding
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.presentation.products.ProductDetailActivity

class ProductAdapter(
    private var products: List<Product> = emptyList(),
    private val onAddToCart: (Product, Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentProduct: Product? = null
        private var currentQuantity: Int = 1

        fun bind(product: Product) {
            currentProduct = product
            currentQuantity = 1

            binding.tvTitle.text = product.name
            binding.tvPrice.text = "S/. ${String.format("%.2f", product.price)}"
            binding.tvCategory.text = product.category
            binding.tvStock.text = "Stock: ${product.stock}"

            updateQuantityDisplay()

            Glide.with(itemView.context)
                .load(product.image)
                .placeholder(R.drawable.ic_launcher_foreground) // ← AHORA FUNCIONARÁ
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.ivProduct)

            binding.btnDecrease.setOnClickListener {
                if (currentQuantity > 1) {
                    currentQuantity--
                    updateQuantityDisplay()
                }
            }

            binding.btnIncrease.setOnClickListener {
                if (currentQuantity < product.stock) {
                    currentQuantity++
                    updateQuantityDisplay()
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailActivity::class.java).apply {
                    putExtra("product", product)
                }
                itemView.context.startActivity(intent)
            }

            binding.btnAddToCart.setOnClickListener {
                currentProduct?.let { product ->
                    onAddToCart(product, currentQuantity)
                    currentQuantity = 1
                    updateQuantityDisplay()
                }
            }
        }

        private fun updateQuantityDisplay() {
            binding.tvQuantity.text = currentQuantity.toString()
            binding.btnDecrease.isEnabled = currentQuantity > 1
            binding.btnIncrease.isEnabled = currentQuantity < (currentProduct?.stock ?: 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}