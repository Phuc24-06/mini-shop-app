package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopper.models.CartItem
import com.example.shopper.models.PaymentMethod
import com.example.shopper.models.PaymentMethodType
import com.example.shopper.services.PaymentService
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    totalAmount: Double,
    onBackClick: () -> Unit = {},
    onCheckoutSuccess: (
        orderId: String,
        paymentMethod: PaymentMethodType,
        shippingAddress: String,
        phoneNumber: String,
        note: String
    ) -> Unit = { _, _, _, _, _ -> }
) {
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethodType?>(null) }
    var shippingAddress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val paymentMethods = remember { PaymentService.getAvailablePaymentMethods() }
    val shippingFee = 30000.0 // Phí ship cố định
    val finalTotal = totalAmount + shippingFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            // Shipping Address Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Địa chỉ giao hàng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = shippingAddress,
                        onValueChange = { shippingAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập địa chỉ giao hàng") },
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Số điện thoại") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Order Summary Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đơn hàng (${cartItems.size} sản phẩm)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cart items
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.productName}",
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = formatPrice(item.price * item.quantity),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tạm tính", fontSize = 14.sp)
                        Text(formatPrice(totalAmount), fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Shipping fee
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Phí vận chuyển", fontSize = 14.sp)
                        Text(formatPrice(shippingFee), fontSize = 14.sp)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tổng cộng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            formatPrice(finalTotal),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }

            // Payment Method Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // SỬA LỖI: Chỉ giữ lại một icon
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phương thức thanh toán",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    paymentMethods.forEach { method ->
                        PaymentMethodItem(
                            paymentMethod = method,
                            isSelected = selectedPaymentMethod == method.type,
                            onSelect = {
                                if (method.isEnabled) {
                                    selectedPaymentMethod = method.type
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Note Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ghi chú cho người bán",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập ghi chú (tùy chọn)") },
                        minLines = 3,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Error message
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // SỬA LỖI: Chỉ giữ lại một icon
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage!!,
                            fontSize = 14.sp,
                            color = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom button
        }

        // Checkout Button (Fixed at bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    when {
                        shippingAddress.isBlank() -> {
                            errorMessage = "Vui lòng nhập địa chỉ giao hàng"
                        }
                        phoneNumber.isBlank() -> {
                            errorMessage = "Vui lòng nhập số điện thoại"
                        }
                        selectedPaymentMethod == null -> {
                            errorMessage = "Vui lòng chọn phương thức thanh toán"
                        }
                        else -> {
                            errorMessage = null
                            isProcessing = true
                            // Generate order ID
                            val orderId = "ORD_${System.currentTimeMillis()}"
                            // Process payment
                            onCheckoutSuccess(
                                orderId,
                                selectedPaymentMethod!!,
                                shippingAddress,
                                phoneNumber,
                                note
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                ),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Đặt hàng - ${formatPrice(finalTotal)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodItem(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = paymentMethod.isEnabled) { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFFF9800) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF3E0) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { if (paymentMethod.isEnabled) onSelect() },
                enabled = paymentMethod.isEnabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFFFF9800)
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paymentMethod.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (paymentMethod.isEnabled) Color.Black else Color.Gray
                )
                Text(
                    text = paymentMethod.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (!paymentMethod.isEnabled) {
                    Text(
                        text = "(Sắp có)",
                        fontSize = 11.sp,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(price)
}