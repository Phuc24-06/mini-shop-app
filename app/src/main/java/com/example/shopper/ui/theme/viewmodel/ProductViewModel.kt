package com.example.shopper.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shopper.models.Product
import com.example.shopper.services.ProductService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun loadProducts(){
        _isLoading.value = true
        ProductService.getAll({list ->
            _products.value = list
            _isLoading.value = false
        },{e ->
            _errorMessage.value = "Loi khi tai san pham: ${e.message}"
            _isLoading.value = false
        })
    }

    fun loadFeaturedProducts(limit: Int) {
        _isLoading.value = true
        ProductService.getFeatured(limit, { list ->
            _products.value = list
            _isLoading.value = false
        }, { e ->
            _errorMessage.value = "Loi khi tai san pham noi bat: ${e.message}"
            _isLoading.value = false
        })
    }

    fun loadProductsByCategory(categoryId: String) {
        _isLoading.value = true
        ProductService.getByCategory(categoryId, { list ->
            _products.value = list
            _isLoading.value = false
        }, { e ->
            _errorMessage.value = "Loi khi tai san pham: ${e.message}"
            _isLoading.value = false
        })

    }

    fun searchProducts(query: String) {
        _isSearching.value = true
        ProductService.search(query, { list ->
            _searchResults.value = list
            _isSearching.value = false
        }, { e ->
            _errorMessage.value = "Loi khi tim kiem san pham: ${e.message}"
            _isSearching.value = false
        })
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun addProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        ProductService.addProduct(product, {
            loadProducts()
            onSuccess()
        }, onFailure)
    }

    fun updateProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Gọi ProductService để cập nhật sản phẩm với giá trị mới
        ProductService.updateProduct(product, {
            // Sau khi cập nhật thành công, load lại danh sách sản phẩm để UI hiển thị giá trị mới
            loadProducts()
            onSuccess()
        }, onFailure)
    } // <--- Đã đóng hàm updateProduct

    fun deleteProduct(productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        ProductService.deleteProduct(productId, {
            loadProducts()
            onSuccess()
        }, onFailure)
    }
} // <--- Đã đóng class ProductViewModel