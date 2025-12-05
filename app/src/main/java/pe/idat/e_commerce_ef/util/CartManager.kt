package pe.idat.e_commerce_ef.util

import pe.idat.e_commerce_ef.domain.model.Product

object CartManager {
    private val cart = mutableListOf<Product>()

    val items: List<Product> get() = cart
    val total: Double get() = cart.sumOf { it.price * it.quantity }
    val itemCount: Int get() = cart.sumOf { it.quantity }

    fun addToCart(product: Product, quantity: Int = 1) {
        val existing = cart.find { it.id == product.id }
        if (existing != null) {
            existing.setQuantity(existing.quantity + quantity)
        } else {
            val newItem = product.copy().apply {
                setQuantity(quantity)
            }
            cart.add(newItem)
        }
    }

    fun removeFromCart(product: Product) {
        cart.removeAll { it.id == product.id }
    }

    fun clearCart() {
        cart.clear()
    }

    // Nuevo metodo para forzar reset del carrito
    fun forceReset() {
        cart.clear()
    }
}