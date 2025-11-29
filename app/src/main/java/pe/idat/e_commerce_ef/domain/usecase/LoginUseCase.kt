package pe.idat.e_commerce_ef.domain.usecase

import pe.idat.e_commerce_ef.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        return repository.login(email, password)
    }
}