package com.example.shopper.models

import androidx.compose.ui.graphics.Color

enum class OrderStatus(val displayName: String, val color: Color) {
    ALL("Tất cả", Color(0xFF757575)),
    PENDING("Chờ xác nhận", Color(0xFFFF9800)),
    PROCESSING("Đang xử lý", Color(0xFF2196F3)),
    SHIPPING("Đang giao", Color(0xFF9C27B0)),
    DELIVERED("Đã giao", Color(0xFF4CAF50)),
    CANCELED("Đã hủy", Color(0xFFF44336)),
    APPROVED("Đã duyệt", Color(0xFF388E3C)),
    REJECTED("Không duyệt", Color(0xFFD32F2F))
}