package pe.idat.e_commerce_ef.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pe.idat.e_commerce_ef.databinding.ItemProductBinding
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.presentation.ProductDetailActivity

class ProductAdapter(
    private val onAddToCart: (Product, Int) -> Unit
) : ListAdapter<Product, ProductAdapter.ViewHolder>(ProductDiffCallback()) {

    class ViewHolder(
        private val binding: ItemProductBinding,
        private val onAddToCart: (Product, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvName.text = product.name
            binding.tvPrice.text = "S/. ${String.format("%.2f", product.price)}"
            binding.tvCategory.text = product.category

            // Usar Coil para cargar imágenes
            binding.ivProduct.load(product.image) {
                crossfade(true)
            }

            // Click en el botón para agregar al carrito (evita que abra el detalle)
            binding.btnAddToCart.setOnClickListener {
                it.isClickable = true
                onAddToCart(product, 1)
            }

            // Click en la imagen para ver detalles
            binding.ivProduct.setOnClickListener {
                openProductDetail(product)
            }

            // Click en el contenedor completo (LinearLayout) para ver detalles
            binding.root.setOnClickListener {
                openProductDetail(product)
            }

            // Click en el nombre para ver detalles
            binding.tvName.setOnClickListener {
                openProductDetail(product)
            }

            // Click en el precio para ver detalles
            binding.tvPrice.setOnClickListener {
                openProductDetail(product)
            }
        }

        private fun openProductDetail(product: Product) {
            val context = itemView.context
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("product", product)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onAddToCart)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}