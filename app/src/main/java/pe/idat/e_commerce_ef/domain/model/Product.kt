package pe.idat.e_commerce_ef.domain.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val stock: Int
) {
    var quantity: Int = 1
        private set

    fun setQuantity(qty: Int) {
        quantity = qty.coerceAtLeast(1).coerceAtMost(stock)
    }

    fun totalPrice(): Double = price * quantity
}