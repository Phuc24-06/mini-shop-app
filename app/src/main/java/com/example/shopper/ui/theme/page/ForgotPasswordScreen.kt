package com.example.shopper.ui.theme.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.ui.theme.viewmodel.AuthState
import com.example.shopper.ui.theme.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Trạng thái cho icon bật/tắt mật khẩu
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Quên Mật Khẩu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tiêu đề và hướng dẫn
            Text(
                text = "Cập nhật Mật khẩu",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Vui lòng nhập Email và Số điện thoại đã đăng ký để xác thực và thiết lập mật khẩu mới.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(32.dp))

            // TextFields cho Email và Số điện thoại
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // TextFields cho Mật khẩu mới
//            OutlinedTextField(
//                value = newPassword,
//                onValueChange = { newPassword = it },
//                label = { Text("Mật khẩu mới") },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Password,
//                    imeAction = ImeAction.Next
//                ),
//                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
//                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
//                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            )
            Spacer(Modifier.height(16.dp))

            // TextFields cho Xác nhận mật khẩu
//            OutlinedTextField(
//                value = confirmPassword,
//                onValueChange = { confirmPassword = it },
//                label = { Text("Xác nhận mật khẩu mới") },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Password,
//                    imeAction = ImeAction.Done
//                ),
//                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
//                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
//                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            )

            Spacer(Modifier.height(32.dp))

            // Nút Cập nhật Mật khẩu (Màu cam chủ đạo)
            Button(
                onClick = {
                    when {
                        email.isBlank() || phone.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                            scope.launch { snackbarHostState.showSnackbar("Vui lòng điền đầy đủ thông tin") }
                        }
                        newPassword != confirmPassword -> {
                            scope.launch { snackbarHostState.showSnackbar("Mật khẩu mới không khớp") }
                        }
                        else -> viewModel.updatePasswordWithEmailAndPhone(email, phone, newPassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("Cập nhật Mật khẩu", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(Modifier.height(12.dp))

            // Quay lại đăng nhập
            TextButton(onClick = onBack) {
                Text(
                    "Quay lại Đăng nhập",
                    color = Color(0xFFFF9800),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }

    // Xử lý AuthState (Giữ nguyên logic cũ)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.PasswordResetSuccess -> {
                val message = (authState as AuthState.PasswordResetSuccess).message
                scope.launch { snackbarHostState.showSnackbar(message) }
                // Sau khi thành công, có thể điều hướng về màn hình đăng nhập
                onBack()
            }
            is AuthState.Error ->
                scope.launch { snackbarHostState.showSnackbar((authState as AuthState.Error).error) }
            else -> {}
        }
        // Luôn reset trạng thái sau khi xử lý (để tránh hiển thị lại Snackbar)
        if (authState is AuthState.PasswordResetSuccess || authState is AuthState.Error) {
            viewModel.resetAuthState()
        }
    }
}