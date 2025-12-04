package pe.idat.e_commerce_ef.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pe.idat.e_commerce_ef.databinding.ItemProductBinding
import pe.idat.e_commerce_ef.domain.model.Product

class ProductAdapter(
    private val onAddToCart: (Product, Int) -> Unit
) : ListAdapter<Product, ProductAdapter.ViewHolder>(ProductDiffCallback()) {

    class ViewHolder(
        private val binding: ItemProductBinding,
        private val onAddToCart: (Product, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvName.text = product.name
            binding.tvPrice.text = "S/. ${product.price}"
            binding.tvCategory.text = product.category

            // CAMBIAR: Usar Coil en lugar de Glide
            binding.ivProduct.load(product.image) {
                crossfade(true)
            }

            binding.btnAddToCart.setOnClickListener {
                onAddToCart(product, 1)
            }

            itemView.setOnClickListener {
                // Abrir detalle del producto
            }
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

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}