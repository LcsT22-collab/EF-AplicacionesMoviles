package pe.idat.e_commerce_ef.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pe.idat.e_commerce_ef.domain.model.Product
import pe.idat.e_commerce_ef.util.CartManager
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _cartState = MutableLiveData<CartState>()
    val cartState: LiveData<CartState> = _cartState

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        val items = CartManager.cartItems
        val total = CartManager.getTotal()
        _cartState.value = CartState(
            items = items,
            total = total,
            isEmpty = items.isEmpty()
        )
    }

    fun removeFromCart(product: Product) {
        CartManager.removeFromCart(product)
        loadCartItems()
    }
}