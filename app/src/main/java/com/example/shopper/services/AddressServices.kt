package com.example.shopper.services

import com.example.shopper.models.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AddressServices {

    private val db = FirebaseFirestore.getInstance()

    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("User not logged in")
    }

    private fun addressRef() =
        db.collection("users")
            .document(getUserId())
            .collection("addresses")

    suspend fun getAddresses(): List<Address> {
        return try {
            val snapshot = addressRef().get().await()
            snapshot.documents.mapNotNull { it.toObject(Address::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addAddress(address: Address): Boolean {
        return try {
            val uid = getUserId()
            val doc = addressRef().document()

            val data = address.copy(
                id = doc.id,
                userId = uid
            )

            doc.set(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateAddress(address: Address): Boolean {
        return try {
            addressRef().document(address.id).set(address).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteAddress(id: String): Boolean {
        return try {
            addressRef().document(id).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun setDefaultAddress(id: String): Boolean {
        return try {
            val all = addressRef().get().await()
            all.documents.forEach {
                it.reference.update("isDefault", it.id == id).await()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
