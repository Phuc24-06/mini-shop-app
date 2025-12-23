package com.example.shopper.ui.theme.componnents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * Component hiển thị rating stars (có thể tương tác hoặc chỉ xem)
 */
@Composable
fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = 24.dp,
    interactive: Boolean = false,
    onRatingChanged: (Float) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isSelected = i <= rating

            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "Star $i",
                tint = if (isSelected) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (interactive) {
                            Modifier.clickable { onRatingChanged(i.toFloat()) }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

/**
 * Component hiển thị rating với số
 */
@Composable
fun RatingWithNumber(
    rating: Float,
    totalReviews: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RatingStars(
            rating = rating,
            starSize = starSize
        )

        Text(
            text = String.format(Locale.getDefault(), "%.1f", rating),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFC107)
        )

        Text(
            text = "($totalReviews)",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

/**
 * Component hiển thị rating bar (phân bố số sao)
 */
@Composable
fun RatingBar(
    stars: Int,
    percentage: Float,
    count: Int,
    modifier: Modifier = Modifier,
    onFilterClick: (Int) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onFilterClick(stars) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Star label
        Text(
            text = "$stars",
            fontSize = 14.sp,
            modifier = Modifier.width(16.dp)
        )

        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(16.dp)
        )

        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
        ) {
            // Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        Modifier
                            .padding(0.dp)
                    ),
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawRoundRect(
                        color = Color(0xFFE0E0E0),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }

            // Foreground
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawRoundRect(
                        color = Color(0xFFFFC107),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }
        }

        // Count
        Text(
            text = "$count",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.width(40.dp)
        )
    }
}

