package com.example.shopper.ui.theme.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.shopper.models.UserProfile
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var users by remember { mutableStateOf(listOf<UserItem>()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    // Lắng nghe realtime danh sách user, không orderBy Firestore để tránh lỗi nếu thiếu trường
    LaunchedEffect(Unit) {
        db.collection("users")
            .addSnapshotListener { snapshot, e ->
                loading = false
                if (e != null) {
                    error = "Lỗi tải danh sách người dùng: ${e.message}"
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val userList = snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("name") ?: ""
                        val email = doc.getString("email") ?: ""
                        val phone = doc.getString("phone") ?: ""
                        val address = doc.getString("address") ?: ""
                        val createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                        val isLocked = doc.getBoolean("isLocked") ?: false
                        val uid = doc.id
                        UserItem(uid, name, email, phone, address, createdAt, isLocked)
                    }
                    users = userList.sortedByDescending { it.createdAt }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý người dùng") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (error.isNotEmpty()) {
                Text(error, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (users.isEmpty()) {
                Text("Không có người dùng nào hoặc dữ liệu bị lỗi!", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(users) { user ->
                        Column {
                            if (user.name.isBlank()) Text("Thiếu tên người dùng", color = Color.Red)
                            if (user.email.isBlank()) Text("Thiếu email", color = Color.Red)
                            if (user.phone.isBlank()) Text("Thiếu số điện thoại", color = Color.Red)
                            if (user.address.isBlank()) Text("Thiếu địa chỉ", color = Color.Red)
                            if (user.createdAt <= 0L) Text("Thiếu hoặc sai ngày tạo tài khoản", color = Color.Red)
                            UserRow(user = user, onToggleLock = { uid, lock ->
                                db.collection("users").document(uid)
                                    .update("isLocked", lock)
                            })
                        }
                    }
                }
            }
        }
    }
}

data class UserItem(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val createdAt: Long,
    val isLocked: Boolean
)

@Composable
fun UserRow(user: UserItem, onToggleLock: (String, Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Tên: ${user.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Text("SĐT: ${user.phone}", style = MaterialTheme.typography.bodyMedium)
            Text("Địa chỉ: ${user.address}", style = MaterialTheme.typography.bodyMedium)
            if (user.createdAt > 0L) {
                Text("Ngày tạo: " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(user.createdAt)), style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Ngày tạo: Không xác định", style = MaterialTheme.typography.bodySmall)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                val lockText = if (user.isLocked) "Mở khóa" else "Khóa tài khoản"
                val lockColor = if (user.isLocked) Color(0xFF4CAF50) else Color(0xFFF44336)
                Button(
                    onClick = { onToggleLock(user.uid, !user.isLocked) },
                    colors = ButtonDefaults.buttonColors(containerColor = lockColor)
                ) {
                    Text(lockText)
                }
            }
        }
    }
}
