package pe.idat.e_commerce_ef.presentation.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.domain.usecase.GetProductsUseCase
import pe.idat.e_commerce_ef.util.CartManager
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _productsState = MutableLiveData<ProductsState>(ProductsState.Loading)
    val productsState: LiveData<ProductsState> = _productsState

    private val _cartCount = MutableLiveData(CartManager.getItemCount())
    val cartCount: LiveData<Int> = _cartCount

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _productsState.value = ProductsState.Loading
            val result = getProductsUseCase()
            _productsState.value = if (result.isSuccess) {
                val products = result.getOrNull() ?: emptyList()
                ProductsState.Success(products)
            } else {
                ProductsState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        CartManager.addToCart(product, quantity)
        _cartCount.value = CartManager.getItemCount()
    }

    fun updateCartCount() {
        _cartCount.value = CartManager.getItemCount()
    }
}