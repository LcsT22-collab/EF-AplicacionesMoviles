package pe.idat.e_commerce_ef.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.util.CartManager

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cartItems = MutableLiveData<List<Product>>()
    val cartItems: LiveData<List<Product>> = _cartItems

    init {
        loadProducts()
        updateCart()
    }

    fun loadProducts() {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repository.getProducts()
            if (result.isSuccess) {
                _products.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _loading.value = false
        }
    }

    // Funciones del carrito
    fun addToCart(product: Product, quantity: Int = 1) {
        CartManager.addToCart(product, quantity)
        updateCart()
    }

    fun removeFromCart(product: Product) {
        CartManager.removeFromCart(product)
        updateCart()
    }

    fun clearCart() {
        CartManager.clearCart()
        updateCart()
    }

    // AGREGAR ESTE MÉTODO
    fun updateCart() {
        _cartItems.value = CartManager.items
    }

    // Funciones de autenticación
    fun login(email: String, password: String, onResult: (Result<Boolean>) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            onResult(result)
        }
    }

    fun register(name: String, email: String, password: String, onResult: (Result<Boolean>) -> Unit) {
        viewModelScope.launch {
            val result = repository.register(name, email, password)
            onResult(result)
        }
    }

    fun logout() {
        repository.logout()
    }

    fun getCurrentUser() = repository.getCurrentUser()
}