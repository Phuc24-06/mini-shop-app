package com.example.shopper.ui.theme.page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import com.example.shopper.models.Product
import com.example.shopper.ui.theme.viewmodel.ProductViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.shopper.ui.theme.componnents.ProductItem
import androidx.compose.ui.Alignment
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.shopper.ui.theme.viewmodel.CategoryViewModel
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    onBack: () -> Unit,
    onAddProduct: () -> Unit, // Tham số này không được dùng trong Composable hiện tại
    onEditProduct: (productId: String) -> Unit, // Tham số này không được dùng
    onDeleteProduct: (productId: String) -> Unit // Tham số này không được dùng
) {
    val productViewModel: ProductViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val products by productViewModel.products.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var editingProductId by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    // Load dữ liệu ban đầu
    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
        categoryViewModel.loadCategories()
    }

    // Hàm reset form
    fun resetForm() {
        name = ""; description = ""; categoryId = ""; price = ""; stock = ""; imageUrl = ""; isAdding = false; editingProductId = null; errorMsg = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(if (editingProductId == null) "Thêm sản phẩm mới" else "Cập nhật sản phẩm", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // 1. Tên sản phẩm
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên sản phẩm") }, modifier = Modifier.fillMaxWidth())

            // 2. Mô tả
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả") }, modifier = Modifier.fillMaxWidth())

            // 3. Danh mục (Dropdown thay cho TextField bị lặp)
            var expanded by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = categories.find { it.id == categoryId }?.name ?: "Chọn danh mục",
                onValueChange = {},
                label = { Text("Danh mục") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Chọn danh mục")
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            categoryId = cat.id
                            expanded = false
                        }
                    )
                }
            }

            // 4. Giá (Đã xóa OutlinedTextField bị lặp)
            OutlinedTextField(
                value = price,
                onValueChange = { value ->
                    if (value.all { it.isDigit() || it == '.' } || value.isEmpty()) {
                        price = value
                    }
                },
                label = { Text("Giá") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 5. Tồn kho (Đã xóa OutlinedTextField bị lặp)
            OutlinedTextField(
                value = stock,
                onValueChange = { value ->
                    if (value.all { it.isDigit() } || value.isEmpty()) {
                        stock = value
                    }
                },
                label = { Text("Tồn kho") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = stock.isNotEmpty() && stock.toIntOrNull() == null
            )

            // 6. URL ảnh
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL ảnh") }, modifier = Modifier.fillMaxWidth())

            if (errorMsg.isNotEmpty()) Text(errorMsg, color = MaterialTheme.colorScheme.error)

            // Khối Button Thêm/Cập nhật
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Button(
                    onClick = {
                        isAdding = true
                        errorMsg = ""
                        val priceVal = price.toDoubleOrNull()
                        val stockVal = stock.toIntOrNull()

                        // Kiểm tra đầu vào
                        if (name.isBlank() || categoryId.isBlank() || priceVal == null || stockVal == null || priceVal <= 0) {
                            errorMsg = "Vui lòng nhập đầy đủ Tên, Danh mục, Giá (>0) và Tồn kho (là số)!";
                            isAdding = false
                            return@Button
                        }

                        val product = Product(
                            id = editingProductId ?: "",
                            name = name,
                            description = description,
                            categoryId = categoryId,
                            price = priceVal,
                            stock = stockVal,
                            imageUrl = imageUrl
                        )

                        // Gọi API
                        if (editingProductId == null) {
                            productViewModel.addProduct(product, { resetForm() }, {
                                errorMsg = it.message ?: "Lỗi khi thêm sản phẩm"; isAdding = false
                            })
                        } else {
                            productViewModel.updateProduct(product, { resetForm() }, {
                                errorMsg = it.message ?: "Lỗi khi cập nhật sản phẩm"; isAdding = false
                            })
                        }
                    },
                    enabled = !isAdding,
                ) {
                    Icon(if (editingProductId == null) Icons.Default.Add else Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (editingProductId == null) "Thêm sản phẩm" else "Cập nhật sản phẩm")
                }

                // Nút Hủy sửa
                if (editingProductId != null) {
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { resetForm() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) { Text("Hủy sửa") }
                }
            }

            Divider(Modifier.padding(vertical = 12.dp))
            Text("Danh sách sản phẩm", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // Danh sách sản phẩm
            LazyColumn(modifier = Modifier.heightIn(min = 200.dp, max = 600.dp)) {
                items(products) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                            // CHỈ GIỮ LẠI MỘT ProductItem
                            ProductItem(product = product, modifier = Modifier.weight(1f))

                            // Nút Sửa
                            IconButton(onClick = {
                                editingProductId = product.id
                                name = product.name
                                description = product.description
                                categoryId = product.categoryId
                                price = product.price.toString()
                                stock = product.stock.toString()
                                imageUrl = product.imageUrl
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Sửa")
                            }

                            // Nút Xóa
                            IconButton(onClick = {
                                productViewModel.deleteProduct(product.id, {
                                    if (editingProductId == product.id) {
                                        resetForm()
                                    }
                                }, { errorMsg = it.message ?: "Lỗi xóa" })
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa")
                            }
                        }
                    }
                }
            }
        }
    }
}