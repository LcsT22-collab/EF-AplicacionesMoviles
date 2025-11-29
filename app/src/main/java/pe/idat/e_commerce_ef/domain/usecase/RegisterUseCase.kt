package pe.idat.e_commerce_ef.domain.usecase

import pe.idat.e_commerce_ef.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Boolean> {
        return repository.register(name, email, password)
    }
}