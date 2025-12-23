package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.shopper.models.Order
import com.example.shopper.models.OrderItem
import com.example.shopper.models.OrderStatus
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    order: Order,
    onBackClick: () -> Unit = {},
    onCancelOrder: (String) -> Unit = {},
    onReorder: (String) -> Unit = {},
    onTrackOrder: (String) -> Unit = {}
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF9800))
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Chi tiết đơn hàng",
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    windowInsets = WindowInsets(0)
                )
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Order Button (only for PENDING)
                        if (order.status == OrderStatus.PENDING) {
                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Red
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                            ) {
                                Text("Hủy đơn")
                            }
                        }

                        // Track Order Button (for PROCESSING, SHIPPING)
                        if (order.status == OrderStatus.PROCESSING ||
                            order.status == OrderStatus.SHIPPING) {
                            Button(
                                onClick = { onTrackOrder(order.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9800)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Theo dõi")
                            }
                        }

                        // Reorder Button (for DELIVERED, CANCELED)
                        if (order.status == OrderStatus.DELIVERED ||
                            order.status == OrderStatus.CANCELED) {
                            Button(
                                onClick = { onReorder(order.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9800)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mua lại")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            // Status Timeline
            StatusTimeline(order = order)

            Spacer(modifier = Modifier.height(8.dp))

            // Order Info Card
            OrderInfoCard(order = order)

            Spacer(modifier = Modifier.height(8.dp))

            // Product Items Card
            ProductItemsCard(items = order.items)

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Summary Card
            PaymentSummaryCard(order = order)

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Cancel Order Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Xác nhận hủy đơn") },
            text = { Text("Bạn có chắc chắn muốn hủy đơn hàng này?") },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelOrder(order.id)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Hủy đơn")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
private fun StatusTimeline(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trạng thái đơn hàng",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = order.status.color.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, order.status.color)
                ) {
                    Text(
                        text = order.status.displayName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = order.status.color
                    )
                }
            }

            if (order.estimatedDelivery != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dự kiến giao: ${order.estimatedDelivery}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun OrderInfoCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Thông tin đơn hàng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(label = "Mã đơn hàng", value = order.orderNumber)
            InfoRow(label = "Ngày đặt", value = order.date)
            InfoRow(label = "Địa chỉ giao hàng", value = order.shippingAddress)
            InfoRow(label = "Phương thức thanh toán", value = order.paymentMethod)
        }
    }
}

@Composable
private fun ProductItemsCard(items: List<OrderItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Sản phẩm (${items.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            items.forEach { item ->
                ProductItemRow(item = item)
                if (item != items.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ProductItemRow(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = item.productImage,
            contentDescription = item.productName,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.2f)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.productName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Số lượng: ${item.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                    .format(item.totalPrice),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
private fun PaymentSummaryCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Thanh toán",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            val subtotal = order.items.sumOf { it.totalPrice }
            val shipping = 30000.0
            val discount = 0.0

            SummaryRow(label = "Tạm tính", value = subtotal)
            SummaryRow(label = "Phí vận chuyển", value = shipping)
            if (discount > 0) {
                SummaryRow(
                    label = "Giảm giá",
                    value = -discount,
                    valueColor = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng cộng",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        .format(order.totalAmount),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: Double,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(value),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = valueColor
        )
    }
}
