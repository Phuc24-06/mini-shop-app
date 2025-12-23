package com.example.shopper.services

import android.util.Log
import com.example.shopper.models.ProductRating
import com.example.shopper.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object ReviewService {
    private val db = FirebaseFirestore.getInstance()
    private const val REVIEWS_COLLECTION = "reviews"
    private const val RATINGS_COLLECTION = "product_ratings"

    /**
     * Thêm review mới
     */
    suspend fun addReview(review: Review, onResult: (Boolean, String) -> Unit) {
        try {
            // Kiểm tra user đã review chưa
            val existingReview = db.collection(REVIEWS_COLLECTION)
                .whereEqualTo("productId", review.productId)
                .whereEqualTo("userId", review.userId)
                .get()
                .await()

            if (!existingReview.isEmpty) {
                onResult(false, "Bạn đã đánh giá sản phẩm này rồi!")
                return
            }

            // Thêm review mới
            val newReviewRef = db.collection(REVIEWS_COLLECTION).document()
            val reviewWithId = review.copy(id = newReviewRef.id)

            newReviewRef.set(reviewWithId).await()

            // Cập nhật rating thống kê
            updateProductRating(review.productId)

            onResult(true, "Đánh giá thành công!")
            Log.d("ReviewService", "Review added: ${reviewWithId.id}")
        } catch (e: Exception) {
            onResult(false, "Lỗi: ${e.message}")
            Log.e("ReviewService", "Error adding review", e)
        }
    }

    /**
     * Lấy tất cả review của sản phẩm
     */
    fun getProductReviews(productId: String, onResult: (List<Review>) -> Unit) {
        db.collection(REVIEWS_COLLECTION)
            .whereEqualTo("productId", productId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ReviewService", "Error getting reviews", error)
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val reviews = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Review::class.java)
                } ?: emptyList()

                Log.d("ReviewService", "Loaded ${reviews.size} reviews for product: $productId")
                onResult(reviews)
            }
    }

    /**
     * Lấy review của user cho sản phẩm cụ thể
     */
    suspend fun getUserReviewForProduct(productId: String, userId: String): Review? {
        return try {
            val snapshot = db.collection(REVIEWS_COLLECTION)
                .whereEqualTo("productId", productId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toObject(Review::class.java)
        } catch (e: Exception) {
            Log.e("ReviewService", "Error getting user review", e)
            null
        }
    }

    /**
     * Cập nhật review
     */
    suspend fun updateReview(reviewId: String, rating: Float, comment: String, onResult: (Boolean, String) -> Unit) {
        try {
            db.collection(REVIEWS_COLLECTION).document(reviewId)
                .update(
                    mapOf(
                        "rating" to rating,
                        "comment" to comment,
                        "timestamp" to System.currentTimeMillis()
                    )
                ).await()

            // Lấy productId để cập nhật rating
            val review = db.collection(REVIEWS_COLLECTION).document(reviewId).get().await()
            val productId = review.getString("productId") ?: ""
            if (productId.isNotEmpty()) {
                updateProductRating(productId)
            }

            onResult(true, "Cập nhật đánh giá thành công!")
        } catch (e: Exception) {
            onResult(false, "Lỗi: ${e.message}")
            Log.e("ReviewService", "Error updating review", e)
        }
    }

    /**
     * Xóa review
     */
    suspend fun deleteReview(reviewId: String, productId: String, onResult: (Boolean, String) -> Unit) {
        try {
            db.collection(REVIEWS_COLLECTION).document(reviewId).delete().await()
            updateProductRating(productId)
            onResult(true, "Xóa đánh giá thành công!")
        } catch (e: Exception) {
            onResult(false, "Lỗi: ${e.message}")
            Log.e("ReviewService", "Error deleting review", e)
        }
    }

    /**
     * Cập nhật thống kê rating của sản phẩm
     */
    private suspend fun updateProductRating(productId: String) {
        try {
            val reviews = db.collection(REVIEWS_COLLECTION)
                .whereEqualTo("productId", productId)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Review::class.java) }

            if (reviews.isEmpty()) {
                // Xóa rating nếu không còn review
                db.collection(RATINGS_COLLECTION).document(productId).delete().await()
                return
            }

            val totalReviews = reviews.size
            val averageRating = reviews.map { it.rating }.average().toFloat()

            val ratingCount = reviews.groupBy { it.rating.toInt() }
            val productRating = ProductRating(
                productId = productId,
                averageRating = averageRating,
                totalReviews = totalReviews,
                fiveStars = ratingCount[5]?.size ?: 0,
                fourStars = ratingCount[4]?.size ?: 0,
                threeStars = ratingCount[3]?.size ?: 0,
                twoStars = ratingCount[2]?.size ?: 0,
                oneStar = ratingCount[1]?.size ?: 0
            )

            db.collection(RATINGS_COLLECTION).document(productId)
                .set(productRating)
                .await()

            Log.d("ReviewService", "Updated rating for product $productId: ${productRating.averageRating} (${productRating.totalReviews} reviews)")
        } catch (e: Exception) {
            Log.e("ReviewService", "Error updating product rating", e)
        }
    }

    /**
     * Lấy thống kê rating của sản phẩm
     */
    fun getProductRating(productId: String, onResult: (ProductRating?) -> Unit) {
        db.collection(RATINGS_COLLECTION).document(productId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ReviewService", "Error getting product rating", error)
                    onResult(null)
                    return@addSnapshotListener
                }

                val rating = snapshot?.toObject(ProductRating::class.java)
                onResult(rating)
            }
    }

    /**
     * Kiểm tra user đã mua sản phẩm chưa (để hiển thị "Đã mua hàng")
     */
    suspend fun hasUserPurchasedProduct(userId: String, productId: String): Boolean {
        return try {
            val orders = db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            orders.documents.any { orderDoc ->
                val items = orderDoc.get("items") as? List<*>
                items?.any { item ->
                    (item as? Map<*, *>)?.get("productId") == productId
                } ?: false
            }
        } catch (e: Exception) {
            Log.e("ReviewService", "Error checking purchase", e)
            false
        }
    }
}

