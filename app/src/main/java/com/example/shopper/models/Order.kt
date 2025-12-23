package com.example.shopper.models

data class Order(
    val id: String,
    val orderNumber: String,
    val date: String,
    val status: OrderStatus,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val shippingAddress: String,
    val phoneNumber: String = "",
    val paymentMethod: String,
    val estimatedDelivery: String? = null,
    val userName: String = "",
    val timestamp: Long = 0L,
    val total: Double = 0.0,
    val note: String = "",


) {
    val itemCount: Int get() = items.sumOf { it.quantity }
}

data class OrderItem(
    val productId: String,
    val productName: String,
    val productImage: String,
    val quantity: Int,
    val price: Double
) {
    val totalPrice: Double get() = price * quantity
}
