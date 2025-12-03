package pe.idat.e_commerce_ef.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("description")
    val description: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("image")
    val image: String,

    // Tu JSON ya incluye stock
    @SerializedName("stock")
    val stock: Int
)