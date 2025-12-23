package com.example.shopper.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopper.models.CategoryItem
import com.example.shopper.services.CategoryService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Trạng thái UI cho CRUD Category
sealed class CategoryUiState {
    object Idle : CategoryUiState()
    object Loading : CategoryUiState()
    data class Success(val message: String) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

class CategoryViewModel : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories.asStateFlow()

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Idle)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    // Khởi tạo và tải danh mục ban đầu
    init {
        // Sử dụng viewModelScope.launch để gọi hàm loadCategories()
        // nếu hàm CategoryService.getAllCategories() được thay đổi thành suspend
        // Hiện tại CategoryService.getAllCategories() sử dụng callback, nên không cần launch nếu không muốn block.
        loadCategories()
    }

    /** [READ] Tải danh sách tất cả danh mục */
    fun loadCategories() {
        _uiState.value = CategoryUiState.Loading

        // Sử dụng hàm getAllCategories đã được sửa lỗi trong CategoryService
        CategoryService.getAllCategories { list ->
            _categories.value = list
            _uiState.value = CategoryUiState.Idle
        }
    }

    /** [CREATE] Thêm danh mục mới */
    fun addCategory(category: CategoryItem) {
        _uiState.value = CategoryUiState.Loading
        CategoryService.addCategory(category) { success, message ->
            if (success) {
                _uiState.value = CategoryUiState.Success(message)
                loadCategories()
            } else {
                _uiState.value = CategoryUiState.Error(message)
            }
        }
    }

    /** [UPDATE] Cập nhật danh mục */
    fun updateCategory(category: CategoryItem) {
        _uiState.value = CategoryUiState.Loading
        CategoryService.updateCategory(category) { success, message ->
            if (success) {
                _uiState.value = CategoryUiState.Success(message)
                loadCategories()
            } else {
                _uiState.value = CategoryUiState.Error(message)
            }
        }
    }

    /** [DELETE] Xóa danh mục */
    fun deleteCategory(categoryId: String) {
        _uiState.value = CategoryUiState.Loading
        CategoryService.deleteCategory(categoryId) { success, message ->
            if (success) {
                _uiState.value = CategoryUiState.Success(message)
                loadCategories()
            } else {
                _uiState.value = CategoryUiState.Error(message)
            }
        }
    }

    /** Reset trạng thái UI sau khi hiển thị thông báo */
    fun resetUiState() {
        _uiState.value = CategoryUiState.Idle
    }
}