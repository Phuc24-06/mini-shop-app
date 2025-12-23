package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.models.Order
import com.example.shopper.models.OrderStatus
import com.example.shopper.ui.theme.componnents.OrderCard
import com.example.shopper.ui.theme.componnents.StatusFilterChip
import com.example.shopper.ui.theme.viewmodel.OrderViewModel

@Composable
fun OrderListScreen(
    onOrderClick: (Order) -> Unit = {},
    viewModel: OrderViewModel = viewModel()
) {
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    // Đã xóa: val filteredOrders = viewModel.getFilteredOrders()
    val allOrders by viewModel.orders.collectAsState()

    // Load orders khi màn hình khởi tạo
    LaunchedEffect(Unit) {
        // TODO: Load orders from Firebase for current user
        // viewModel.loadUserOrders(userId)
    }

    // Derived state - TÍNH TOÁN filteredOrders (Giữ lại cách này)
    val filteredOrders by remember(allOrders, selectedStatus) { // Thêm keys để tối ưu
        derivedStateOf {
            if (selectedStatus == OrderStatus.ALL) {
                allOrders.sortedByDescending { it.timestamp }
            } else {
                allOrders.filter { it.status == selectedStatus }.sortedByDescending { it.timestamp }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Status Filter Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(OrderStatus.entries) { status ->
                StatusFilterChip(
                    status = status,
                    isSelected = selectedStatus == status,
                    onClick = { viewModel.filterByStatus(status) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Orders List
        if (filteredOrders.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📦",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Không có đơn hàng",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bạn chưa có đơn hàng nào trong trạng thái này",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredOrders) { order ->
                    OrderCard(
                        order = order,
                        onClick = { onOrderClick(order) }
                    )
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}