package pe.idat.e_commerce_ef.util

import pe.idat.e_commerce_ef.data.local.entity.LocalProduct
import pe.idat.e_commerce_ef.data.remote.dto.ProductDto
import pe.idat.e_commerce_ef.domain.model.Product

object ProductMapper {
    fun dtoToDomain(dto: ProductDto): Product = Product(
        id = dto.id,
        name = dto.name,
        price = dto.price,
        description = dto.description,
        category = dto.category,
        image = dto.image,
        stock = dto.stock
    )

    fun entityToDomain(entity: LocalProduct): Product = Product(
        id = entity.id,
        name = entity.name,
        price = entity.price,
        description = entity.description,
        category = entity.category,
        image = entity.image,
        stock = entity.stock
    )

    fun domainToEntity(product: Product): LocalProduct = LocalProduct(
        id = product.id,
        name = product.name,
        price = product.price,
        description = product.description,
        category = product.category,
        image = product.image,
        stock = product.stock
    )
}