package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.ui.theme.viewmodel.AuthState
import com.example.shopper.ui.theme.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
@Composable
fun AuthenticationScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onBackClick: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }

    // Reset các trường khi chuyển đổi giữa đăng nhập và đăng ký
    LaunchedEffect(isLogin) {
        email = ""
        password = ""
        userName = ""
        viewModel.resetAuthState()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFEEEEEE)), // Nền nhẹ hơn để Card nổi bật
            contentAlignment = Alignment.Center
        ) {
            // SỬA UI: Dùng Card để làm nổi bật Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), // Đặt padding cho Card
                shape = RoundedCornerShape(16.dp), // Góc bo tròn
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Thêm bóng
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp), // Padding bên trong Card
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Back button + title (Aligned left inside the card)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFFF9800))
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = if (isLogin) "Đăng nhập" else "Đăng ký",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary // Giữ màu primary (thường là màu đậm hơn)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp)) // Tăng khoảng cách

                    // Username (only register)
                    if (!isLogin) {
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("Tên đăng nhập") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isLogin) ImeAction.Done else ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isLogin) {
                        Spacer(Modifier.height(8.dp))
                        // Nút "Quên mật khẩu?" canh phải
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(onClick = onForgotPasswordClick) {
                                Text("Quên mật khẩu?", color = Color(0xFFFF9800))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank() || (!isLogin && userName.isBlank())) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Vui lòng điền đầy đủ thông tin")
                                }
                            } else {
                                if (isLogin) viewModel.login(email, password)
                                else viewModel.register(email, password, userName)
                            }
                        },
                        enabled = authState !is AuthState.Loading,
                        modifier = Modifier
                            .fillMaxWidth() // Dùng fillMaxWidth cho nút chính
                            .height(50.dp),
                        // SỬA UI: Dùng màu cam cho nút chính
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            when (authState) {
                                is AuthState.Loading -> "Đang xử lý..."
                                else -> if (isLogin) "Đăng nhập" else "Đăng ký"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { isLogin = !isLogin }) {
                        Text(
                            if (isLogin) "Chưa có tài khoản? Đăng ký"
                            else "Đã có tài khoản? Đăng nhập",
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }
        }
    }

    // Lắng nghe authState
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.LoginSuccess -> {
                onLoginSuccess()
                viewModel.resetAuthState()
            }
            is AuthState.RegisterSuccess -> {
                val message = (authState as AuthState.RegisterSuccess).message
                snackbarHostState.showSnackbar(message)
                isLogin = true // Chuyển về màn hình đăng nhập
                delay(100)
                viewModel.resetAuthState()
            }
            is AuthState.Error -> {
                val error = (authState as AuthState.Error).error
                snackbarHostState.showSnackbar(error)
                delay(100)
                viewModel.resetAuthState()
            }
            else -> {}
        }
    }
}