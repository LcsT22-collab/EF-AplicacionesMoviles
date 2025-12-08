package pe.idat.e_commerce_ef.util

import pe.idat.e_commerce_ef.domain.model.Product

object CartManager {
    private val cart = mutableListOf<Product>()

    val items: List<Product> get() = cart
    val total: Double get() = cart.sumOf { it.price * it.quantity }
    val itemCount: Int get() = cart.sumOf { it.quantity }

    fun addToCart(product: Product, quantity: Int = 1): Boolean {
        val existing = cart.find { it.id == product.id }

        if (existing != null) {
            val newTotalQty = existing.quantity + quantity
            if (newTotalQty <= product.stock) {
                existing.setQuantity(newTotalQty)
                return true
            }
        } else {
            if (product.stock >= quantity) {
                val newItem = product.copy().apply { setQuantity(quantity) }
                cart.add(newItem)
                return true
            }
        }
        return false
    }

    fun removeFromCart(product: Product) {
        cart.removeAll { it.id == product.id }
    }

    fun clearCart() {
        cart.clear()
    }
}