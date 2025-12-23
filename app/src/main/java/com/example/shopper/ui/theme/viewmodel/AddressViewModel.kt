package com.example.shopper.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopper.models.Address
import com.example.shopper.services.AddressServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _operationStatus = MutableStateFlow<String?>(null)
    val operationStatus: StateFlow<String?> = _operationStatus

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        _isLoading.value = true
        viewModelScope.launch {
            _addresses.value = AddressServices.getAddresses()
            _isLoading.value = false
        }
    }

    fun addAddress(address: Address) {
        _isLoading.value = true
        viewModelScope.launch {
            val success = AddressServices.addAddress(address)
            if (success) {
                _operationStatus.value = "Thêm địa chỉ thành công!"
                // 🔥 ĐẢM BẢO TẢI LẠI
                loadAddresses()
            } else {
                _operationStatus.value = "Lỗi khi thêm địa chỉ."
            }
            _isLoading.value = false
        }
    }

    fun updateAddress(address: Address) {
        _isLoading.value = true
        viewModelScope.launch {
            val success = AddressServices.updateAddress(address)
            if (success) {
                _operationStatus.value = "Cập nhật địa chỉ thành công!"
                // 🔥 ĐẢM BẢO TẢI LẠI
                loadAddresses()
            } else {
                _operationStatus.value = "Lỗi khi cập nhật địa chỉ."
            }
            _isLoading.value = false
        }
    }

    fun deleteAddress(addressId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val success = AddressServices.deleteAddress(addressId)
            if (success) {
                _operationStatus.value = "Xóa địa chỉ thành công!"
                // 🔥 ĐẢM BẢO TẢI LẠI
                loadAddresses()
            } else {
                _operationStatus.value = "Lỗi khi xóa địa chỉ."
            }
            _isLoading.value = false
        }
    }

    fun setDefaultAddress(addressId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val success = AddressServices.setDefaultAddress(addressId)
            if (success) {
                _operationStatus.value = "Đặt địa chỉ mặc định thành công!"
                // 🔥 ĐẢM BẢO TẢI LẠI
                loadAddresses()
            } else {
                _operationStatus.value = "Lỗi khi đặt địa chỉ mặc định."
            }
            _isLoading.value = false
        }
    }

    fun clearStatus() {
        _operationStatus.value = null
    }
}