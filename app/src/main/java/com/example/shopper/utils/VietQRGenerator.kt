package com.example.shopper.utils

import java.net.URLEncoder

object VietQRGenerator {
    // ===== THAY ĐỔI THÔNG TIN TÀI KHOẢN CỦA BẠN Ở ĐÂY =====
    private const val BANK_ID = "970422" // Mã ngân hàng (VD: MB Bank = 970422, VietcomBank = 970436, VietinBank = 970415)
    private const val ACCOUNT_NO = "1962004688888" // Số tài khoản của bạn
    private const val ACCOUNT_NAME = "Mai Thanh Lâm" // Tên chủ tài khoản (viết hoa không dấu)
    // =======================================================

    /**
     * Generate VietQR URL để hiển thị mã QR thanh toán
     *
     * Danh sách mã ngân hàng phổ biến:
     * - 970422: MB Bank
     * - 970436: VietcomBank
     * - 970415: VietinBank
     * - 970405: AgriBank
     * - 970407: Techcombank
     * - 970423: TPBank
     * - 970403: Sacombank
     * - 970432: VPBank
     * - 970418: BIDV
     * - 970448: OCB
     */
    fun generateQRUrl(
        orderId: String,
        amount: Double,
        description: String? = null
    ): String {
        val amountInt = amount.toInt()
        val orderInfo = description ?: "Thanh toan don hang $orderId"
        val encodedInfo = URLEncoder.encode(orderInfo, "UTF-8")
        val encodedName = URLEncoder.encode(ACCOUNT_NAME, "UTF-8")

        return "https://img.vietqr.io/image/" +
            "$BANK_ID-$ACCOUNT_NO-compact2.png?" +
            "amount=$amountInt" +
            "&addInfo=$encodedInfo" +
            "&accountName=$encodedName"
    }

    /**
     * Lấy tên ngân hàng từ mã
     */
    fun getBankName(): String {
        return when (BANK_ID) {
            "970422" -> "MB Bank"
            "970436" -> "VietcomBank"
            "970415" -> "VietinBank"
            "970405" -> "AgriBank"
            "970407" -> "Techcombank"
            "970423" -> "TPBank"
            "970403" -> "Sacombank"
            "970432" -> "VPBank"
            "970418" -> "BIDV"
            "970448" -> "OCB"
            else -> "Ngân hàng"
        }
    }

    /**
     * Lấy số tài khoản (ẩn bớt cho bảo mật)
     */
    fun getMaskedAccountNo(): String {
        return if (ACCOUNT_NO.length > 4) {
            "**** **** ${ACCOUNT_NO.takeLast(4)}"
        } else {
            ACCOUNT_NO
        }
    }
}

