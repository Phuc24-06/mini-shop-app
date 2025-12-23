package com.example.shopper.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopper.models.*
import com.example.shopper.services.OrderService
import com.example.shopper.services.AuthServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class OrderViewModel : ViewModel() {

    // Khởi tạo _orders với emptyList() và sẽ được load từ Firebase
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _selectedStatus = MutableStateFlow(OrderStatus.ALL)
    val selectedStatus: StateFlow<OrderStatus> = _selectedStatus

    // Lưu thông tin đơn hàng tạm thời khi chờ thanh toán QR
    private var pendingOrderData: PendingOrderData? = null

    data class PendingOrderData(
        val orderId: String,
        val items: List<CartItem>,
        val total: Double,
        val paymentMethod: PaymentMethodType,
        val shippingAddress: String,
        val phoneNumber: String,
        val note: String
    )

    init {
        // Load user orders from Firebase when ViewModel is created
        loadUserOrders()
    }

    fun filterByStatus(status: OrderStatus) {
        _selectedStatus.value = status
    }

    fun getFilteredOrders(): List<Order> {
        return if (_selectedStatus.value == OrderStatus.ALL) {
            _orders.value
        } else {
            _orders.value.filter { it.status == _selectedStatus.value }
        }
    }

    /**
     * Load orders for current user from Firebase
     */
    fun loadUserOrders() {
        val currentUser = AuthServices.getCurrentUser()
        if (currentUser != null) {
            OrderService.getOrdersByUser(
                userId = currentUser.uid,
                onSuccess = { ordersList ->
                    _orders.value = ordersList.sortedByDescending { it.timestamp }
                    android.util.Log.d("OrderViewModel", "Loaded ${ordersList.size} orders for user ${currentUser.uid}")
                },
                onFailure = { e ->
                    android.util.Log.e("OrderViewModel", "Failed to load user orders: ${e.message}", e)
                    _orders.value = emptyList()
                }
            )
        } else {
            android.util.Log.w("OrderViewModel", "No user logged in, cannot load orders")
            _orders.value = emptyList()
        }
    }

    fun cancelOrder(orderId: String) {
        // Update local state first
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId && order.status == OrderStatus.PENDING) {
                order.copy(status = OrderStatus.CANCELED)
            } else {
                order
            }
        }

        // Update in Firebase
        OrderService.updateOrderStatus(
            orderId = orderId,
            newStatus = OrderStatus.CANCELED,
            onSuccess = {
                android.util.Log.d("OrderViewModel", "Order $orderId canceled in Firebase")
                loadUserOrders() // Reload to sync
            },
            onFailure = { e ->
                android.util.Log.e("OrderViewModel", "Failed to cancel order: ${e.message}", e)
            }
        )
    }

    /**
     * Load all orders from Firebase (for admin)
     */
    fun loadAllOrders() {
        OrderService.getAllOrders(
            onSuccess = { ordersList ->
                _orders.value = ordersList.sortedByDescending { it.timestamp }
                android.util.Log.d("OrderViewModel", "Loaded ${ordersList.size} orders from Firebase")
            },
            onFailure = { e ->
                android.util.Log.e("OrderViewModel", "Failed to load orders: ${e.message}", e)
            }
        )
    }

    /**
     * Approve order (admin function)
     * Đã chỉnh lại: Khi duyệt chỉ chuyển sang APPROVED, sau đó có nút giao hàng thành công
     */
    fun approveOrder(orderId: String) {
        // Update local state first
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId && order.status == OrderStatus.PENDING) {
                order.copy(status = OrderStatus.APPROVED)
            } else {
                order
            }
        }

        // Update in Firebase
        OrderService.updateOrderStatus(
            orderId = orderId,
            newStatus = OrderStatus.APPROVED,
            onSuccess = {
                android.util.Log.d("OrderViewModel", "Order $orderId approved in Firebase")
                loadAllOrders() // Reload for admin to see updated list
            },
            onFailure = { e ->
                android.util.Log.e("OrderViewModel", "Failed to approve order: ${e.message}", e)
            }
        )
    }

    /**
     * Đánh dấu đơn hàng đã giao (admin function)
     */
    fun markOrderDelivered(orderId: String) {
        // Update local state first
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId && (order.status == OrderStatus.APPROVED || order.status == OrderStatus.PENDING)) {
                order.copy(status = OrderStatus.DELIVERED)
            } else {
                order
            }
        }
        // Update in Firebase
        OrderService.updateOrderStatus(
            orderId = orderId,
            newStatus = OrderStatus.DELIVERED,
            onSuccess = {
                android.util.Log.d("OrderViewModel", "Order $orderId delivered in Firebase")
                loadAllOrders() // Reload for admin to see updated list
            },
            onFailure = { e ->
                android.util.Log.e("OrderViewModel", "Failed to mark delivered: ${e.message}", e)
            }
        )
    }

    /**
     * Reject order (admin function)
     */
    fun rejectOrder(orderId: String) {
        // Update local state first
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId && order.status == OrderStatus.PENDING) {
                order.copy(status = OrderStatus.REJECTED)
            } else {
                order
            }
        }

        // Update in Firebase
        OrderService.updateOrderStatus(
            orderId = orderId,
            newStatus = OrderStatus.REJECTED,
            onSuccess = {
                android.util.Log.d("OrderViewModel", "Order $orderId rejected in Firebase")
                loadAllOrders() // Reload for admin to see updated list
            },
            onFailure = { e ->
                android.util.Log.e("OrderViewModel", "Failed to reject order: ${e.message}", e)
            }
        )
    }

    /**
     * Lưu thông tin đơn hàng tạm thời khi chờ thanh toán QR
     */
    fun savePendingOrder(
        orderId: String,
        items: List<CartItem>,
        total: Double,
        paymentMethod: PaymentMethodType,
        shippingAddress: String,
        phoneNumber: String,
        note: String = ""
    ) {
        pendingOrderData = PendingOrderData(
            orderId = orderId,
            items = items,
            total = total,
            paymentMethod = paymentMethod,
            shippingAddress = shippingAddress,
            phoneNumber = phoneNumber,
            note = note
        )
        android.util.Log.d("OrderViewModel", "Pending order saved: $orderId")
    }

    /**
     * Xác nhận thanh toán thành công - tạo đơn hàng từ pending data
     */
    fun confirmPendingOrder() {
        pendingOrderData?.let { data ->
            createOrder(
                orderId = data.orderId,
                items = data.items,
                total = data.total,
                paymentMethod = data.paymentMethod,
                shippingAddress = data.shippingAddress,
                phoneNumber = data.phoneNumber,
                note = data.note
            )
            pendingOrderData = null // Clear pending data
            android.util.Log.d("OrderViewModel", "Pending order confirmed and created")
        }
    }

    /**
     * Hủy đơn hàng pending (khi user back từ màn hình QR)
     */
    fun cancelPendingOrder() {
        pendingOrderData = null
        android.util.Log.d("OrderViewModel", "Pending order canceled")
    }

    /**
     * Reorder - Add all items from an order back to cart
     */
    fun reorder(orderId: String, cartViewModel: CartViewModel, onComplete: () -> Unit) {
        // Find the order by ID
        val order = _orders.value.find { it.id == orderId }

        if (order != null) {
            android.util.Log.d("OrderViewModel", "Reordering: $orderId with ${order.items.size} items")

            // Convert OrderItem to Product and add to cart
            order.items.forEach { orderItem ->
                // Create a temporary Product from OrderItem
                val product = com.example.shopper.models.Product(
                    id = orderItem.productId,
                    name = orderItem.productName,
                    price = orderItem.price,
                    imageUrl = orderItem.productImage,
                    description = "", // Not available in OrderItem
                    categoryId = "", // Not available in OrderItem
                    stock = 999 // Assume in stock
                )

                // Add to cart with the same quantity
                cartViewModel.addToCart(product, orderItem.quantity)
            }

            android.util.Log.d("OrderViewModel", "Successfully added ${order.items.size} items to cart")
            onComplete()
        } else {
            android.util.Log.w("OrderViewModel", "Order not found: $orderId")
        }
    }

    /**
     * Get order items for reordering (to be used by UI layer)
     */
    fun getOrderForReorder(orderId: String): Order? {
        return _orders.value.find { it.id == orderId }
    }

    /**
     * Tạo đơn hàng mới
     */
    fun createOrder(
        orderId: String,
        items: List<CartItem>,
        total: Double,
        paymentMethod: PaymentMethodType,
        shippingAddress: String,
        phoneNumber: String,
        note: String = ""
    ) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        val currentDate = dateFormat.format(Date())

        // Calculate estimated delivery (7 days from now)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val estimatedDelivery = dateFormat.format(calendar.time)

        // Convert CartItem to OrderItem
        val orderItems = items.map { cartItem ->
            OrderItem(
                productId = cartItem.productId,
                productName = cartItem.productName,
                productImage = cartItem.imageUrl,
                quantity = cartItem.quantity,
                price = cartItem.price
            )
        }

        // Create new order
        val newOrder = Order(
            id = orderId,
            orderNumber = "DH${System.currentTimeMillis()}",
            date = currentDate,
            status = OrderStatus.PENDING,
            items = orderItems,
            totalAmount = total,
            shippingAddress = shippingAddress,
            phoneNumber = phoneNumber,
            paymentMethod = when (paymentMethod) {
                PaymentMethodType.COD -> "Thanh toán khi nhận hàng"
                PaymentMethodType.MOMO -> "Ví MoMo"
                PaymentMethodType.BANK_TRANSFER -> "Chuyển khoản ngân hàng"
                PaymentMethodType.CREDIT_CARD -> "Thẻ tín dụng/ghi nợ"
            },
            estimatedDelivery = estimatedDelivery
        )

        // Add to local orders list first (tùy chọn)
        _orders.value = listOf(newOrder) + _orders.value

        // Save to Firebase
        OrderService.saveOrder(newOrder,
            onSuccess = { id ->
                android.util.Log.d("OrderViewModel", "Order saved to Firebase with ID: $id")
                // Reload user orders to get fresh data from Firebase
                loadUserOrders()
            },
            onFailure = { e ->
                android.util.Log.e("OrderViewModel", "Failed to save order: ${e.message}", e)
                // Xử lý lỗi: có thể xóa order khỏi list nếu không lưu được
            }
        )
    }
}

    private fun getSampleOrders(): List<Order> {
        return listOf(
            Order(
                id = "ORD001",
                orderNumber = "DH001234567",
                date = "15/11/2025",
                status = OrderStatus.PENDING,
                items = listOf(
                    OrderItem(
                        productId = "1",
                        productName = "iPhone 15 Pro Max",
                        productImage = "https://cdn.tgdd.vn/Products/Images/42/305658/iphone-15-pro-max-blue-thumbnew-600x600.jpg",
                        quantity = 1,
                        price = 29990000.0
                    ),
                    OrderItem(
                        productId = "2",
                        productName = "AirPods Pro Gen 2",
                        productImage = "https://cdn.tgdd.vn/Products/Images/54/289780/airpods-pro-2-1-600x600.jpg",
                        quantity = 1,
                        price = 6490000.0
                    )
                ),
                totalAmount = 36480000.0,
                shippingAddress = "123 Nguyễn Huệ, Quận 1, TP.HCM",
                phoneNumber = "0901234567",
                paymentMethod = "Thanh toán khi nhận hàng",
                estimatedDelivery = "20/11/2025"
            ),
            Order(
                id = "ORD002",
                orderNumber = "DH001234566",
                date = "10/11/2025",
                status = OrderStatus.SHIPPING,
                items = listOf(
                    OrderItem(
                        productId = "3",
                        productName = "Samsung Galaxy S24 Ultra",
                        productImage = "https://cdn.tgdd.vn/Products/Images/42/307174/samsung-galaxy-s24-ultra-grey-thumbnew-600x600.jpg",
                        quantity = 1,
                        price = 29990000.0
                    )
                ),
                totalAmount = 29990000.0,
                shippingAddress = "456 Lê Lợi, Quận 1, TP.HCM",
                phoneNumber = "0912345678",
                paymentMethod = "Chuyển khoản ngân hàng",
                estimatedDelivery = "18/11/2025"
            ),
            Order(
                id = "ORD003",
                orderNumber = "DH001234565",
                date = "05/11/2025",
                status = OrderStatus.DELIVERED,
                items = listOf(
                    OrderItem(
                        productId = "4",
                        productName = "MacBook Pro M3",
                        productImage = "https://cdn.tgdd.vn/Products/Images/44/309016/mac-pro-14-m3-2023-1-600x600.jpg",
                        quantity = 1,
                        price = 43990000.0
                    )
                ),
                totalAmount = 43990000.0,
                shippingAddress = "789 Trần Hưng Đạo, Quận 5, TP.HCM",
                phoneNumber = "0923456789",
                paymentMethod = "Thanh toán online",
                estimatedDelivery = "08/11/2025"
            ),
            Order(
                id = "ORD004",
                orderNumber = "DH001234564",
                date = "01/11/2025",
                status = OrderStatus.PROCESSING,
                items = listOf(
                    OrderItem(
                        productId = "5",
                        productName = "iPad Pro M2 11 inch",
                        productImage = "https://cdn.tgdd.vn/Products/Images/522/289973/ipad-pro-11-2022-wifi-gray-thumb-600x600.jpg",
                        quantity = 1,
                        price = 21990000.0
                    ),
                    OrderItem(
                        productId = "6",
                        productName = "Apple Pencil Gen 2",
                        productImage = "https://cdn.tgdd.vn/Products/Images/522/226935/but-cam-ung-apple-pencil-2-mhl23-thumb-1-600x600.jpg",
                        quantity = 1,
                        price = 3490000.0
                    )
                ),
                totalAmount = 25480000.0,
                shippingAddress = "321 Võ Văn Tần, Quận 3, TP.HCM",
                phoneNumber = "0934567890",
                paymentMethod = "Thanh toán khi nhận hàng",
                estimatedDelivery = "19/11/2025"
            ),
            Order(
                id = "ORD005",
                orderNumber = "DH001234563",
                date = "28/10/2025",
                status = OrderStatus.CANCELED,
                items = listOf(
                    OrderItem(
                        productId = "7",
                        productName = "Sony WH-1000XM5",
                        productImage = "https://cdn.tgdd.vn/Products/Images/54/289780/airpods-pro-2-1-600x600.jpg",
                        quantity = 1,
                        price = 8490000.0
                    )
                ),
                totalAmount = 8490000.0,
                shippingAddress = "654 Điện Biên Phủ, Quận Bình Thạnh, TP.HCM",
                phoneNumber = "0945678901",
                paymentMethod = "Chuyển khoản ngân hàng"
            )
        )
    }
