package pe.idat.e_commerce_ef.domain.model

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    var stock: Int
) : Serializable {
    var quantity: Int = 1
        private set

    fun setQuantity(qty: Int) {
        quantity = qty.coerceAtLeast(1).coerceAtMost(stock)
    }

    fun totalPrice(): Double = price * quantity

    fun reduceStock(): Boolean {
        return if (quantity <= stock) {
            stock -= quantity
            true
        } else {
            false
        }
    }

    fun canAddToCart(requestedQty: Int = 1): Boolean {
        return stock >= requestedQty
    }
}