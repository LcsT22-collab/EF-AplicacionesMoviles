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

    private val _purchaseResult = MutableLiveData<Pair<Boolean, String?>>()
    val purchaseResult: LiveData<Pair<Boolean, String?>> = _purchaseResult

    init {
        loadProducts()
        updateCart()
    }

    fun loadProducts() {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.getProducts()
                if (result.isSuccess) {
                    _products.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Excepción"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1): Boolean {
        val currentProduct = _products.value?.find { it.id == product.id }

        if (currentProduct != null && currentProduct.stock >= quantity) {
            val success = CartManager.addToCart(product, quantity)
            if (success) {
                updateCart()
                return true
            }
        }
        return false
    }

    fun removeFromCart(product: Product) {
        CartManager.removeFromCart(product)
        updateCart()
    }

    fun updateCart() {
        _cartItems.value = CartManager.items
    }

    fun processPurchase() {
        viewModelScope.launch {
            _loading.value = true

            try {
                val currentProducts = _products.value ?: emptyList()
                val cartItems = CartManager.items

                for (cartItem in cartItems) {
                    val product = currentProducts.find { it.id == cartItem.id }
                    if (product == null || product.stock < cartItem.quantity) {
                        _purchaseResult.value = Pair(false, "Stock insuficiente para ${cartItem.name}")
                        _loading.value = false
                        return@launch
                    }
                }

                val updatedProducts = currentProducts.map { product ->
                    val cartItem = cartItems.find { it.id == product.id }
                    if (cartItem != null) {
                        product.copy(stock = product.stock - cartItem.quantity)
                    } else {
                        product
                    }
                }

                val updateResult = repository.updateProducts(updatedProducts)

                if (updateResult.isSuccess) {
                    _products.value = updatedProducts
                    CartManager.clearCart()
                    updateCart()
                    _purchaseResult.value = Pair(true, "Compra realizada exitosamente")
                } else {
                    _purchaseResult.value = Pair(false, "Error al guardar cambios")
                }

            } catch (e: Exception) {
                _purchaseResult.value = Pair(false, "Error: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

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
        CartManager.clearCart()
        repository.logout()
    }

    fun getCurrentUser() = repository.getCurrentUser()
}