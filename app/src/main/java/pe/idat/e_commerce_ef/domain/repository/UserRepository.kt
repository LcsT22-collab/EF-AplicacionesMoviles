package pe.idat.e_commerce_ef.domain.repository

import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    suspend fun login(email: String, password: String): Result<Boolean>
    suspend fun register(name: String, email: String, password: String): Result<Boolean>
    fun logout()
    fun getCurrentUser(): FirebaseUser?
}