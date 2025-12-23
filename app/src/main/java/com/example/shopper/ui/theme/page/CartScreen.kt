package com.example.shopper.ui.theme.page

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopper.models.CartItem
import com.example.shopper.ui.theme.ShopperTheme
import com.example.shopper.ui.theme.componnents.CartItemCard
import com.example.shopper.ui.theme.viewmodel.CartViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel? = null,
    onCheckout: (items: List<CartItem>, total: Double) -> Unit = { _, _ -> }
) {
    // Dữ liệu mẫu cho preview
    val previewCartItems = remember {
        mutableStateListOf(
            CartItem("1", "Áo thun nam", 1, 250000.0, "https://example.com/image1.jpg"),
            CartItem("2", "Quần jean nữ", 1, 550000.0, "https://example.com/image2.jpg"),
            CartItem("3", "Giày thể thao", 1, 1200000.0, "https://example.com/image3.jpg")
        )
    }

    val previewQuantities = remember {
        mutableStateMapOf<String, Int>().apply {
            previewCartItems.forEach { item ->
                this[item.productId] = item.quantity
            }
        }
    }

    // Sử dụng snapshot state để đọc từ ViewModel
    val displayCartItems = cartViewModel?.cartItems ?: previewCartItems
    val quantities = cartViewModel?.quantities ?: previewQuantities


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))

    ) {

        if (displayCartItems.isEmpty()) {
            // Empty cart view
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Empty cart",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Giỏ hàng trống",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Cart items list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = displayCartItems.size,
                        key = { index -> displayCartItems[index].productId }
                    ) { index ->
                        val item = displayCartItems[index]
                        val currentQuantity = quantities[item.productId] ?: item.quantity

                        CartItemCard(
                            item = item,
                            quantity = currentQuantity,
                            onIncreaseQuantity = {
                                cartViewModel?.increaseQuantity(item.productId)
                            },
                            onDecreaseQuantity = {
                                cartViewModel?.decreaseQuantity(item.productId)
                            },
                            onRemove = {
                                cartViewModel?.removeFromCart(item.productId)
                            }
                        )
                    }
                }

                // Checkout section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tạm tính:", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${calculateSubtotal(displayCartItems, quantities)}đ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Tổng cộng:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${calculateSubtotal(displayCartItems, quantities)}đ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val total = displayCartItems.sumOf { item ->
                                    val quantity = quantities[item.productId] ?: item.quantity
                                    item.price * quantity
                                }
                                onCheckout(displayCartItems.toList(), total)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            )
                        ) {
                            Text("Thanh toán", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}



private fun calculateSubtotal(items: List<CartItem>, quantities: Map<String, Int>): String {
    val total = items.sumOf { item ->
        val quantity = quantities[item.productId] ?: item.quantity
        item.price * quantity
    }
    return String.format(Locale.US, "%,.0f", total)
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    ShopperTheme(content = {
        CartScreen()
    })
}