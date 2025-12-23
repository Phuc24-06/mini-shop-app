package com.example.shopper.services

import com.example.shopper.models.CategoryItem
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
// Import này không cần thiết nếu bạn chỉ dùng Firestore, có thể xóa:
// import com.google.firebase.database.DataSnapshot
// import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.toObjects // Giữ lại nếu bạn sử dụng nó

object CategoryService {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("categories")

    /** * Lấy tất cả danh mục từ Firestore.
     * Sử dụng phương thức mapNotNull và toObjects để dễ dàng ánh xạ dữ liệu.
     */
    fun getAllCategories(callback: (List<CategoryItem>) -> Unit) {
        Log.d("CategoryService", "🔍 Bắt đầu lấy dữ liệu từ node 'categories'...")

        collection.get()
            .addOnSuccessListener { result ->
                // Ánh xạ dữ liệu và gán doc.id vào CategoryItem
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(CategoryItem::class.java)?.copy(id = doc.id)
                }

                Log.d("CategoryService", "✅ Lấy được ${list.size} categories từ Firestore")
                Log.d("CategoryService", "📋 Categories: ${list.map { it.id to it.name }}")

                callback(list)
            }
            .addOnFailureListener { e ->
                Log.e("CategoryService", "❌ Lỗi Firestore: ${e.message}", e)
                callback(emptyList())
            }
    }

    /** Thêm danh mục mới (DÙNG ID truyền vào) */
    fun addCategory(category: CategoryItem, callback: (Boolean, String) -> Unit) {
        val data = mapOf(
            "name" to category.name,
            "imageUrl" to category.imageUrl
        )

        collection.document(category.id)
            .set(data)
            .addOnSuccessListener { callback(true, "Thêm danh mục thành công") }
            .addOnFailureListener { e -> callback(false, e.message ?: "Lỗi thêm danh mục") }
    }

    /** Cập nhật danh mục */
    fun updateCategory(category: CategoryItem, callback: (Boolean, String) -> Unit) {
        val data = mapOf(
            "name" to category.name,
            "imageUrl" to category.imageUrl
        )

        collection.document(category.id)
            .set(data)
            .addOnSuccessListener { callback(true, "Cập nhật danh mục thành công") }
            .addOnFailureListener { e -> callback(false, e.message ?: "Lỗi cập nhật") }
    }

    /** Xóa danh mục */
    fun deleteCategory(categoryId: String, callback: (Boolean, String) -> Unit) {
        collection.document(categoryId)
            .delete()
            .addOnSuccessListener { callback(true, "Xóa danh mục thành công") }
            .addOnFailureListener { e -> callback(false, e.message ?: "Lỗi xóa danh mục") }
    }
} // <--- Đảm bảo dấu ngoặc nhọn đóng ở đây