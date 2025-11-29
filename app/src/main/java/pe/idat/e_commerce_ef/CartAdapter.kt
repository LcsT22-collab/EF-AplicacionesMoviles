package pe.idat.e_commerce_ef.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.model.Product

class CartAdapter(
    private var cartItems: List<Product> = emptyList(),
    private val onRemoveFromCart: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProduct: ImageView = itemView.findViewById(R.id.ivCartProduct)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvCartTitle)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCartCategory)
        private val btnRemove: TextView = itemView.findViewById(R.id.btnRemoveFromCart)

        fun bind(product: Product) {
            tvTitle.text = product.name
            tvPrice.text = "S/. ${String.format("%.2f", product.getTotalPrice())} (x${product.selectedQuantity})"
            tvCategory.text = "${product.category} | Cantidad: ${product.selectedQuantity}"

            Glide.with(itemView.context)
                .load(product.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivProduct)

            btnRemove.setOnClickListener {
                onRemoveFromCart(product)
            }

            itemView.setOnClickListener {
                // Mostrar detalles del producto
                // Podemos abrir ProductDetailActivity aquí también
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
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