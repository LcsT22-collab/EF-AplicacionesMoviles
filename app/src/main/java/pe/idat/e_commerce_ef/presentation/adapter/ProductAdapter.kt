package pe.idat.e_commerce_ef.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.databinding.ItemProductBinding
import pe.idat.e_commerce_ef.domain.model.Product

class ProductAdapter(
    private val onItemClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductTitle.text = product.name
            binding.tvProductPrice.text = binding.root.context.getString(
                R.string.format_price,
                product.price
            )
            binding.tvProductCategory.text = product.category

            binding.ivProduct.load(product.image) {
                crossfade(true)
                error(R.drawable.image_background)
            }

            binding.root.setOnClickListener {
                onItemClick(product)
            }

            binding.btnAddToCart.setOnClickListener {
                onAddToCart(product)
            }
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
        holder.bind(getItem(position))
    }
}
