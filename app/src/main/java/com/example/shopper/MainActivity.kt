package com.example.shopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shopper.ui.components.GradientBackgroundScreen
import com.example.shopper.ui.theme.ShopperTheme
import com.example.shopper.ui.theme.ShopBottomNav
import com.example.shopper.ui.theme.page.AuthenticationScreen
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.shopper.ui.theme.page.ForgotPasswordScreen
import com.example.shopper.ui.theme.page.ChangePasswordScreen
import com.example.shopper.ui.theme.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import com.example.shopper.ui.theme.page.AdminDashboardScreen
import com.example.shopper.ui.theme.page.AdminOrdersScreen
import com.example.shopper.ui.theme.page.AdminProductsScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopperTheme {
                AppNav()
                val analytics = Firebase.analytics
                analytics.logEvent("app_open") {
                    param("msg", "Hello Firebase!")
                }
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val userRole by authViewModel.userRole.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val startDestination = remember(isLoggedIn, userRole) {
        when {
            !isLoggedIn -> "auth" // Chưa đăng nhập -> Màn hình đăng nhập
            userRole == "admin" -> "admin_dashboard" // Đã đăng nhập và là Admin -> Trang Admin
            else -> "home" // Đã đăng nhập và là Customer -> Trang Home
        }
    }
    GradientBackgroundScreen { // ✅ Gradient bao ngoài NavHost
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            composable("auth") {
                AuthenticationScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.navigate("home") {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onForgotPasswordClick = {
                        navController.navigate("forgot_password")
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate("forgot_password")
                    }
                )
            }

            // MÀN HÌNH QUÊN MẬT KHẨU
            composable("forgot_password") {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack() } // Quay lại Auth Screen
                )
            }

            // MÀN HÌNH ĐỔI MẬT KHẨU (truy cập từ Profile)
            composable("change_password") {
                ChangePasswordScreen(
                    onPasswordChanged = { navController.popBackStack() }, // Quay lại Profile
                    onBack = { navController.popBackStack() }
                )
            }
            // Thêm route cho admin dashboard
            composable("admin_dashboard") {
                AdminDashboardScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateTo = { route: String ->
                        when (route) {
                            "products" -> navController.navigate("admin_products")
                            "admin_orders" -> navController.navigate("admin_orders")
                            "users" -> navController.navigate("admin_users") // Màn hình giả định
                            "reports" -> navController.navigate("admin_reports") // Màn hình giả định
                        }
                    }
                )
            }
            // Thêm route cho admin_products
            composable("admin_products") {
                AdminProductsScreen(
                    onBack = { navController.popBackStack() },
                    onAddProduct = { /* TODO: Hiển thị form thêm sản phẩm */ },
                    onEditProduct = { productId -> /* TODO: Hiển thị form sửa sản phẩm */ },
                    onDeleteProduct = { productId -> /* TODO: Xóa sản phẩm */ }
                )
            }
            // Thêm route cho admin_orders
            composable("admin_orders") {
                AdminOrdersScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("home") {
                ShopBottomNav(
                    onNavigateToAuth = {
                        navController.navigate("auth")
                    },
                    // THÊM ĐIỀU HƯỚNG TỪ PROFILE SANG CHANGE_PASSWORD
                    onNavigateToChangePassword = {
                        navController.navigate("change_password")
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    ShopperTheme {
        AppNav()
    }
}