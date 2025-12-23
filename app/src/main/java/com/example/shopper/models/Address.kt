package com.example.shopper.models

/**
 * Model cho Địa chỉ giao hàng (Shipping Address)
 */
data class Address(
    val id: String = "",
    val userId: String = "",      // UID của người dùng sở hữu địa chỉ
    val receiverName: String = "",
    val phoneNumber: String = "",
    val streetAddress: String = "", // Số nhà, tên đường
    val city: String = "",          // Tỉnh/Thành phố
    val district: String = "",      // Quận/Huyện
    val ward: String = "",          // Phường/Xã
    val isDefault: Boolean = false  // Địa chỉ mặc định
) {
    // Getter để hiển thị địa chỉ đầy đủ
    fun getFullAddress(): String {
        return "$streetAddress, $ward, $district, $city"
    }
}