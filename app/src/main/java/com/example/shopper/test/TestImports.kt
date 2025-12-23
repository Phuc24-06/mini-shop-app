package com.example.shopper.test

import androidx.compose.runtime.Composable
import com.example.shopper.ui.theme.componnents.Banner
import com.example.shopper.ui.theme.componnents.CategoryItemView
import com.example.shopper.ui.theme.componnents.ProductItem

// Test file to verify imports work
@Composable
fun TestImports() {
    Banner()
    CategoryItemView(name = "Test", iconUrl = "")
}

