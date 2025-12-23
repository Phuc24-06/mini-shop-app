package com.example.shopper.services

import com.example.shopper.models.Product
import com.google.firebase.firestore.FirebaseFirestore

object ProductService {
    private val db = FirebaseFirestore.getInstance()

    fun getAll(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getByCategory(categoryId: String, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getFeatured(limit: Int, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products")
            .limit(limit.toLong())
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun search(query: String, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        // Simple client-side search: load all then filter
        getAll({ list ->
            val filtered = list.filter { p ->
                p.name.contains(query, ignoreCase = true) ||
                        (p.description?.contains(query, ignoreCase = true) ?: false)
            }
            onSuccess(filtered)
        }, { e -> onFailure(e) })
    }

    fun addProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products")
            .add(product)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products")
            .document(product.id)
            .set(product)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun deleteProduct(productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products")
            .document(productId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

}


