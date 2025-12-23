package com.example.shopper.ui.theme.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.ui.theme.viewmodel.AuthState
import com.example.shopper.ui.theme.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
// THÊM: Import cho biểu tượng thành công
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color // Import Color
import androidx.compose.ui.text.font.FontWeight
// THÊM: Import cho delay
import kotlinx.coroutines.delay

// Cần opt-in cho các API thử nghiệm của Material 3 như TopAppBarDefaults
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: AuthViewModel = viewModel(),
    onPasswordChanged: () -> Unit,
    onBack: () -> Unit // Callback để xử lý sự kiện Back
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // THÊM: State để hiển thị màn hình thành công
    var isPasswordChangeSuccessful by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // HIỂN THỊ TIÊU ĐỀ KHÁC KHI THÀNH CÔNG
                        if (isPasswordChangeSuccessful) "Thành công" else "Đổi mật khẩu",
                        // Sử dụng style titleLarge cho TopAppBar
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    // Vô hiệu hóa nút Back khi đang ở màn hình thành công
                    IconButton(onClick = onBack, enabled = !isPasswordChangeSuccessful) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                // --- CẬP NHẬT MÀU SẮC PHÙ HỢP VỚI ĐĂNG NHẬP/ĐĂNG KÝ (MÀU CAM) ---
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9800), // Màu cam
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                )
            )
        }
    ) { padding ->
        // SỬA: Hiển thị giao diện dựa trên trạng thái thành công
        if (isPasswordChangeSuccessful) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle, // Biểu tượng thành công
                    contentDescription = "Thành công",
                    tint = Color(0xFF4CAF50), // Màu xanh lá cây
                    modifier = Modifier.size(96.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Đổi mật khẩu thành công!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = successMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            // Nội dung gốc: Form đổi mật khẩu
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))

                // Mật khẩu hiện tại
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Mật khẩu hiện tại") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                // Mật khẩu mới
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Mật khẩu mới") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                // Xác nhận mật khẩu mới
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu mới") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Vui lòng điền đầy đủ thông tin") }
                        } else if (newPassword != confirmPassword) {
                            scope.launch { snackbarHostState.showSnackbar("Mật khẩu mới không khớp") }
                        } else {
                            // Gọi hàm đổi mật khẩu với mật khẩu cũ để xác thực lại
                            viewModel.changePassword(
                                currentPassword = currentPassword,
                                newPassword = newPassword
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    // --- ĐỔI MÀU NÚT ĐỂ PHÙ HỢP VỚI MÀU CHỦ ĐẠO ---
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800), // Màu cam
                        contentColor = Color.White
                    )
                ) {
                    Text("Đổi mật khẩu")
                }
            }
        }
    }

    // SỬA LOGIC XỬ LÝ THÔNG BÁO THÀNH CÔNG VÀ LỖI
    LaunchedEffect(authState) {
        when (authState) {

            is AuthState.PasswordActionSuccess -> {
                val message = (authState as AuthState.PasswordActionSuccess).message
                // Cập nhật state để hiển thị màn hình thành công
                successMessage = message
                isPasswordChangeSuccessful = true
                // KHÔNG gọi onPasswordChanged() ở đây nữa
            }

            is AuthState.Error -> {
                val error = (authState as AuthState.Error).error
                scope.launch { snackbarHostState.showSnackbar(error) }
            }

            else -> {}
        }
    }

    // THÊM: Logic tự động điều hướng sau 2 giây khi thành công
    LaunchedEffect(isPasswordChangeSuccessful) {
        if (isPasswordChangeSuccessful) {
            delay(2000) // Đợi 2 giây
            onPasswordChanged()   // Điều hướng về màn hình hồ sơ
        }
    }
}