package pe.idat.e_commerce_ef.data.remote.dto

data class ProductDto(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val stock: Int
)