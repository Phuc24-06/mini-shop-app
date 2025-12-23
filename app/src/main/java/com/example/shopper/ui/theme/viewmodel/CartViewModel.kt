package com.example.shopper.ui.theme.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopper.models.CartItem
import com.example.shopper.models.Product
import com.example.shopper.services.CartService
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    // Danh sách giỏ hàng
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems

    // Map để lưu số lượng của từng sản phẩm - sử dụng mutableStateMapOf để observable
    private val _quantities = mutableStateMapOf<String, Int>()
    val quantities: Map<String, Int> = _quantities

    /**
     * Load giỏ hàng từ Firestore khi khởi tạo ViewModel hoặc khi đăng nhập
     */
    fun loadCartFromFirestore() {
        viewModelScope.launch {
            try {
                val cartItems = CartService.loadCart()
                _cartItems.clear()
                _quantities.clear()

                cartItems.forEach { item ->
                    _cartItems.add(item)
                    _quantities[item.productId] = item.quantity
                }

                android.util.Log.d("CartViewModel", "Loaded ${cartItems.size} items from Firestore")
            } catch (e: Exception) {
                android.util.Log.e("CartViewModel", "Error loading cart from Firestore", e)
            }
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng và lưu vào Firestore
     */
    fun addToCart(product: Product, quantity: Int) {
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        val existingItem = _cartItems.find { it.productId == product.id }

        if (existingItem != null) {
            val currentQuantity = _quantities[product.id] ?: existingItem.quantity
            val newQuantity = currentQuantity + quantity
            _quantities[product.id] = newQuantity

            // Cập nhật quantity trong CartItem
            existingItem.quantity = newQuantity

            // Lưu vào Firestore
            viewModelScope.launch {
                try {
                    CartService.updateQuantity(product.id, newQuantity)
                } catch (e: Exception) {
                    android.util.Log.e("CartViewModel", "Error updating quantity in Firestore", e)
                }
            }
        } else {
            val cartItem = CartItem(
                product.id,
                product.name,
                quantity,
                product.price,
                product.imageUrl
            )
            _cartItems.add(cartItem)
            _quantities[product.id] = quantity

            // Lưu vào Firestore
            viewModelScope.launch {
                try {
                    CartService.addOrUpdateCartItem(cartItem)
                } catch (e: Exception) {
                    android.util.Log.e("CartViewModel", "Error adding item to Firestore", e)
                }
            }
        }
    }

    /**
     * Tăng số lượng sản phẩm
     */
    fun increaseQuantity(productId: String) {
        val currentQuantity = _quantities[productId]
        if (currentQuantity != null) {
            val newQuantity = currentQuantity + 1
            _quantities[productId] = newQuantity

            // Cập nhật trong _cartItems
            _cartItems.find { it.productId == productId }?.quantity = newQuantity

            // Lưu vào Firestore
            viewModelScope.launch {
                try {
                    CartService.updateQuantity(productId, newQuantity)
                } catch (e: Exception) {
                    android.util.Log.e("CartViewModel", "Error increasing quantity in Firestore", e)
                }
            }
        }
    }

    /**
     * Giảm số lượng sản phẩm
     */
    fun decreaseQuantity(productId: String) {
        val currentQuantity = _quantities[productId]
        if (currentQuantity != null) {
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                _quantities[productId] = newQuantity

                // Cập nhật trong _cartItems
                _cartItems.find { it.productId == productId }?.quantity = newQuantity

                // Lưu vào Firestore
                viewModelScope.launch {
                    try {
                        CartService.updateQuantity(productId, newQuantity)
                    } catch (e: Exception) {
                        android.util.Log.e("CartViewModel", "Error decreasing quantity in Firestore", e)
                    }
                }
            } else {
                // Nếu số lượng = 1, xóa sản phẩm
                removeFromCart(productId)
            }
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    fun removeFromCart(productId: String) {
        _cartItems.removeAll { it.productId == productId }
        _quantities.remove(productId)

        // Xóa khỏi Firestore
        viewModelScope.launch {
            try {
                CartService.removeCartItem(productId)
            } catch (e: Exception) {
                android.util.Log.e("CartViewModel", "Error removing item from Firestore", e)
            }
        }
    }

    /**
     * Lấy số lượng của sản phẩm
     */
    fun getQuantity(productId: String): Int {
        return quantities[productId] ?: 0
    }

    /**
     * Tính tổng tiền
     */
    fun calculateTotal(): Double {
        return _cartItems.sumOf { item ->
            val quantity = quantities[item.productId] ?: item.quantity
            item.price * quantity
        }
    }

    /**
     * Xóa tất cả sản phẩm trong giỏ hàng
     */
    fun clearCart() {
        _cartItems.clear()
        _quantities.clear()

        // Xóa khỏi Firestore
        viewModelScope.launch {
            try {
                CartService.clearCart()
            } catch (e: Exception) {
                android.util.Log.e("CartViewModel", "Error clearing cart from Firestore", e)
            }
        }
    }

    /**
     * Chuẩn bị dữ liệu cho checkout
     */
    fun prepareCheckout() {
        // Cập nhật quantity trong CartItem từ quantities map
        _cartItems.forEachIndexed { index, item ->
            val currentQuantity = _quantities[item.productId] ?: item.quantity
            if (item.quantity != currentQuantity) {
                // Update quantity directly since CartItem is a Java class with setter
                item.quantity = currentQuantity
            }
        }
    }
}