package com.example.shopper.services

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.auth.EmailAuthProvider

object AuthServices {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Đăng ký với Email + Password + Tên người dùng
     * (Không bắt buộc) Tên người dùng được lưu vào Firebase Auth DisplayName.
     * Thêm user với role='user' và phone='' vào Firestore.
     */
    fun signUp(email: String, password: String, name: String = "", onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // 1. Update display name
                        val profileUpdate = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user.updateProfile(profileUpdate)
                            .addOnCompleteListener { profileTask ->
                                // 2. Lưu thông tin ban đầu vào Firestore
                                val userData = mapOf(
                                    "email" to email,
                                    "role" to "user", // Vai trò mặc định
                                    "phone" to "" // Số điện thoại ban đầu trống
                                )
                                db.collection("users").document(user.uid).set(userData)
                                    .addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            onResult(true, "Đăng ký thành công")
                                        } else {
                                            // Xử lý lỗi Firestore (vẫn coi là thành công vì Auth đã xong)
                                            onResult(true, "Đăng ký thành công (Lưu dữ liệu Firestore thất bại: ${dbTask.exception?.message})")
                                        }
                                    }
                            }
                    } else {
                        onResult(true, "Đăng ký thành công nhưng không lấy được User")
                    }
                } else {
                    onResult(false, task.exception?.message ?: "Lỗi đăng ký")
                }
            }
    }

    /**
     * Đăng nhập bình thường
     * Trả về thêm role (String?) qua callback
     */
    fun logIn(email: String, password: String, onResult: (Boolean, String, String?) -> Unit) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        db.collection("users").document(user.uid).get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role")
                                val isLocked = document.getBoolean("isLocked") ?: false
                                if (isLocked) {
                                    auth.signOut()
                                    onResult(false, "Tài khoản đã bị khóa", null)
                                } else {
                                    onResult(true, "Đăng nhập thành công", role)
                                }
                            }
                            .addOnFailureListener {
                                // Lỗi kiểm tra Firestore, nhưng Auth vẫn thành công
                                onResult(true, "Đăng nhập thành công", null)
                            }
                    } else {
                        onResult(false, "Người dùng không tồn tại", null)
                    }
                } else {
                    // Lỗi từ Firebase Auth
                    onResult(false, task.exception?.message ?: "Lỗi đăng nhập", null)
                }
            }
    }

    /**
     * Lấy người dùng hiện tại
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Lấy số điện thoại của user hiện tại từ Firestore (null nếu không tồn tại)
     */
    fun getUserPhone(onResult: (String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(null)
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val phone = if (doc.exists()) doc.getString("phone") else null
                onResult(phone)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Cập nhật thông tin profile (name trong Auth và phone trong Firestore)
     */
    fun updateUserProfile(name: String, phone: String, onResult: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "Người dùng chưa đăng nhập")
            return
        }

        // Update display name in Firebase Auth
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update phone number in Firestore
                    db.collection("users").document(user.uid)
                        .set(mapOf("phone" to phone), SetOptions.merge())
                        .addOnCompleteListener { phoneUpdateTask ->
                            if (phoneUpdateTask.isSuccessful) {
                                onResult(true, "Cập nhật thông tin thành công")
                            } else {
                                onResult(false, phoneUpdateTask.exception?.message ?: "Lỗi cập nhật số điện thoại")
                            }
                        }
                } else {
                    onResult(false, task.exception?.message ?: "Lỗi cập nhật")
                }
            }
    }

    /**
     * Gửi email đặt lại mật khẩu
     * (Đây là hàm được sử dụng cho chức năng Quên mật khẩu/Reset Password)
     */
    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Email đặt lại mật khẩu đã được gửi! Vui lòng kiểm tra hộp thư.")
                } else {
                    onResult(false, task.exception?.message ?: "Lỗi gửi email reset")
                }
            }
    }

    /**
     * Hàm dùng cho màn hình Quên Mật Khẩu (ForgotPasswordScreen).
     * Mặc dù nhận phone/newPassword, nhưng Firebase Auth chỉ cho phép reset qua email.
     * -> Sẽ gọi thẳng resetPassword.
     */
    fun updatePasswordViaResetEmail(email: String, onResult: (Boolean, String) -> Unit) {
        // Bỏ qua phone và newPassword, chỉ dùng Email để gửi email reset
        resetPassword(email) { success, message ->
            if (success) {
                onResult(true, "Đã gửi email đặt lại mật khẩu đến $email. Vui lòng kiểm tra hộp thư.")
            } else {
                onResult(false, message)
            }
        }
    }

    /**
     * Đăng xuất
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Đổi mật khẩu của người dùng hiện tại (yêu cầu mật khẩu cũ để Re-authenticate)
     */
    fun changePassword(currentPassword: String, newPassword: String, onResult: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "Bạn phải đăng nhập để đổi mật khẩu.")
            return
        }

        val email = user.email
        if (email == null) {
            onResult(false, "Không thể xác thực lại. Tài khoản không có email.")
            return
        }

        // Bước 1: Tạo thông tin xác thực (Credential) từ mật khẩu cũ
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        // Bước 2: Xác thực lại người dùng
        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // Bước 3: Nếu xác thực lại thành công, tiến hành cập nhật mật khẩu mới
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onResult(true, "Mật khẩu đã được cập nhật thành công.")
                            } else {
                                onResult(
                                    false,
                                    updateTask.exception?.message ?: "Lỗi cập nhật mật khẩu."
                                )
                            }
                        }
                } else {
                    // Lỗi xác thực lại (ví dụ: Mật khẩu cũ sai)
                    onResult(
                        false,
                        reauthTask.exception?.message
                            ?: "Xác thực lại thất bại. Vui lòng kiểm tra mật khẩu hiện tại."
                    )
                }
            }
    }

    /**
     * Lấy role của user hiện tại từ Firestore
     * Trả về qua callback: role (String?) hoặc null nếu không có
     */
    fun getUserRole(onResult: (String?) -> Unit) {
        val user = getCurrentUser()
        if (user == null) {
            onResult(null)
            return
        }
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                onResult(role)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Upload avatar bằng cách chuyển ảnh thành Base64 và lưu vào Firestore
     */
    fun uploadAvatar(imageUri: Uri, context: android.content.Context, onResult: (Boolean, String, String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "Người dùng chưa đăng nhập", null)
            return
        }

        try {
            // Đọc ảnh từ URI và chuyển thành Base64
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes == null) {
                onResult(false, "Không thể đọc ảnh", null)
                return
            }

            // Nén ảnh để tránh quá lớn (optional, nhưng nên làm)
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val resizedBitmap = resizeBitmap(bitmap, 512) // Resize về 512px
            val compressedBytes = java.io.ByteArrayOutputStream()
            resizedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, compressedBytes)

            val base64String = android.util.Base64.encodeToString(
                compressedBytes.toByteArray(),
                android.util.Base64.DEFAULT
            )

            // Lưu Base64 vào Firestore
            db.collection("users").document(user.uid)
                .set(mapOf("avatarBase64" to base64String), SetOptions.merge())
                .addOnSuccessListener {
                    onResult(true, "Cập nhật ảnh đại diện thành công", base64String)
                }
                .addOnFailureListener { e ->
                    onResult(false, e.message ?: "Lỗi lưu ảnh vào Firestore", null)
                }

        } catch (e: Exception) {
            onResult(false, e.message ?: "Lỗi xử lý ảnh", null)
        }
    }

    /**
     * Resize bitmap để giảm kích thước
     */
    private fun resizeBitmap(bitmap: android.graphics.Bitmap, maxSize: Int): android.graphics.Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val ratio = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }

        return android.graphics.Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Lấy avatar Base64 từ Firestore
     */
    fun getAvatarBase64(onResult: (String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(null)
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val avatarBase64 = if (doc.exists()) doc.getString("avatarBase64") else null
                onResult(avatarBase64)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}