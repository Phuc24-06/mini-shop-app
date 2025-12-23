package com.example.shopper.ui.theme.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shopper.ui.theme.viewmodel.ProductViewModel
import com.example.shopper.models.Product
import com.example.shopper.ui.theme.componnents.ProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    onProductClick: (Product) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by productViewModel.searchResults.collectAsState()
    val isSearching by productViewModel.isSearching.collectAsState()

    // Tự động tìm kiếm khi nhập liệu
    LaunchedEffect(searchQuery) {
        productViewModel.searchProducts(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Tìm kiếm sản phẩm...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Black
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color(0xFFFF9800),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        productViewModel.clearSearchResults()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9800)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Divider để tách biệt thanh search với nội dung
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // Nội dung chính
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (searchQuery.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nhập tên sản phẩm để tìm kiếm", color = Color.Gray)
                    }
                } else if (isSearching) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFFF9800))
                    }
                } else if (searchResults.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Không tìm thấy sản phẩm nào", color = Color.Gray)
                    }
                } else {
                    // Hiển thị kết quả dạng Grid giống HomeScreen
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(searchResults) { product ->
                            ProductItem(
                                product = product,
                                onClick = { onProductClick(product) }
                            )
                        }
                    }
                }
            }
        }
    }
}

