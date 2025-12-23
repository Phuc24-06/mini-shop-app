package com.example.shopper.ui.theme.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.models.Address
import com.example.shopper.ui.theme.viewmodel.AddressViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    viewModel: AddressViewModel = viewModel(),
    onBack: () -> Unit,
    onAddAddress: () -> Unit,
    onEditAddress: (Address) -> Unit
) {
    val addresses by viewModel.addresses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val status by viewModel.operationStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            // ✅ SỬA: Thay thế Row tùy chỉnh bằng CenterAlignedTopAppBar
            CenterAlignedTopAppBar(
                title = { Text("Địa chỉ giao hàng", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // Đảm bảo nền khớp với Scaffold
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAddress,
                containerColor = Color(0xFFFF9800) // Dùng màu cam chủ đạo
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm địa chỉ", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        // Xử lý thông báo trạng thái
        LaunchedEffect(status) {
            status?.let {
                scope.launch { snackbarHostState.showSnackbar(it) }
                viewModel.clearStatus()
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF9800))
            }
        } else if (addresses.isEmpty()) {
            // ✅ SỬA: Cải thiện trạng thái trống (Empty State)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "No Address",
                    modifier = Modifier.size(64.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Chưa có địa chỉ nào",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vui lòng thêm địa chỉ giao hàng để tiếp tục.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(addresses) { address ->

                    val onAddressClick: (Address) -> Unit = { clickedAddress ->
                        if (!clickedAddress.isDefault) {
                            // Đặt làm mặc định nếu chưa phải là mặc định
                            viewModel.setDefaultAddress(clickedAddress.id)
                        }
                    }

                    AddressItem(
                        address = address,
                        onEdit = onEditAddress,
                        onDelete = { viewModel.deleteAddress(address.id) },
                        onAddressClick = onAddressClick // Gán hành động click đơn giản
                    )
                }
            }
        }
    }
}

@Composable
fun AddressItem(address: Address, onEdit: (Address) -> Unit, onDelete: () -> Unit, onAddressClick: (Address) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddressClick(address) },
        // ✅ THÊM: Màu nền và độ cao cho Card
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tên người nhận
                Text(
                    text = address.receiverName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (address.isDefault) FontWeight.Bold else FontWeight.SemiBold, // Dùng SemiBold cho địa chỉ không mặc định
                    color = if (address.isDefault) Color(0xFFFF9800) else Color.Black // Dùng màu cam chủ đạo
                )
                // Chip Mặc định
                if (address.isDefault) {
                    AssistChip(
                        onClick = { /* Do nothing */ },
                        label = { Text("Mặc định", fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFFFF9800), // Vẫn giữ màu cam nhất quán
                            labelColor = Color.White
                        ),
                        // THÊM: Giảm padding để chip nhỏ gọn hơn
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            // ✅ SỬA: Thêm màu xám nhạt cho SĐT và Địa chỉ
            Text(text = "SĐT: ${address.phoneNumber}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = "Địa chỉ: ${address.getFullAddress()}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            // Nút Sửa/Xóa (chỉ hiển thị)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Giữ lại chức năng Sửa riêng biệt
                TextButton(onClick = { onEdit(address) }) {
                    Text("SỬA", fontWeight = FontWeight.SemiBold, color = Color(0xFFFF9800)) // Nhấn mạnh màu Sửa
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onDelete) {
                    Text("XÓA", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold) // Nhấn mạnh màu Xóa
                }
            }
        }
    }
}