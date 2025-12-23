package com.example.shopper.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ScheduleSend
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.shopper.models.Product
import com.example.shopper.models.Order
import com.example.shopper.models.Address // <-- THÊM: Import Model Address
import com.example.shopper.models.PaymentMethodType
import com.example.shopper.ui.theme.componnents.ToolBarHeader
<<<<<<< HEAD
import com.example.shopper.ui.theme.page.AdminDashboardScreen
import com.example.shopper.ui.theme.page.AdminOrdersScreen
import com.example.shopper.ui.theme.page.AdminProductsScreen
import com.example.shopper.ui.theme.page.AdminUsersScreen
import com.example.shopper.ui.theme.page.CartScreen
import com.example.shopper.ui.theme.page.CategoryScreen
import com.example.shopper.ui.theme.page.CheckoutScreen
import com.example.shopper.ui.theme.page.EditProfileScreen
import com.example.shopper.ui.theme.page.OrderDetailScreen
import com.example.shopper.ui.theme.page.OrderListScreen
import com.example.shopper.ui.theme.page.PaymentQRScreen
import com.example.shopper.ui.theme.page.ProductDetailScreen
import com.example.shopper.ui.theme.page.ProductReviewsScreen
import com.example.shopper.ui.theme.page.ProductsByCategoryScreen
import com.example.shopper.ui.theme.page.ProfileScreen
import com.example.shopper.ui.theme.page.SearchScreen
import com.example.shopper.ui.theme.page.WriteReviewScreen
=======
import com.example.shopper.ui.theme.page.*
>>>>>>> update-ui-password
import com.example.shopper.ui.theme.viewmodel.CartViewModel
import com.example.shopper.ui.theme.viewmodel.OrderViewModel
import com.example.shopper.ui.theme.viewmodel.AuthViewModel
import com.example.shopper.ui.theme.viewmodel.AddressViewModel // <-- THÊM: Import AddressViewModel
import com.google.gson.Gson
import com.example.shopper.services.AuthServices
<<<<<<< HEAD
import com.example.shopper.models.PaymentMethodType
import com.example.shopper.ui.theme.page.AdminUsersScreen

=======
import com.example.shopper.ui.theme.page.AdminDashboardScreen
>>>>>>> update-ui-password
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Trang Chủ", Icons.Default.Home)
    object Cart : BottomNavItem("cart", "Giỏ hàng", Icons.Default.ShoppingCart)
    object Category : BottomNavItem("category", "Danh mục", Icons.AutoMirrored.Filled.List)
<<<<<<< HEAD

    object Order : BottomNavItem("order", "Đơn hàng", Icons.AutoMirrored.Filled.ScheduleSend )
=======
    object Order : BottomNavItem("order", "Đơn hàng", Icons.Default.Receipt) // Đã đổi icon
>>>>>>> update-ui-password
    object Profile : BottomNavItem("profile", "Tài khoản", Icons.Default.Person)
}

// Route chi tiết
const val PRODUCT_DETAIL_ROUTE = "product_detail/{productJson}"
const val ORDER_DETAIL_ROUTE = "order_detail/{orderJson}"
const val EDIT_PROFILE_ROUTE = "edit_profile"
const val CHECKOUT_ROUTE = "checkout"
const val PAYMENT_QR_ROUTE = "payment_qr/{orderId}/{amount}"
const val WRITE_REVIEW_ROUTE = "write_review/{productId}/{productName}"
const val PRODUCT_REVIEWS_ROUTE = "product_reviews/{productId}/{productName}"

// <-- THÊM: Các Route cho Địa chỉ giao hàng
const val ADDRESS_LIST_ROUTE = "address_list"
const val ADDRESS_EDIT_DETAIL_ROUTE = "address_edit/{addressJson}"

@Composable
fun ShopBottomNav(
    onNavigateToAuth: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToChangePassword: () -> Unit // Giữ lại hàm này
) {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()
    val productViewModel: com.example.shopper.ui.theme.viewmodel.ProductViewModel = viewModel()
    val addressViewModel: AddressViewModel = viewModel() // Khai báo AddressViewModel

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()

<<<<<<< HEAD
    // Load giỏ hàng khi user đăng nhập
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            cartViewModel.loadCartFromFirestore()
            android.util.Log.d("ShopBottomNav", "Loading cart from Firestore for logged in user")
        } else {
            // Xóa giỏ hàng local khi logout
            cartViewModel.clearCart()
        }
    }

    // Nếu là admin, chuyển sang dashboard admin
    LaunchedEffect(isLoggedIn, userRole) {
        if (isLoggedIn && userRole == "admin") {
            navController.navigate("admin_dashboard") {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
                restoreState = false
            }
        }
    }

    // Xác định các route cần hiển thị toolbar
    val toolbarRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Cart.route,
        BottomNavItem.Category.route,
        BottomNavItem.Order.route,
        BottomNavItem.Profile.route,
        "search"
=======
//    LaunchedEffect(isLoggedIn, userRole) {
//        if (isLoggedIn && userRole == "admin") {
//            navController.navigate("admin_dashboard") {
//                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
//                launchSingleTop = true
//                restoreState = false
//            }
//        }
//    }
//    // Chuyển hướng sang admin ngay khi đăng nhập thành công
//    LaunchedEffect(isLoggedIn, userRole) {
//        android.util.Log.d("ShopBottomNav", "isLoggedIn=$isLoggedIn, userRole=$userRole")
//        if (isLoggedIn && userRole == "admin") {
//            navController.navigate("admin_dashboard") {
//                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
//                launchSingleTop = true
//                restoreState = false
//            }
//        }
//    }
    // Danh sách items cho Bottom Navigation (không hiển thị nếu là admin)
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Cart,
        BottomNavItem.Category,
        BottomNavItem.Order,
        BottomNavItem.Profile
>>>>>>> update-ui-password
    )

    // Điều hướng sang Admin Dashboard nếu là admin
//    LaunchedEffect(isLoggedIn, userRole) {
//        if (isLoggedIn && userRole == "admin") {
//            navController.navigate("admin_dashboard") {
//                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
//                launchSingleTop = true
//                restoreState = false
//            }
//        }
//    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Danh sách các route KHÔNG cần hiển thị Toolbar chính
    val routesWithoutToolbar = listOf(
        "product_detail", "order_detail", "category_products", "edit_profile",
        "checkout", PAYMENT_QR_ROUTE.substringBefore("/"),
        ADDRESS_LIST_ROUTE, "address_add", "address_edit",
        "admin" // Các màn hình admin tự quản lý toolbar
    )

    // Xác định xem có nên hiển thị Bottom Bar không
    val shouldShowBottomBar = currentRoute in bottomNavItems.map { it.route } && userRole != "admin"

    // Xác định xem có nên hiển thị Toolbar (ToolBarHeader) không
    val shouldShowToolbar = currentRoute?.let { route ->
        routesWithoutToolbar.none { route.contains(it) } && userRole != "admin"
    } ?: true


    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            if (shouldShowToolbar) {
                ToolBarHeader(
                    title = "Shopper",
                    onSearchClick = {
                        navController.navigate("search")
                    },
                    onCartClick = {
                        navController.navigate(BottomNavItem.Cart.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        bottomBar = {
<<<<<<< HEAD
            val items = if (userRole == "admin") {
                listOf(BottomNavItem.Profile)
            } else {
                listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Cart,
                    BottomNavItem.Category,
                    BottomNavItem.Order,
                    BottomNavItem.Profile
                )
            }
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            // Nếu là admin, chỉ cho phép vào mục Profile
                            if (userRole != "admin" || item == BottomNavItem.Profile) {
=======
            if (shouldShowBottomBar) {
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
>>>>>>> update-ui-password
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
<<<<<<< HEAD
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isSelected) Color(0xFFFFE0B2) else Color.Transparent,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) Color(0xFFFF9800) else Color.Gray
=======
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (isSelected) Color(0xFFFFE0B2) else Color.Transparent,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) Color(0xFFFF9800) else Color.Gray
                                    )
                                }
                            },
                            label = {
                                Text(
                                    item.title,
                                    color = if (isSelected) Color(0xFFFF9800) else Color.Gray
>>>>>>> update-ui-password
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {

            // HOME
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onProductClick = { product ->
                        val productJson = Gson().toJson(product)
                        val encodedJson = java.net.URLEncoder.encode(productJson, "UTF-8")
                        navController.navigate("product_detail/$encodedJson")
                    },
                    onCategoryClick = { categoryId, categoryName ->
                        val encoded = java.net.URLEncoder.encode(categoryId, "UTF-8")
                        val encodedName = java.net.URLEncoder.encode(categoryName, "UTF-8")
                        navController.navigate("category_products/$encoded?name=$encodedName")
                    }
                )
            }

            // CART
            composable(BottomNavItem.Cart.route) {
                if (isLoggedIn) {
                    CartScreen(
                        cartViewModel = cartViewModel,
                        onCheckout = { items, total ->
                            // Store cart data in ViewModel for checkout
                            cartViewModel.prepareCheckout()
                            navController.navigate(CHECKOUT_ROUTE)
                        }
                    )
                } else {
                    onNavigateToAuth()
                }
            }
            // CATEGORY
            composable(BottomNavItem.Category.route) {
                CategoryScreen(onCategoryClick = { categoryId, categoryName ->
                    val encoded = java.net.URLEncoder.encode(categoryId, "UTF-8")
                    val encodedName = java.net.URLEncoder.encode(categoryName, "UTF-8")
                    navController.navigate("category_products/$encoded?name=$encodedName")
                })
            }

            // ORDER LIST
            composable(BottomNavItem.Order.route) {
                if (isLoggedIn) {
                    OrderListScreen(
                        viewModel = orderViewModel,
                        onOrderClick = { order ->
                            val orderJson = Gson().toJson(order)
                            val encodedJson = java.net.URLEncoder.encode(orderJson, "UTF-8")
                            navController.navigate("order_detail/$encodedJson")
                        }
                    )
                } else {
                    onNavigateToAuth()
                }
            }

            // PROFILE
            composable(BottomNavItem.Profile.route) {
                if(isLoggedIn) {
                    var avatarBase64 by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        AuthServices.getAvatarBase64 { base64 ->
                            avatarBase64 = base64
                        }
                    }

                    ProfileScreen(
                        userName = AuthServices.getCurrentUser()?.displayName ?: "Người dùng",
                        userEmail = AuthServices.getCurrentUser()?.email ?: "",
                        userAvatar = avatarBase64,
                        onMenuItemClick = { action ->
                            when (action) {
                                "orders" -> {
                                    navController.navigate(BottomNavItem.Order.route)
                                }
                                "logout" -> {
                                    authViewModel.logout()
                                    onNavigateToAuth()
                                }
                                // LOGIC ĐIỀU HƯỚNG TỚI MÀN HÌNH ĐỔI MẬT KHẨU
                                "change_password" -> {
                                    onNavigateToChangePassword()
                                }
                                // <-- THÊM: LOGIC ĐIỀU HƯỚNG TỚI MÀN HÌNH QUẢN LÝ ĐỊA CHỈ
                                "addresses" -> { // Đã sửa từ "address" sang "addresses" để phù hợp với ProfileScreen
                                    navController.navigate(ADDRESS_LIST_ROUTE)
                                }
                                else -> {}
                            }
                        },
                        onEditProfileClick = {
                            navController.navigate(EDIT_PROFILE_ROUTE)
                        }
                    )
                } else {
                    onNavigateToAuth()
                }
            }


            // PRODUCT DETAIL
            composable(
                route = PRODUCT_DETAIL_ROUTE,
                arguments = listOf(navArgument("productJson") { type = NavType.StringType })
            ) {
                val productJson = it.arguments?.getString("productJson") ?: return@composable
                val decoded = java.net.URLDecoder.decode(productJson, "UTF-8")
                val product = Gson().fromJson(decoded, Product::class.java)

                ProductDetailScreen(
                    product = product,
                    onBackClick = { navController.popBackStack() },
                    onAddToCart = { p, q ->
                        cartViewModel.addToCart(p, q)
                        navController.navigate(BottomNavItem.Cart.route)
                    },
                    onViewAllReviews = {
                        val encodedName = java.net.URLEncoder.encode(product.name, "UTF-8")
                        navController.navigate("product_reviews/${product.id}/$encodedName")
                    },
                    onWriteReview = {
                        val encodedName = java.net.URLEncoder.encode(product.name, "UTF-8")
                        navController.navigate("write_review/${product.id}/$encodedName")
                    }
                )
            }

            // ORDER DETAIL
            composable(
                route = ORDER_DETAIL_ROUTE,
                arguments = listOf(navArgument("orderJson") { type = NavType.StringType })
            ) {
                val orderJson = it.arguments?.getString("orderJson") ?: return@composable
                val decoded = java.net.URLDecoder.decode(orderJson, "UTF-8")
                val order = Gson().fromJson(decoded, Order::class.java)

                OrderDetailScreen(
                    order = order,
                    onBackClick = { navController.popBackStack() },
                    onCancelOrder = { id ->
                        orderViewModel.cancelOrder(id)
                        navController.popBackStack()
                    },
                    onReorder = { id ->
                        orderViewModel.reorder(id, cartViewModel) {
                            navController.navigate(BottomNavItem.Cart.route)
                        }
                    }
                )
            }

            // PRODUCTS BY CATEGORY
            composable(
                route = "category_products/{categoryId}?name={categoryName}",
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.StringType },
                    navArgument("categoryName") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId")?.let {
                    java.net.URLDecoder.decode(it, "UTF-8")
                } ?: ""
                val categoryName = backStackEntry.arguments?.getString("categoryName")?.let {
                    java.net.URLDecoder.decode(it, "UTF-8")
                } ?: ""

                ProductsByCategoryScreen(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    onProductClick = { product ->
                        val productJson = Gson().toJson(product)
                        val encodedJson = java.net.URLEncoder.encode(productJson, "UTF-8")
                        navController.navigate("product_detail/$encodedJson")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // EDIT PROFILE
            composable(EDIT_PROFILE_ROUTE) {
                val currentUser = AuthServices.getCurrentUser()
                EditProfileScreen(
                    userName = currentUser?.displayName ?: "",
                    userEmail = currentUser?.email ?: "",
                    userPhone = "", // Phone sẽ được load bởi EditProfileScreen VM
                    userAvatar = currentUser?.photoUrl?.toString(),
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { name, phone ->
                        authViewModel.updateProfile(name, phone)
                        navController.popBackStack()
                    }
                )
            }

            // CHECKOUT
            composable(CHECKOUT_ROUTE) {
                val cartItems = cartViewModel.cartItems.toList()
                val total = cartItems.sumOf { item ->
                    val quantity = cartViewModel.quantities[item.productId] ?: item.quantity
                    item.price * quantity
                }

                CheckoutScreen(
                    cartItems = cartItems,
                    totalAmount = total,
                    onBackClick = { navController.popBackStack() },
                    onCheckoutSuccess = { orderId, paymentMethod, shippingAddress, phoneNumber, note ->
                        // Tính tổng tiền bao gồm phí ship
                        val shippingFee = 30000.0
                        val finalTotal = total + shippingFee

                        if (paymentMethod == PaymentMethodType.BANK_TRANSFER) {
                            // Lưu thông tin đơn hàng tạm thời vào ViewModel (chưa xác nhận)
                            orderViewModel.savePendingOrder(
                                orderId = orderId,
                                items = cartItems,
                                total = finalTotal,
                                paymentMethod = paymentMethod,
                                shippingAddress = shippingAddress,
                                phoneNumber = phoneNumber,
                                note = note
                            )
                            // Chuyển đến màn hình QR
                            navController.navigate("payment_qr/$orderId/$finalTotal")
                        } else {
                            // Các phương thức thanh toán khác: tạo đơn hàng ngay
                            orderViewModel.createOrder(
                                orderId = orderId,
                                items = cartItems,
                                total = finalTotal,
                                paymentMethod = paymentMethod,
                                shippingAddress = shippingAddress,
                                phoneNumber = phoneNumber,
                                note = note
                            )
                            // Clear cart
                            cartViewModel.clearCart()
                            // Navigate to Home with clean back stack
                            navController.navigate(BottomNavItem.Home.route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    }
                )
            }

            // PAYMENT QR
            composable(
                route = PAYMENT_QR_ROUTE,
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.StringType }
                )
            ) {
                val orderId = it.arguments?.getString("orderId") ?: return@composable
                val amountStr = it.arguments?.getString("amount") ?: return@composable
                val amount = amountStr.toDoubleOrNull() ?: 0.0

                PaymentQRScreen(
                    orderId = orderId,
                    totalAmount = amount,
                    onBackClick = {
                        // Hủy đơn hàng pending
                        orderViewModel.cancelPendingOrder()
                        navController.popBackStack()
                    },
                    onPaymentConfirmed = {
                        // Xác nhận thanh toán - tạo đơn hàng từ pending order
                        orderViewModel.confirmPendingOrder()
                        // Clear cart
                        cartViewModel.clearCart()
                        // Navigate to Home and clear entire back stack
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
            }

            // SEARCH
            composable("search") {
                SearchScreen(
                    navController = navController,
                    productViewModel = productViewModel,
                    onProductClick = { product ->
                        val productJson = Gson().toJson(product)
                        val encodedJson = java.net.URLEncoder.encode(productJson, "UTF-8")
                        navController.navigate("product_detail/$encodedJson")
                    }
                )
            }

<<<<<<< HEAD
            // WRITE REVIEW
            composable(
                route = WRITE_REVIEW_ROUTE,
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("productName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val productName = backStackEntry.arguments?.getString("productName")?.let {
                    java.net.URLDecoder.decode(it, "UTF-8")
                } ?: ""

                WriteReviewScreen(
                    productId = productId,
                    productName = productName,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // PRODUCT REVIEWS
            composable(
                route = PRODUCT_REVIEWS_ROUTE,
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("productName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val productName = backStackEntry.arguments?.getString("productName")?.let {
                    java.net.URLDecoder.decode(it, "UTF-8")
                } ?: ""

                ProductReviewsScreen(
                    productId = productId,
                    productName = productName,
                    onBackClick = { navController.popBackStack() },
                    onWriteReviewClick = {
                        val encodedName = java.net.URLEncoder.encode(productName, "UTF-8")
                        navController.navigate("write_review/$productId/$encodedName")
=======
            // CÁC COMPOSABLE CHO ĐỊA CHỈ GIAO HÀNG
            // ADDRESS LIST
            composable(ADDRESS_LIST_ROUTE) {
                AddressListScreen(
                    viewModel = addressViewModel, // Sử dụng AddressViewModel đã khai báo
                    onBack = { navController.popBackStack() },
                    onAddAddress = {
                        navController.navigate("address_add")
                    },
                    onEditAddress = { address ->
                        val addressJson = Gson().toJson(address)
                        val encodedJson = java.net.URLEncoder.encode(addressJson, "UTF-8")
                        navController.navigate("address_edit/$encodedJson")
>>>>>>> update-ui-password
                    }
                )
            }

<<<<<<< HEAD
=======
            // ADDRESS ADD (Sử dụng AddressEditScreen với addressToEdit = null)
            composable("address_add") {
                AddressEditScreen(
                    addressToEdit = null,
                    onBack = { navController.popBackStack() },
                    viewModel = addressViewModel // Truyền AddressViewModel
                )
            }

            // ADDRESS EDIT
            composable(
                route = ADDRESS_EDIT_DETAIL_ROUTE,
                arguments = listOf(navArgument("addressJson") { type = NavType.StringType })
            ) {
                val addressJson = it.arguments?.getString("addressJson") ?: return@composable
                val decoded = java.net.URLDecoder.decode(addressJson, "UTF-8")
                val address = Gson().fromJson(decoded, Address::class.java)

                AddressEditScreen(
                    addressToEdit = address,
                    onBack = { navController.popBackStack() },
                    viewModel = addressViewModel // Truyền AddressViewModel
                )
            }

            // ADMIN ROUTES

>>>>>>> update-ui-password
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
            // Thêm route cho admin_users với hàm onBack để quay lại trang admin_dashboard
            composable("admin_users") {
                AdminUsersScreen(onBack = { navController.popBackStack() })
            }
            // TODO: Thêm các màn hình admin_reports
        }
    }
}