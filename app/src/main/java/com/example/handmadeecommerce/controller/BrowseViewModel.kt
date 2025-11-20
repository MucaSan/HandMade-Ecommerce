package com.example.handmadeecommerce.controller

import androidx.lifecycle.*
import com.example.handmadeecommerce.model.Product
import com.example.handmadeecommerce.repository.CartRepository
import com.example.handmadeecommerce.repository.ProductRepository
import kotlinx.coroutines.launch

class BrowseViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _message = MutableLiveData<String>() // Para Toasts
    val message: LiveData<String> = _message

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val result = productRepository.getAllProducts()
            result.onSuccess {
                _products.value = it
            }.onFailure {
                _message.value = "Erro ao carregar produtos: ${it.message}"
            }
        }
    }

    fun addToCart(product: Product) {
        cartRepository.addItem(product)
        _message.value = "${product.name} adicionado ao carrinho!"
    }
}

class BrowseViewModelFactory(
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BrowseViewModel(productRepo, cartRepo) as T
    }
}