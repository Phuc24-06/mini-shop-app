package com.example.shopper.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopper.models.ProductRating
import com.example.shopper.models.Review
import com.example.shopper.services.AuthServices
import com.example.shopper.services.ReviewService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _productRating = MutableStateFlow<ProductRating?>(null)
    val productRating: StateFlow<ProductRating?> = _productRating

    private val _userReview = MutableStateFlow<Review?>(null)
    val userReview: StateFlow<Review?> = _userReview

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Load reviews cho sản phẩm
     */
    fun loadProductReviews(productId: String) {
        ReviewService.getProductReviews(productId) { reviewList ->
            _reviews.value = reviewList
        }
    }

    /**
     * Load rating thống kê
     */
    fun loadProductRating(productId: String) {
        ReviewService.getProductRating(productId) { rating ->
            _productRating.value = rating
        }
    }

    /**
     * Load review của user hiện tại
     */
    fun loadUserReview(productId: String) {
        viewModelScope.launch {
            val userId = AuthServices.getCurrentUser()?.uid ?: return@launch
            val review = ReviewService.getUserReviewForProduct(productId, userId)
            _userReview.value = review
        }
    }

    /**
     * Thêm review mới
     */
    fun addReview(
        productId: String,
        rating: Float,
        comment: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val user = AuthServices.getCurrentUser()
            if (user == null) {
                onResult(false, "Vui lòng đăng nhập để đánh giá")
                _isLoading.value = false
                return@launch
            }

            // Kiểm tra đã mua hàng chưa
            val hasPurchased = ReviewService.hasUserPurchasedProduct(user.uid, productId)

            val review = Review(
                productId = productId,
                userId = user.uid,
                userName = user.displayName ?: "Người dùng",
                rating = rating,
                comment = comment,
                isVerifiedPurchase = hasPurchased
            )

            ReviewService.addReview(review) { success, message ->
                _isLoading.value = false
                onResult(success, message)

                if (success) {
                    // Reload reviews
                    loadUserReview(productId)
                }
            }
        }
    }

    /**
     * Cập nhật review
     */
    fun updateReview(
        reviewId: String,
        rating: Float,
        comment: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            ReviewService.updateReview(reviewId, rating, comment) { success, message ->
                _isLoading.value = false
                onResult(success, message)
            }
        }
    }

    /**
     * Xóa review
     */
    fun deleteReview(reviewId: String, productId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            ReviewService.deleteReview(reviewId, productId) { success, message ->
                _isLoading.value = false
                onResult(success, message)

                if (success) {
                    _userReview.value = null
                }
            }
        }
    }

    /**
     * Reset state
     */
    fun clear() {
        _reviews.value = emptyList()
        _productRating.value = null
        _userReview.value = null
    }
}

