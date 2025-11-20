package com.example.handmadeecommerce.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.handmadeecommerce.model.CartItem
import com.example.handmadeecommerce.model.Order
import com.example.handmadeecommerce.model.OrderItem
import com.example.handmadeecommerce.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CartRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val _cartItems = MutableList(0) { CartItem(Product()) }.toMutableList()
    private val _cartItemsLiveData = MutableLiveData<List<CartItem>>()
    val cartItemsLiveData: LiveData<List<CartItem>> = _cartItemsLiveData

    private val _totalPrice = MutableLiveData(0.0)
    val totalPrice: LiveData<Double> = _totalPrice

    init {
        _cartItems.clear()
        updateObservables()
    }

    fun addItem(product: Product) {
        val index = _cartItems.indexOfFirst { it.product.productId == product.productId }
        if (index != -1) {
            val currentItem = _cartItems[index]
            val updatedItem = currentItem.copy(quantity = currentItem.quantity + 1)
            _cartItems[index] = updatedItem
        } else {
            _cartItems.add(CartItem(product, 1))
        }
        updateObservables()
    }

    fun increaseQuantity(cartItem: CartItem) {
        val index = _cartItems.indexOfFirst { it.product.productId == cartItem.product.productId }
        if (index != -1) {
            val currentItem = _cartItems[index]
            val updatedItem = currentItem.copy(quantity = currentItem.quantity + 1)

            _cartItems[index] = updatedItem
            updateObservables()
        }
    }

    fun decreaseQuantity(cartItem: CartItem) {
        val index = _cartItems.indexOfFirst { it.product.productId == cartItem.product.productId }
        if (index != -1) {
            val currentItem = _cartItems[index]
            if (currentItem.quantity > 1) {
                val updatedItem = currentItem.copy(quantity = currentItem.quantity - 1)
                _cartItems[index] = updatedItem
            } else {
                _cartItems.removeAt(index)
            }
            updateObservables()
        }
    }


    fun clearCart() {
        _cartItems.clear()
        updateObservables()
    }

    private fun updateObservables() {
        _cartItemsLiveData.value = _cartItems.toList()
        _totalPrice.value = _cartItems.sumOf { it.product.price * it.quantity }
    }

    suspend fun checkout(): Result<String> {
         val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuário não logado"))
        val currentItems = _cartItems.toList()

        if (currentItems.isEmpty()) return Result.failure(Exception("Carrinho vazio"))

        val totalAmount = currentItems.sumOf { it.product.price * it.quantity }

        return try {
            val batch = firestore.batch()
            val orderRef = firestore.collection("orders").document()
            val orderId = orderRef.id

            val order = Order(
                id = orderId,
                userId = userId,
                totalAmount = totalAmount
            )
            batch.set(orderRef, order)

            for (item in currentItems) {
                val itemRef = firestore.collection("order_items").document()
                val orderItem = OrderItem(
                    id = itemRef.id,
                    orderId = orderId,
                    productId = item.product.productId,
                    artisanId = item.product.artisanId,
                    productName = item.product.name,
                    productPrice = item.product.price,
                    quantity = item.quantity,
                    subtotal = item.product.price * item.quantity
                )
                batch.set(itemRef, orderItem)
            }
            batch.commit().await()
            clearCart()

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}