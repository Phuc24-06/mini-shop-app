package com.example.shopper.ui.theme

import com.example.shopper.ui.theme.componnents.Banner
import com.example.shopper.ui.theme.componnents.CategoryItemView
import com.example.shopper.ui.theme.componnents.ProductItem
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.models.Product
import com.example.shopper.ui.theme.viewmodel.CategoryViewModel
import com.example.shopper.ui.theme.viewmodel.ProductViewModel

@Composable
fun HomeScreen(
    onProductClick: (Product) -> Unit = {},
    onCategoryClick: (categoryId: String, categoryName: String) -> Unit = { _, _ -> }
) {
    val categoryViewModel: CategoryViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()

    val categories by categoryViewModel.categories.collectAsState()
    val products by productViewModel.products.collectAsState()
    val isLoadingProducts by productViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
        productViewModel.loadFeaturedProducts(20) // Load 20 sản phẩm đầu tiên
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Banner()

        Spacer(modifier = Modifier.height(12.dp))

        // Danh mục
        if (categories.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { item ->
                    CategoryItemView(name = item.name , iconUrl = item.imageUrl)

                    CategoryItemView(name = item.name , iconUrl = item.imageUrl, modifier = Modifier,
                        onClick = { onCategoryClick(item.id, item.name) })

                }
            }
        } else {
            Text("Đang tải danh mục...")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tiêu đề sản phẩm
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sản phẩm nổi bật",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Danh sách sản phẩm
        if (isLoadingProducts) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (products.isNotEmpty()) {
            // Grid sản phẩm - 2 cột
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                val chunkedProducts = products.chunked(2)
                chunkedProducts.forEach { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { product ->
                            ProductItem(
                                product = product,
                                modifier = Modifier.weight(1f),
                                onClick = { onProductClick(product) }
                            )
                        }
                        // Nếu hàng cuối cùng chỉ có 1 sản phẩm, thêm Spacer để cân bằng
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Chưa có sản phẩm nào",
                modifier = Modifier.padding(32.dp),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}