package com.example.shopper.ui.theme.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopper.services.AuthServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()

    data class LoginSuccess(val role: String, val userId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "") : AuthState()
    data class RegisterSuccess(val message: String) : AuthState()
    data class PasswordActionSuccess(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
    data class PasswordResetSuccess(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    val isLoggedIn = MutableStateFlow(AuthServices.getCurrentUser() != null)

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            isLoggedIn.value = user != null

            if (user != null) {
                fetchUserRole()
            } else {
                _userRole.value = null
                _authState.value = AuthState.Idle
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    private fun fetchUserRole() {
        AuthServices.getUserRole { role ->
            _userRole.value = role ?: "customer"
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        AuthServices.logIn(email, password) { success, message, role ->
            if (success) {
                val realRole = role ?: "customer"
                _userRole.value = realRole
                isLoggedIn.value = true

                _authState.value = AuthState.LoginSuccess(realRole)
            } else {
                _authState.value = AuthState.Error(message)
            }
        }
    }

    // HÀM BỊ LỖI LOGIC ĐÃ ĐƯỢC XÓA BỎ:
    // fun updatePassword(phone: String, newPassword: String) { ... }


    fun register(email: String, password: String, name: String) {
        _authState.value = AuthState.Loading

        AuthServices.signUp(email, password, name) { success, message ->
            if (success) {
                isLoggedIn.value = true
                _userRole.value = "customer"

                _authState.value = AuthState.RegisterSuccess(message)
            } else {
                _authState.value = AuthState.Error(message)
            }
        }
    }

    fun logout() {
        AuthServices.logout()
        isLoggedIn.value = false
        _userRole.value = null
        _authState.value = AuthState.Idle
    }

    fun resetPassword(email: String) {
        _authState.value = AuthState.Loading

        AuthServices.resetPassword(email) { success, message ->
            if (success) {
                _authState.value = AuthState.PasswordActionSuccess(message)
            } else {
                _authState.value = AuthState.Error(message)
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        _authState.value = AuthState.Loading

        AuthServices.updateUserProfile(name, phone) { success, message ->
            if (success) {
                _authState.value = AuthState.PasswordActionSuccess(message)
            } else {
                _authState.value = AuthState.Error(message)
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        _authState.value = AuthState.Loading

        AuthServices.changePassword(currentPassword, newPassword) { success, message ->
            if (success) {
                _authState.value = AuthState.PasswordActionSuccess(message)
            } else {
                _authState.value = AuthState.Error(message)
            }
        }
    }

    /**
     * Hàm này được gọi từ ForgotPasswordScreen.
     * Mặc dù ForgotPasswordScreen.kt yêu cầu 3 trường (email, phone, newPassword),
     * nhưng chúng ta chỉ sử dụng email để gửi email reset, đây là phương pháp an toàn của Firebase.
     */
    fun updatePasswordWithEmailAndPhone(email: String, phone: String, newPassword: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            // Gọi hàm đã sửa trong AuthServices (chỉ thực hiện gửi email reset)
            AuthServices.updatePasswordViaResetEmail(email) { success, message ->
                if (success) {
                    _authState.value = AuthState.PasswordResetSuccess(message)
                } else {
                    _authState.value = AuthState.Error(message)
                }
            }
        }
    }
}