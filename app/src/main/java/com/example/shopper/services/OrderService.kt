package com.example.shopper.services

import com.example.shopper.models.Order
import com.example.shopper.models.OrderItem
import com.example.shopper.models.OrderStatus
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log // Cần import Log để sử dụng android.util.Log

object OrderService {
    private val db = FirebaseFirestore.getInstance()

    fun getOrdersByUser(userId: String, onSuccess: (List<Order>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val ordersList = result.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        val id = doc.id
                        val orderNumber = data["orderNumber"] as? String ?: ""
                        val date = data["date"] as? String ?: ""

                        val statusStr = data["status"] as? String ?: "PENDING"
                        val status = try {
                            OrderStatus.valueOf(statusStr)
                        } catch (e: Exception) {
                            OrderStatus.PENDING
                        }

                        val totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0
                        val shippingAddress = data["shippingAddress"] as? String ?: ""
                        val phoneNumber = data["phoneNumber"] as? String ?: ""
                        val paymentMethod = data["paymentMethod"] as? String ?: ""
                        val estimatedDelivery = data["estimatedDelivery"] as? String

                        val itemsListRaw = data["items"] as? List<Map<String, Any>> ?: emptyList()
                        val items = itemsListRaw.map { itemMap ->
                            OrderItem(
                                productId = itemMap["productId"] as? String ?: "",
                                productName = itemMap["productName"] as? String ?: "",
                                productImage = itemMap["productImage"] as? String ?: "",
                                quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 0,
                                price = (itemMap["price"] as? Number)?.toDouble() ?: 0.0
                            )
                        }

                        val userName = data["userName"] as? String ?: ""
                        val timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L

                        Order(
                            id = id,
                            orderNumber = orderNumber,
                            date = date,
                            status = status,
                            items = items,
                            totalAmount = totalAmount,
                            shippingAddress = shippingAddress,
                            phoneNumber = phoneNumber,
                            paymentMethod = paymentMethod,
                            estimatedDelivery = estimatedDelivery,
                            userName = userName,
                            timestamp = timestamp
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                onSuccess(ordersList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun saveOrder(order: Order, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val orderMap = hashMapOf<String, Any?>(
            "orderNumber" to order.orderNumber,
            "date" to order.date,
            "status" to order.status.name,
            "totalAmount" to order.totalAmount,
            "shippingAddress" to order.shippingAddress,
            "phoneNumber" to order.phoneNumber,
            "paymentMethod" to order.paymentMethod,
            "estimatedDelivery" to order.estimatedDelivery,
            "items" to order.items.map { item: OrderItem ->
                mapOf(
                    "productId" to item.productId,
                    "productName" to item.productName,
                    "productImage" to item.productImage,
                    "quantity" to item.quantity,
                    "price" to item.price
                )
            },
            "userId" to AuthServices.getCurrentUser()?.uid,
            "userName" to (AuthServices.getCurrentUser()?.displayName ?: ""),
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("orders")
            .add(orderMap)
            .addOnSuccessListener { docRef ->
                onSuccess(docRef.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * Get all orders (for admin)
     */
    fun getAllOrders(onSuccess: (List<Order>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("orders")
            .get()
            .addOnSuccessListener { result ->
                val ordersList = result.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        val id = doc.id
                        val orderNumber = data["orderNumber"] as? String ?: ""
                        val date = data["date"] as? String ?: ""

                        val statusStr = data["status"] as? String ?: "PENDING"
                        val status = try {
                            OrderStatus.valueOf(statusStr)
                        } catch (e: Exception) {
                            OrderStatus.PENDING
                        }

                        val totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0
                        val shippingAddress = data["shippingAddress"] as? String ?: ""
                        val phoneNumber = data["phoneNumber"] as? String ?: ""
                        val paymentMethod = data["paymentMethod"] as? String ?: ""
                        val estimatedDelivery = data["estimatedDelivery"] as? String
                        val itemsListRaw = data["items"] as? List<Map<String, Any>> ?: emptyList()
                        val items = itemsListRaw.map { itemMap ->
                            OrderItem(
                                productId = itemMap["productId"] as? String ?: "",
                                productName = itemMap["productName"] as? String ?: "",
                                productImage = itemMap["productImage"] as? String ?: "",
                                quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 0,
                                price = (itemMap["price"] as? Number)?.toDouble() ?: 0.0
                            )
                        }

                        val userName = data["userName"] as? String ?: ""
                        val timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L

                        Order(
                            id = id,
                            orderNumber = orderNumber,
                            date = date,
                            status = status,
                            items = items,
                            totalAmount = totalAmount,
                            shippingAddress = shippingAddress,
                            phoneNumber = phoneNumber,
                            paymentMethod = paymentMethod,
                            estimatedDelivery = estimatedDelivery,
                            userName = userName,
                            timestamp = timestamp
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                onSuccess(ordersList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * Update order status in Firebase
     */
    fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("orders")
            .document(orderId)
            .update("status", newStatus.name)
            .addOnSuccessListener {
                Log.d("OrderService", "Order $orderId status updated to ${newStatus.name}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("OrderService", "Failed to update order status: ${exception.message}")
                onFailure(exception)
            }
    }
}