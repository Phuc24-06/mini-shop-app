package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.ui.theme.componnents.*
import com.example.shopper.ui.theme.viewmodel.ReviewViewModel

import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductReviewsScreen(
    productId: String,
    productName: String,
    onBackClick: () -> Unit = {},
    onWriteReviewClick: () -> Unit = {},
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val reviews by reviewViewModel.reviews.collectAsState()
    val productRating by reviewViewModel.productRating.collectAsState()

    // Load data
    LaunchedEffect(productId) {
        reviewViewModel.loadProductReviews(productId)
        reviewViewModel.loadProductRating(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đánh giá sản phẩm", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onWriteReviewClick,
                containerColor = Color(0xFFFF9800)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Viết đánh giá",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Product Name
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = productName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Rating Summary
            if (productRating != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Average rating
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f", productRating!!.averageRating),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF9800)
                                    )
                                    RatingStars(
                                        rating = productRating!!.averageRating,
                                        starSize = 20.dp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${productRating!!.totalReviews} đánh giá",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }

                                // Rating distribution
                                Column(
                                    modifier = Modifier.weight(1.5f)
                                ) {
                                    for (stars in 5 downTo 1) {
                                        val count = when (stars) {
                                            5 -> productRating!!.fiveStars
                                            4 -> productRating!!.fourStars
                                            3 -> productRating!!.threeStars
                                            2 -> productRating!!.twoStars
                                            1 -> productRating!!.oneStar
                                            else -> 0
                                        }
                                        RatingBar(
                                            stars = stars,
                                            percentage = productRating!!.getPercentage(stars),
                                            count = count
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Reviews List
            if (reviews.isEmpty()) {
                item {
                    EmptyReviewState()
                }
            } else {
                item {
                    Text(
                        text = "Tất cả đánh giá (${reviews.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(reviews) { review ->
                    ReviewCard(review = review)
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}

