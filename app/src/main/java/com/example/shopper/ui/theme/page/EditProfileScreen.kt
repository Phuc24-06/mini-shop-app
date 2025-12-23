package com.example.shopper.ui.theme.page

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shopper.services.AuthServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userName: String = "",
    userEmail: String = "",
    userPhone: String = "",
    userAvatar: String? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: (name: String, phone: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(userName) }
    var phone by remember { mutableStateOf(userPhone) }
    var avatarBase64 by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoadingPhone by remember { mutableStateOf(true) }
    var isUploadingAvatar by remember { mutableStateOf(false) }
    var showUploadError by remember { mutableStateOf(false) }
    var uploadErrorMessage by remember { mutableStateOf("") }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            android.util.Log.d("EditProfileScreen", "Image selected: $uri")
            isUploadingAvatar = true
            AuthServices.uploadAvatar(uri, context) { success, message, base64String ->
                isUploadingAvatar = false
                android.util.Log.d("EditProfileScreen", "Upload result: success=$success, message=$message")
                if (success && base64String != null) {
                    avatarBase64 = base64String
                    Toast.makeText(context, "Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    showUploadError = true
                    uploadErrorMessage = message
                    Toast.makeText(context, "Lỗi: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Load phone và avatar từ Firestore
    LaunchedEffect(Unit) {
        isLoadingPhone = true
        AuthServices.getUserPhone { phoneFromDb ->
            if (!phoneFromDb.isNullOrBlank()) {
                phone = phoneFromDb
            }
            isLoadingPhone = false
        }

        // Load avatar Base64
        AuthServices.getAvatarBase64 { base64 ->
            avatarBase64 = base64
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            // Avatar Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                        .clickable(enabled = !isUploadingAvatar) {
                            // Mở image picker
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarBase64 != null) {
                        // Hiển thị ảnh từ Base64
                        val bitmap = remember(avatarBase64) {
                            try {
                                val decodedBytes = android.util.Base64.decode(avatarBase64, android.util.Base64.DEFAULT)
                                android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (bitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Avatar",
                                modifier = Modifier.size(60.dp),
                                tint = Color.Gray
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Avatar",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }

                    // Loading overlay khi đang upload
                    if (isUploadingAvatar) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Camera icon overlay
                    if (!isUploadingAvatar) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9800))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Name Field
                    Text(
                        text = "Họ và tên",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập họ và tên") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field (Read-only)
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color(0xFFE0E0E0),
                            disabledTextColor = Color.Gray
                        ),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.Gray
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Field
                    Text(
                        text = "Số điện thoại",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập số điện thoại") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    isSaving = true
                    onSaveClick(name, phone)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                ),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Lưu thay đổi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Additional Options
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    // Change Password
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* TODO: Change password */ }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Change Password",
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Đổi mật khẩu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Thay đổi mật khẩu đăng nhập",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Arrow",
                            tint = Color.Gray
                        )
                    }

                    Divider(color = Color(0xFFE0E0E0))



                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Snackbar hiển thị lỗi upload
        if (showUploadError) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { showUploadError = false }) {
                        Text("OK", color = Color.White)
                    }
                }
            ) {
                Text(uploadErrorMessage)
            }
        }
    }
}
