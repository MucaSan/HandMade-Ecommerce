package com.example.handmadeecommerce.controller

import androidx.lifecycle.*
import com.example.handmadeecommerce.model.CartItem
import com.example.handmadeecommerce.repository.CartRepository
import kotlinx.coroutines.launch


sealed class CartState {
    object Idle : CartState()
    object Loading : CartState()
    data class CheckoutSuccess(val orderId: String) : CartState()
    data class Error(val message: String) : CartState()
}

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {

    val cartItems: LiveData<List<CartItem>> = cartRepository.cartItemsLiveData
    val totalPrice: LiveData<Double> = cartRepository.totalPrice


    private val _cartState = MutableLiveData<CartState>(CartState.Idle)
    val cartState: LiveData<CartState> = _cartState

    fun increaseQuantity(item: CartItem) {
        cartRepository.increaseQuantity(item)
    }

    fun decreaseQuantity(item: CartItem) {
        cartRepository.decreaseQuantity(item)
    }

    fun checkout() {
        if (cartItems.value.isNullOrEmpty()) {
            _cartState.value = CartState.Error("O carrinho está vazio.")
            return
        }

        _cartState.value = CartState.Loading

        viewModelScope.launch {
            val result = cartRepository.checkout()

            result.onSuccess { orderId ->
                _cartState.value = CartState.CheckoutSuccess(orderId)
            }.onFailure { e ->
                _cartState.value = CartState.Error(e.message ?: "Erro ao finalizar compra")
                _cartState.value = CartState.Idle
            }
        }
    }
}

class CartViewModelFactory(private val repository: CartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}