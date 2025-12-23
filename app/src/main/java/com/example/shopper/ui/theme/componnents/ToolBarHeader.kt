package com.example.shopper.ui.theme.componnents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolBarHeader(
        title:String,
        onBack:(() -> Unit)? = null,
        onCartClick: (() -> Unit)? = null,
        onSearchClick: (() -> Unit)? = null // Chỉ giữ lại một khai báo tham số này
) {

        TopAppBar(
                title = {
                        Text(
                                text = title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                        )
                },
                navigationIcon = {
                        if (onBack != null) {
                                IconButton(onClick = onBack) {
                                        Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Back",
                                                tint = Color.White
                                        )
                                }
                        }
                },
                actions = {
                        // Chỉ giữ lại MỘT khối IconButton cho Tìm kiếm
                        IconButton(onClick = { onSearchClick?.invoke() }) {
                                Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.White
                                )
                        }

                        // IconButton cho Giỏ hàng
                        IconButton(onClick = { onCartClick?.invoke() }) {
                                Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Cart",
                                        tint = Color.White
                                )
                        }
                }, // <--- Dấu ngoặc nhọn đóng của actions

                colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFF9800),
                        titleContentColor = Color.White
                )
        )
}