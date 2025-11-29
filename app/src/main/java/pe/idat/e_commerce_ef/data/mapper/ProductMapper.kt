package pe.idat.e_commerce_ef.data.mapper

import pe.idat.e_commerce_ef.data.local.entity.LocalProduct
import pe.idat.e_commerce_ef.data.remote.dto.ProductDto
import pe.idat.e_commerce_ef.domain.model.Product

object ProductMapper {

    fun apiToDomain(dto: ProductDto): Product {
        return Product(
            id = dto.id,
            name = dto.title,
            price = dto.price,
            description = dto.description,
            category = dto.category,
            image = dto.image,
            stock = (10..50).random() // FakeStore no tiene stock, lo simulamos
        )
    }

    fun domainToLocal(product: Product): LocalProduct {
        return LocalProduct(
            id = product.id,
            name = product.name,
            price = product.price,
            description = product.description,
            category = product.category,
            image = product.image,
            stock = product.stock
        )
    }

    fun localToDomain(entity: LocalProduct): Product {
        return Product(
            id = entity.id,
            name = entity.name,
            price = entity.price,
            description = entity.description,
            category = entity.category,
            image = entity.image,
            stock = entity.stock
        )
    }
}