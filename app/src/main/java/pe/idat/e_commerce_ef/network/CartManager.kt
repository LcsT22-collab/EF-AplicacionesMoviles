package pe.idat.e_commerce_ef.network

import pe.idat.e_commerce_ef.model.Product

object CartManager {
    private val _cartItems = mutableListOf<Product>()
    val cartItems: List<Product> get() = _cartItems

    fun addToCart(product: Product, quantity: Int = 1) {
        // Verificar si el producto ya está en el carrito
        val existingProductIndex = _cartItems.indexOfFirst { it.id == product.id }

        if (existingProductIndex != -1) {
            // Si ya existe, actualizar la cantidad
            val existingProduct = _cartItems[existingProductIndex]
            existingProduct.setQuantity(existingProduct.selectedQuantity + quantity)
        } else {
            // Si no existe, agregar nuevo producto con la cantidad especificada
            val productCopy = product.copy().apply {
                setQuantity(quantity)
            }
            _cartItems.add(productCopy)
        }
    }

    fun updateQuantity(product: Product, newQuantity: Int) {
        val existingProduct = _cartItems.find { it.id == product.id }
        existingProduct?.setQuantity(newQuantity)
    }

    fun removeFromCart(product: Product) {
        _cartItems.removeAll { it.id == product.id }
    }

    fun clearCart() {
        _cartItems.clear()
    }

    fun getTotal(): Double = _cartItems.sumOf { it.getTotalPrice() }

    fun getItemCount(): Int = _cartItems.sumOf { it.selectedQuantity }

    fun getUniqueItemCount(): Int = _cartItems.size
}