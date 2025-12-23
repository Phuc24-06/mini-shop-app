package com.example.shopper.ui.theme.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // <--- Import Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shopper.models.Address
import com.example.shopper.ui.theme.viewmodel.AddressViewModel
import kotlinx.coroutines.launch

@Composable
fun AddressEditScreen(
    viewModel: AddressViewModel = viewModel(),
    addressToEdit: Address? = null,
    onBack: () -> Unit
) {
    // Khởi tạo trạng thái dựa trên addressToEdit
    var receiverName by remember { mutableStateOf(addressToEdit?.receiverName ?: "") }
    var phoneNumber by remember { mutableStateOf(addressToEdit?.phoneNumber ?: "") }
    var streetAddress by remember { mutableStateOf(addressToEdit?.streetAddress ?: "") }
    var city by remember { mutableStateOf(addressToEdit?.city ?: "") }
    var district by remember { mutableStateOf(addressToEdit?.district ?: "") }
    var ward by remember { mutableStateOf(addressToEdit?.ward ?: "") }
    var isDefault by remember { mutableStateOf(addressToEdit?.isDefault ?: false) }

    val isEditing = addressToEdit != null
    val title = if (isEditing) "Chỉnh sửa địa chỉ" else "Thêm địa chỉ mới"

    val status by viewModel.operationStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // LOGIC SỬA LỖI: Chờ kết quả thành công trước khi quay lại
    LaunchedEffect(status) {
        status?.let {
            if (it.contains("thành công")) {
                scope.launch {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearStatus()
                    onBack() // CHỈ QUAY LẠI KHI THÀNH CÔNG
                }
            } else if (it.contains("Lỗi")) {
                scope.launch {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearStatus()
                }
            }
        }
    }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    // SỬ DỤNG MÀU CHỦ ĐẠO CHO ICON
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFFFF9800))
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Tên người nhận
            OutlinedTextField(
                value = receiverName,
                onValueChange = { receiverName = it },
                label = { Text("Tên người nhận") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                // Áp dụng màu primary (cam) cho các trường khi focus
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Số điện thoại
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Số điện thoại") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                // Áp dụng màu primary (cam) cho các trường khi focus
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Địa chỉ cụ thể (Số nhà, đường)
            OutlinedTextField(
                value = streetAddress,
                onValueChange = { streetAddress = it },
                label = { Text("Số nhà, Tên đường") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                // Áp dụng màu primary (cam) cho các trường khi focus
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Tỉnh/Thành phố
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Tỉnh/Thành phố") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                // Áp dụng màu primary (cam) cho các trường khi focus
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Quận/Huyện
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("Quận/Huyện") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                // Áp dụng màu primary (cam) cho các trường khi focus
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Phường/Xã
            OutlinedTextField(
                value = ward,
                onValueChange = { ward = it },
                label = { Text("Phường/Xã") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                // Áp dụng màu primary (cam) cho các trường khi focus
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // Checkbox Mặc định
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    // SỬ DỤNG MÀU CAM CHO CHECKBOX KHI ĐƯỢC CHỌN
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF9800))
                )
                Text("Đặt làm địa chỉ mặc định", modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Nút Lưu
            Button(
                onClick = {
                    if (receiverName.isBlank() || phoneNumber.isBlank() || streetAddress.isBlank() || city.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Vui lòng điền đầy đủ các trường bắt buộc.") }
                        return@Button
                    }
                    val newAddress = Address(
                        id = addressToEdit?.id ?: "",
                        receiverName = receiverName,
                        phoneNumber = phoneNumber,
                        // FIX CUỐI CÙNG: Sử dụng tên trường đồng nhất
                        streetAddress = streetAddress,
                        city = city,
                        district = district,
                        ward = ward,
                        isDefault = isDefault
                    )
                    if (isEditing) {
                        viewModel.updateAddress(newAddress)
                    } else {
                        viewModel.addAddress(newAddress)
                    }
                },
                // SỬ DỤNG MÀU CAM CHO NÚT BUTTON
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Lưu thay đổi" else "Thêm địa chỉ")
            }
        }
    }
}