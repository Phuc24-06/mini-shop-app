package com.example.shopper.models

enum class PaymentMethodType {
    COD,           // Cash on Delivery
    MOMO,          // MoMo QR Payment (sẽ implement sau)
    BANK_TRANSFER, // Chuyển khoản ngân hàng (optional)
    CREDIT_CARD    // Thẻ tín dụng (optional)
}

data class PaymentMethod(
    val type: PaymentMethodType,
    val name: String,
    val description: String,
    val iconResId: Int? = null, // Resource ID cho icon (optional)
    val isEnabled: Boolean = true
)

// Payment Result
sealed class PaymentResult {
    data class Success(val transactionId: String) : PaymentResult()
    data class Error(val message: String) : PaymentResult()
    object Pending : PaymentResult()
}

