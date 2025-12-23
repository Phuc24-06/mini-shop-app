package com.example.shopper.ui.theme.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons

// Giữ lại import này cho Icons.Default.Check/Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping

// Import AutoMirrored cho biểu tượng mũi tên quay lại
import androidx.compose.material.icons.automirrored.filled.ArrowBack
// Xóa: import androidx.compose.material.icons.filled.ArrowBack
// Xóa: import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.shopper.models.Order
import com.example.shopper.models.OrderStatus
import com.example.shopper.ui.theme.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    onBack: () -> Unit
) {
    // Lấy OrderViewModel, nơi xử lý mọi logic liên quan đến đơn hàng
    val orderViewModel: OrderViewModel = viewModel()
    val orders by orderViewModel.orders.collectAsState()
    var errorMsg by remember { mutableStateOf("") }
    // Tải tất cả đơn hàng MỘT LẦN khi màn hình được tạo
    LaunchedEffect(Unit) {
        orderViewModel.loadAllOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // CHỈ GIỮ LẠI MỘT ICON
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Danh sách đơn hàng", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (orders.isEmpty()) {
                Text("Chưa có đơn hàng nào.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                orders.forEach { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Mã đơn: ${order.orderNumber}", style = MaterialTheme.typography.bodyLarge)
                            Text("Ngày đặt: ${order.date}", style = MaterialTheme.typography.bodyMedium)
                            Text("Tài khoản: ${order.userName}", style = MaterialTheme.typography.bodyMedium)
                            Text("Khách: ${order.shippingAddress}", style = MaterialTheme.typography.bodyMedium)
                            Text("Tổng tiền: ${order.totalAmount} đ", style = MaterialTheme.typography.bodyMedium)
                            Text("Trạng thái: ${order.status}", style = MaterialTheme.typography.bodyMedium)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                if (order.status == OrderStatus.PENDING) {
                                    Button(
                                        onClick = { orderViewModel.approveOrder(order.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Duyệt")
                                        Spacer(Modifier.width(4.dp))
                                        Text("Duyệt")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = { orderViewModel.rejectOrder(order.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Không duyệt")
                                        Spacer(Modifier.width(4.dp))
                                        Text("Không duyệt")
                                    }
                                } else if (order.status == OrderStatus.APPROVED) {
                                    Button(
                                        onClick = { orderViewModel.markOrderDelivered(order.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                    ) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = "Giao hàng thành công")
                                        Spacer(Modifier.width(4.dp))
                                        Text("Giao hàng thành công")
                                    }
                                } else {
                                    Text("Đã xử lý", color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
            }
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}