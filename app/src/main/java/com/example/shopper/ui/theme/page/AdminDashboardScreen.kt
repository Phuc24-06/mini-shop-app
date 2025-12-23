package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Thêm 2 import này vào AdminDashboardScreen.kt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.ui.theme.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    onNavigateTo: (String) -> Unit // Callback để chuyển trang con (Products, Users...)
) {
    val authViewModel: AuthViewModel = viewModel()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F7FA) // Màu nền xám nhẹ dịu mắt
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- Phần 1: Thống kê tổng quan ---
            Text("Tổng quan hôm nay", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Thẻ Doanh thu
                StatCard(
                    title = "Doanh thu",
                    value = "12.5M₫",
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFF4CAF50), // Xanh lá
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Thẻ Đơn hàng
                StatCard(
                    title = "Đơn hàng",
                    value = "128",
                    icon = Icons.Default.ShoppingCart,
                    color = Color(0xFF2196F3), // Xanh dương
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                // Thẻ User mới
                StatCard(
                    title = "Khách mới",
                    value = "45",
                    icon = Icons.Default.PersonAdd,
                    color = Color(0xFFFF9800), // Cam
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Thẻ Đang xử lý
                StatCard(
                    title = "Chờ xử lý",
                    value = "12",
                    icon = Icons.Default.PendingActions,
                    color = Color(0xFFE91E63), // Hồng
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Phần 2: Menu Quản lý ---
            Text("Quản lý", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminActionItem(
                    icon = Icons.Default.Inventory,
                    title = "Quản lý Sản phẩm",
                    subtitle = "Thêm, sửa, xóa sản phẩm",
                    onClick = { onNavigateTo("products") }
                )
                AdminActionItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "Quản lý Đơn hàng",
                    subtitle = "Xem và quản lý đơn hàng",
                    onClick = { onNavigateTo("admin_orders") }
                )
                AdminActionItem(
                    icon = Icons.Default.People,
                    title = "Quản lý Người dùng",
                    subtitle = "Danh sách khách hàng và phân quyền",
                    onClick = { onNavigateTo("users") }
                )
                AdminActionItem(
                    icon = Icons.Default.Analytics,
                    title = "Báo cáo & Thống kê",
                    subtitle = "Xem chi tiết hiệu quả kinh doanh",
                    onClick = { onNavigateTo("reports") }
                )
                AdminActionItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Đăng xuất Admin",
                    subtitle = "Thoát khỏi tài khoản quản trị",
                    onClick = {
                        authViewModel.logout() // 1. Thực hiện đăng xuất
                        onBack() // 2. Quay lại. Vì nó là startDestination, nó sẽ pop về Auth Screen
                    }
                )

            }
        }
    }
}

// --- Các Component con tái sử dụng ---

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun AdminActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp).background(Color.Transparent), tint = Color.LightGray) // Hack icon arrow right nếu cần
        }
    }
}