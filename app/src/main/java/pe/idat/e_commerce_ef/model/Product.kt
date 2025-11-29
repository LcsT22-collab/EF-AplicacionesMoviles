package pe.idat.e_commerce_ef.model

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val stock: Int
) : Serializable {

    var selectedQuantity: Int = 1
        private set

    fun setQuantity(quantity: Int) {
        selectedQuantity = quantity.coerceAtLeast(1).coerceAtMost(stock)
    }

    fun getTotalPrice(): Double {
        return price * selectedQuantity
    }
}