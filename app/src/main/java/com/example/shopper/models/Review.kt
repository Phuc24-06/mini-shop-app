package com.example.shopper.models

import java.util.Date

/**
 * Model cho đánh giá sản phẩm
 */
data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String? = null,
    val rating: Float = 0f, // 1-5 sao
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val images: List<String> = emptyList(), // URL ảnh review (optional)
    val isVerifiedPurchase: Boolean = false // Đã mua hàng chưa
) {
    // Convert Long timestamp to Date for display
    fun getDate(): Date {
        return Date(timestamp)
    }

    // Format date to string
    fun getFormattedDate(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("vi", "VN"))
        return sdf.format(getDate())
    }
}

/**
 * Model cho thống kê rating của sản phẩm
 */
data class ProductRating(
    val productId: String = "",
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val fiveStars: Int = 0,
    val fourStars: Int = 0,
    val threeStars: Int = 0,
    val twoStars: Int = 0,
    val oneStar: Int = 0
) {
    // Tính phần trăm cho mỗi rating
    fun getPercentage(stars: Int): Float {
        if (totalReviews == 0) return 0f
        val count = when (stars) {
            5 -> fiveStars
            4 -> fourStars
            3 -> threeStars
            2 -> twoStars
            1 -> oneStar
            else -> 0
        }
        return (count.toFloat() / totalReviews) * 100
    }
}

