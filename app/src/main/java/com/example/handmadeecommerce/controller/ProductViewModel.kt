package com.example.handmadeecommerce.controller

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.handmadeecommerce.model.Product
import com.example.handmadeecommerce.repository.ProductRepository
import kotlinx.coroutines.launch


sealed class ProductFormState {
    object Idle : ProductFormState()
    object Loading : ProductFormState()
    data class Success(val message: String) : ProductFormState()
    data class Error(val message: String) : ProductFormState()
}


sealed class ProductListState {
    object Loading : ProductListState()
    data class Success(val products: List<Product>) : ProductListState()
    data class Error(val message: String) : ProductListState()
}

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {


    private val _productListState = MutableLiveData<ProductListState>(ProductListState.Loading)
    val productListState: LiveData<ProductListState> = _productListState


    private val _productFormState = MutableLiveData<ProductFormState>(ProductFormState.Idle)
    val productFormState: LiveData<ProductFormState> = _productFormState


    private var allProducts = listOf<Product>()


    init {
        fetchArtisanProducts()
    }


    fun fetchArtisanProducts() {
        _productListState.value = ProductListState.Loading
        viewModelScope.launch {
            val result = repository.getArtisanProducts()
            result.onSuccess { products ->
                allProducts = products
                _productListState.value = ProductListState.Success(products)
            }.onFailure { e ->
                _productListState.value = ProductListState.Error(e.message ?: "Erro ao buscar produtos")
            }
        }
    }


    fun addProduct(product: Product, imageUri: Uri?) {
        _productFormState.value = ProductFormState.Loading
        viewModelScope.launch {
            try {
                if (imageUri != null) {
                    val uploadResult = repository.uploadImage(imageUri)
                    uploadResult.onSuccess { downloadUrl ->
                        val productWithUrl = product.copy(imageUrl = downloadUrl)
                        val addResult = repository.addProduct(productWithUrl)
                        addResult.onSuccess {
                            _productFormState.value = ProductFormState.Success("Produto adicionado!")
                            fetchArtisanProducts()
                        }.onFailure { e ->
                            _productFormState.value = ProductFormState.Error(e.message ?: "Erro ao salvar no Firestore")
                        }
                    }.onFailure { e ->
                        _productFormState.value = ProductFormState.Error(e.message ?: "Erro no upload da imagem")
                    }
                } else {
                     _productFormState.value = ProductFormState.Error("Por favor, selecione uma imagem.")
                }
            } catch (e: Exception) {
                _productFormState.value = ProductFormState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }


    fun updateProduct(product: Product, imageUri: Uri?) {
        _productFormState.value = ProductFormState.Loading
        viewModelScope.launch {
            try {
                var productToUpdate = product

                if (imageUri != null) {
                    val uploadResult = repository.uploadImage(imageUri)
                    uploadResult.onSuccess { downloadUrl ->
                        productToUpdate = product.copy(imageUrl = downloadUrl)
                    }.onFailure { e ->
                        _productFormState.value = ProductFormState.Error(e.message ?: "Erro no upload da imagem")
                        return@launch
                    }
                }

                val updateResult = repository.updateProduct(productToUpdate)
                updateResult.onSuccess {
                    _productFormState.value = ProductFormState.Success("Produto atualizado!")
                    fetchArtisanProducts()
                }.onFailure { e ->
                    _productFormState.value = ProductFormState.Error(e.message ?: "Erro ao atualizar no Firestore")
                }

            } catch (e: Exception) {
                _productFormState.value = ProductFormState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = repository.deleteProduct(productId)
            result.onSuccess {
                fetchArtisanProducts()
            }.onFailure { e ->
                _productListState.value = ProductListState.Error(e.message ?: "Erro ao deletar")
            }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _productListState.value = ProductListState.Success(allProducts)
            return
        }
        val filteredList = allProducts.filter {
            it.name.contains(query, ignoreCase = true)
        }
        _productListState.value = ProductListState.Success(filteredList)
    }

    fun resetFormState() {
        _productFormState.value = ProductFormState.Idle
    }
}