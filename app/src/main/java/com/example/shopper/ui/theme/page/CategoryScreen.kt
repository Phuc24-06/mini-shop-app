package com.example.shopper.ui.theme.page

import com.example.shopper.ui.theme.componnents.CategoryItemView
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shopper.ui.theme.viewmodel.CategoryViewModel

// Màn hình hiển thị danh sách categories
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (categoryId: String, categoryName: String) -> Unit = { _, _ -> }
) {
    val viewModel: CategoryViewModel = viewModel()
    val categories by viewModel.categories.collectAsState() // ✅ lấy dữ liệu từ ViewModel
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
        ,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(12.dp))
        if (categories.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(categories.size) { index ->
                    val item = categories[index]
                    CategoryItemView(
                        name = item.name,
                        iconUrl = item.imageUrl,
                        onClick = {
                            // ✅ Truyền cả ID và tên danh mục
                            onCategoryClick(item.id, item.name)
                        }
                    )
                }
            }
        } else {
            Text("Đang tải Category...")
        }

    }
}
