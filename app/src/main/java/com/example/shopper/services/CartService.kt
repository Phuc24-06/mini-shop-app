package com.example.shopper.services

import com.example.shopper.models.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Service để quản lý giỏ hàng trên Firestore
 * Cấu trúc: users/{userId}/cart/{productId}
 */
object CartService {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Lấy userId hiện tại
     */
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Lấy reference đến collection cart của user hiện tại
     */
    private fun getCartCollection() = getCurrentUserId()?.let {
        firestore.collection("users").document(it).collection("cart")
    }

    /**
     * Thêm hoặc cập nhật sản phẩm trong giỏ hàng
     */
    suspend fun addOrUpdateCartItem(cartItem: CartItem) {
        val userId = getCurrentUserId() ?: return

        try {
            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document(cartItem.productId)
                .set(cartItem)
                .await()

            android.util.Log.d("CartService", "Added/Updated item: ${cartItem.productName}")
        } catch (e: Exception) {
            android.util.Log.e("CartService", "Error adding item to cart", e)
            throw e
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    suspend fun updateQuantity(productId: String, quantity: Int) {
        val userId = getCurrentUserId() ?: return

        try {
            if (quantity <= 0) {
                // Nếu số lượng <= 0, xóa sản phẩm
                removeCartItem(productId)
            } else {
                firestore.collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(productId)
                    .update("quantity", quantity)
                    .await()

                android.util.Log.d("CartService", "Updated quantity for $productId: $quantity")
            }
        } catch (e: Exception) {
            android.util.Log.e("CartService", "Error updating quantity", e)
            throw e
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    suspend fun removeCartItem(productId: String) {
        val userId = getCurrentUserId() ?: return

        try {
            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document(productId)
                .delete()
                .await()

            android.util.Log.d("CartService", "Removed item: $productId")
        } catch (e: Exception) {
            android.util.Log.e("CartService", "Error removing item", e)
            throw e
        }
    }

    /**
     * Load toàn bộ giỏ hàng từ Firestore
     */
    suspend fun loadCart(): List<CartItem> {
        val userId = getCurrentUserId() ?: return emptyList()

        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .await()

            val cartItems = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CartItem::class.java)
            }

            android.util.Log.d("CartService", "Loaded ${cartItems.size} items from cart")
            cartItems
        } catch (e: Exception) {
            android.util.Log.e("CartService", "Error loading cart", e)
            emptyList()
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    suspend fun clearCart() {
        val userId = getCurrentUserId() ?: return

        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .await()

            // Xóa từng document
            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            android.util.Log.d("CartService", "Cleared cart")
        } catch (e: Exception) {
            android.util.Log.e("CartService", "Error clearing cart", e)
            throw e
        }
    }

    /**
     * Lắng nghe thay đổi giỏ hàng real-time
     */
    fun observeCart(onCartChanged: (List<CartItem>) -> Unit) {
        val collection = getCartCollection() ?: return

        collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("CartService", "Error observing cart", error)
                return@addSnapshotListener
            }

            val cartItems = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(CartItem::class.java)
            } ?: emptyList()

            onCartChanged(cartItems)
        }
    }
}

