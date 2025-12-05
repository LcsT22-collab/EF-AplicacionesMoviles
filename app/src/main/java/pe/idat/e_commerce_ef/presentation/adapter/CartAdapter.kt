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
            binding.tvCartPrice.text = getString(
                R.string.format_price_with_quantity,
                product.totalPrice(),
                product.quantity
            )
            binding.tvCartCategory.text = getString(
                R.string.category_format,
                product.category,
                product.quantity
            )

            binding.ivCartProduct.load(product.image)

            binding.btnRemoveFromCart.setOnClickListener {
                onRemoveFromCart(product)
            }
        }

        private fun getString(resId: Int, vararg args: Any): String {
            return binding.root.context.getString(resId, *args)
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