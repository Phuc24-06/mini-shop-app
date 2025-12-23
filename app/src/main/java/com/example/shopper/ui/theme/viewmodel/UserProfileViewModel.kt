package com.example.shopper.ui.theme.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopper.models.UserProfile
import com.example.shopper.services.AuthServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel: ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Load user profile from Firebase Auth + Firestore (phone).
     * If no user logged in, sets profile to null.
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = AuthServices.getCurrentUser()
            if (user == null) {
                _userProfile.value = null
                _isLoading.value = false
                return@launch
            }

            // Get phone from Firestore via AuthServices callback
            AuthServices.getUserPhone { phoneFromDb ->
                // Tạo UserProfile mới với dữ liệu hiện tại
                _userProfile.value = UserProfile(
                    name = user.displayName ?: "",
                    email = user.email ?: "",
                    phone = phoneFromDb ?: "",
                    // Giữ lại address nếu nó đã có giá trị, hoặc đặt trống
                    address = _userProfile.value?.address ?: ""
                )
                _isLoading.value = false
            }
        }
    }

    /**
     * Update profile (displayName in Auth + phone in Firestore).
     * onResult returns (success, message).
     */
    fun updateUserProfile(name: String, phone: String, onResult: (Boolean, String) -> Unit) {
        _isLoading.value = true
        AuthServices.updateUserProfile(name, phone) { success, message ->
            if (success) {
                // Update local state to reflect saved values
                val currentEmail = AuthServices.getCurrentUser()?.email ?: _userProfile.value?.email ?: ""
                _userProfile.value = UserProfile(
                    name = name,
                    email = currentEmail,
                    phone = phone,
                    address = _userProfile.value?.address ?: ""
                )
            }
            _isLoading.value = false
            onResult(success, message)
        }
    }

    /**
     * Upload avatar image (Base64 method - không cần Firebase Storage)
     * @param imageUri URI của ảnh được chọn
     * @param context Context để đọc ảnh
     * @param onResult callback trả về (success, message, base64String)
     */
    fun uploadAvatar(imageUri: Uri, context: android.content.Context, onResult: (Boolean, String, String?) -> Unit) {
        _isLoading.value = true
        AuthServices.uploadAvatar(imageUri, context) { success, message, base64String ->
            _isLoading.value = false
            onResult(success, message, base64String)
        }
    }

    /**
     * Lấy avatar Base64 từ Firestore
     */
    fun getAvatarBase64(onResult: (String?) -> Unit) {
        AuthServices.getAvatarBase64(onResult)
    }
}