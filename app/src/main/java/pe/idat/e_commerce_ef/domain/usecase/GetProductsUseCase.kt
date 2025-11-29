package pe.idat.e_commerce_ef.domain.usecase

import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): Result<List<Product>> {
        return repository.getProducts()
    }
}