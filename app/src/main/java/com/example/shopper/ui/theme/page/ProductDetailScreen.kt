package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shopper.models.Product
import com.example.shopper.ui.theme.componnents.RatingStars
import com.example.shopper.ui.theme.componnents.RatingWithNumber
import com.example.shopper.ui.theme.viewmodel.ReviewViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBackClick: () -> Unit = {},
    onAddToCart: (Product, Int) -> Unit = { _, _ -> },
    onViewAllReviews: () -> Unit = {},
    onWriteReview: () -> Unit = {},
    reviewViewModel: ReviewViewModel = viewModel()
) {
    var quantity by remember { mutableStateOf(1) }
    val productRating by reviewViewModel.productRating.collectAsState()
    val reviews by reviewViewModel.reviews.collectAsState()

    // Load reviews khi vào màn hình
    LaunchedEffect(product.id) {
        android.util.Log.d("ProductDetail", "Loading reviews for product: ${product.id}")
        reviewViewModel.loadProductRating(product.id)
        reviewViewModel.loadProductReviews(product.id)
    }

    // Debug: Log reviews data
    LaunchedEffect(reviews) {
        android.util.Log.d("ProductDetail", "Reviews updated: ${reviews.size} reviews")
        reviews.forEach { review ->
            android.util.Log.d("ProductDetail", "Review: ${review.userName} - ${review.rating} stars - ${review.comment}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${product.name} ") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9800),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tổng tiền",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = formatPrice(product.price * quantity),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color(0xFFE91E63)
                        )
                    }

                    Button(
                        onClick = { onAddToCart(product, quantity) },
                        enabled = product.stock > 0 && quantity <= product.stock,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        ),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thêm vào giỏ", fontSize = 16.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
        ) {
            // Ảnh sản phẩm
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin sản phẩm
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Tên sản phẩm
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Giá
                    Text(
                        text = formatPrice(product.price),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFE91E63)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = Color.LightGray)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tồn kho
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tồn kho:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Text(
                            text = "${product.stock} sản phẩm",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = if (product.stock > 0) Color(0xFF4CAF50) else Color.Red
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chọn số lượng
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Số lượng:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )

                            IconButton(
                                onClick = { if (quantity < product.stock) quantity++ },
                                enabled = quantity < product.stock
                            ) {
                                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mô tả sản phẩm
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Mô tả sản phẩm",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.description.ifEmpty { "Chưa có mô tả cho sản phẩm này." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Đánh giá & Nhận xét
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Đánh giá sản phẩm",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.Black
                        )

                        if (productRating != null && productRating!!.totalReviews > 0) {
                            TextButton(onClick = onViewAllReviews) {
                                Text(
                                    text = "Xem tất cả",
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rating Summary
                    if (productRating != null && productRating!!.totalReviews > 0) {
                        RatingWithNumber(
                            rating = productRating!!.averageRating,
                            totalReviews = productRating!!.totalReviews
                        )
                    } else {
                        Text(
                            text = "Chưa có đánh giá nào",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))



                    Spacer(modifier = Modifier.height(16.dp))

                    // Button để viết đánh giá
                    OutlinedButton(
                        onClick = onWriteReview,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF9800)
                        )
                    ) {
                        Text("Đánh giá")
                    }



                    // Hiển thị 2-3 review đầu tiên
                    if (reviews.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray)

                        reviews.take(2).forEachIndexed { index, review ->
                            Spacer(modifier = Modifier.height(12.dp))

                            // Review content without extra Card wrapper
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Header: Avatar + Name + Rating
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(Color(0xFFFF9800)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Name + Date
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = review.userName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            if (review.isVerifiedPurchase) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Verified,
                                                    contentDescription = "Đã mua hàng",
                                                    tint = Color(0xFF4CAF50),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = review.getFormattedDate(),
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    // Rating stars
                                    RatingStars(
                                        rating = review.rating,
                                        starSize = 16.dp
                                    )
                                }

                                // Comment
                                if (review.comment.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = review.comment,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            if (index < 1 && reviews.size > 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            }
                        }

                        if (reviews.size > 2) {
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = onViewAllReviews,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Xem thêm ${reviews.size - 2} đánh giá",
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(price)
}

