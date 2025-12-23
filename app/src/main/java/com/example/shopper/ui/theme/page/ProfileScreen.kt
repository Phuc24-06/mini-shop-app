package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shopper.ui.theme.componnents.ProfileMenuItem
import com.example.shopper.ui.theme.componnents.UserInfoCard


@Composable
fun ProfileScreen(
    userName: String ?= null ,
    userEmail: String ?= null,
    userAvatar: String? = null,
    onMenuItemClick: (String) -> Unit = {},
    onEditProfileClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // User Info Card
        UserInfoCard(
            userName = userName ?: "", // Xử lý null
            userEmail = userEmail ?:"", // Xử lý null
            userAvatar = userAvatar,
            modifier = Modifier.padding(top = 16.dp),
            onEditClick = {
                onEditProfileClick()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Menu Items
        ProfileMenuItem(
            icon = Icons.Default.ShoppingCart,
            title = "Đơn hàng của tôi",
            subtitle = "Theo dõi, trả hàng và mua lại",
            onClick = { onMenuItemClick("orders") }
        )

        ProfileMenuItem(
            icon = Icons.Default.Favorite,
            title = "Sản phẩm yêu thích",
            subtitle = "Danh sách sản phẩm đã thích",
            onClick = { onMenuItemClick("favorites") }
        )

        ProfileMenuItem(
            icon = Icons.Default.LocationOn,
            title = "Địa chỉ giao hàng",
            subtitle = "Quản lý địa chỉ giao hàng",
            onClick = { onMenuItemClick("addresses") } // Đã chọn 'addresses'
        )

        ProfileMenuItem(
            icon = Icons.Default.Star,
            title = "Phương thức thanh toán",
            subtitle = "Quản lý thẻ và ví điện tử",
            onClick = { onMenuItemClick("payment") }
        )

        ProfileMenuItem(
            icon = Icons.Default.Notifications,
            title = "Thông báo",
            subtitle = "Cài đặt thông báo và khuyến mãi",
            onClick = { onMenuItemClick("notifications") }
        )

        // MỤC ĐỔI MẬT KHẨU
        ProfileMenuItem(
            icon = Icons.Default.Lock,
            title = "Đổi mật khẩu",
            subtitle = "Cập nhật mật khẩu tài khoản",
            onClick = { onMenuItemClick("change_password") }
        )

        ProfileMenuItem(
            icon = Icons.Default.Phone,
            title = "Trợ giúp & Hỗ trợ",
            subtitle = "Câu hỏi thường gặp và liên hệ",
            onClick = { onMenuItemClick("help") }
        )

        ProfileMenuItem(
            icon = Icons.Default.Settings,
            title = "Cài đặt",
            subtitle = "Tài khoản, bảo mật và quyền riêng tư",
            onClick = { onMenuItemClick("settings") }
        )

        ProfileMenuItem(
            icon = Icons.Default.Info,
            title = "Thông tin ứng dụng",
            subtitle = "Phiên bản và điều khoản sử dụng",
            onClick = { onMenuItemClick("about") }
        )

        ProfileMenuItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Đăng xuất",
            onClick = { onMenuItemClick("logout") },
            iconColor = Color.Red
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}