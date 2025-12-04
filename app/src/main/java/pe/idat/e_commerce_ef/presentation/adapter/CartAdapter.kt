package pe.idat.e_commerce_ef.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.databinding.ItemCartBinding
import pe.idat.e_commerce_ef.domain.model.Product

class CartAdapter(
    private var cartItems: List<Product> = emptyList(),
    private val onRemoveFromCart: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvCartTitle.text = product.name
            binding.tvCartPrice.text = "S/. ${String.format("%.2f", product.totalPrice())} (x${product.quantity})"
            binding.tvCartCategory.text = "${product.category} | Cantidad: ${product.quantity}"

            // CAMBIAR: Usar Coil en lugar de Glide
            binding.ivCartProduct.load(product.image) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
            }

            binding.btnRemoveFromCart.setOnClickListener {
                onRemoveFromCart(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newCartItems: List<Product>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}