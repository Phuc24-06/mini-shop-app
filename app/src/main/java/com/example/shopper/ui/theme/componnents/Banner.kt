package com.example.shopper.ui.theme.componnents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun Banner() {
    val banners = listOf(
        BannerItem(
            imageUrl = "https://picsum.photos/800/400?random=1",
            title = "Siêu Sale Hôm Nay!",
            subtitle = "Giảm đến 70% toàn bộ sản phẩm"
        ),
        BannerItem(
            imageUrl = "https://picsum.photos/800/400?random=2",
            title = "Miễn phí vận chuyển",
            subtitle = "Cho đơn hàng từ 99.000đ"
        ),
        BannerItem(
            imageUrl = "https://picsum.photos/800/400?random=3",
            title = "Hàng mới về",
            subtitle = "Khám phá bộ sưu tập mùa thu"
        )
    )

    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(8.dp)
        ) { page ->
            val banner = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                // Ảnh nền
                AsyncImage(
                    model = banner.imageUrl,
                    contentDescription = banner.title,
                    modifier = Modifier.fillMaxSize()
                )

                // Lớp gradient mờ để chữ nổi hơn
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0xAA000000) // đen mờ phía dưới
                                )
                            )
                        )
                )

                // Text đè lên ảnh
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = banner.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = banner.subtitle,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        // 🔘 Dots indicator
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            repeat(banners.size) { index ->
                val color =
                    if (pagerState.currentPage == index) Color(0xFFFF9800) else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(8.dp)
                        .background(color, RoundedCornerShape(50))
                )
            }
        }
    }
}

data class BannerItem(
    val imageUrl: String,
    val title: String,
    val subtitle: String
)
