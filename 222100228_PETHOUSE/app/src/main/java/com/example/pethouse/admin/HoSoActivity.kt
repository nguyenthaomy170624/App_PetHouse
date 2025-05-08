package com.example.pethouse.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.DangNhapActivity
import com.example.pethouse.databinding.ActivityHoSoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HoSoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHoSoBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val TAG = "HoSoActivity"
    private var originalData = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHoSoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFieldsEnabled(false)
        setUserAndAdminData()

        binding.btnEdit.setOnClickListener {
            val isEnable = !binding.name.isEnabled
            setFieldsEnabled(isEnable)
            if (isEnable) binding.name.requestFocus()
        }

        binding.luuthongtin.setOnClickListener {
            if (auth.currentUser == null) {
                Toast.makeText(this, "Phiên đăng nhập đã hết, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
                redirectToLogin()
                return@setOnClickListener
            }

            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val address = binding.address.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val password = binding.password.text.toString()

            if (validateInput(name, email, phone, password)) {
                updateUserAndAdminData(name, email, address, phone, password)
            }
        }

        binding.btnDangxuat.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun setFieldsEnabled(enabled: Boolean) {
        binding.name.isEnabled = enabled
        binding.address.isEnabled = enabled
        binding.email.isEnabled = enabled
        binding.phone.isEnabled = enabled
        binding.password.isEnabled = enabled
    }

    private fun validateInput(name: String, email: String, phone: String, password: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isNotEmpty() && password.length < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updateUserAndAdminData(name: String, email: String, address: String, phone: String, password: String) {
        val userId = auth.currentUser?.uid ?: return
        val adminReference = database.getReference("admin")
        val changes = mutableListOf<String>()

        adminReference.orderByChild("user_id").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val adminSnapshot = snapshot.children.first()
                        val adminKey = adminSnapshot.key
                        if (adminKey == null) {
                            Log.e(TAG, "adminKey is null")
                            return
                        }

                        val adminData = mutableMapOf<String, Any>()

                        if (name != originalData["admin_name"]) {
                            adminData["admin_name"] = name
                            changes.add("Họ tên")
                        }
                        if (email != originalData["admin_email"]) {
                            adminData["admin_email"] = email
                            changes.add("Email")
                        }
                        if (address != originalData["admin_address"] && address.isNotEmpty()) {
                            adminData["admin_address"] = address
                            changes.add("Địa chỉ")
                        }
                        if (phone != originalData["admin_phone"]) {
                            adminData["admin_phone"] = phone
                            changes.add("Số điện thoại")
                        }

                        if (adminData.isNotEmpty()) {
                            adminReference.child(adminKey).updateChildren(adminData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Admin data updated: $adminData")
                                    updateUserData(userId, password, changes)
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to update admin data", e)
                                    Toast.makeText(this@HoSoActivity, "Cập nhật thông tin thất bại", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            updateUserData(userId, password, changes)
                        }
                    } else {
                        Log.w(TAG, "Không tìm thấy thông tin admin tương ứng")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Lỗi database khi cập nhật admin", error.toException())
                }
            })
    }

    private fun updateUserData(userId: String, password: String, changes: MutableList<String>) {
        val user = auth.currentUser ?: return

        if (password.isNotBlank() && password != originalData["user_pass"]) {
            user.updatePassword(password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Mật khẩu được cập nhật trong Firebase Auth")
                    changes.add("Mật khẩu")
                    saveUserToDatabase(userId, password, changes)
                } else {
                    Log.e(TAG, "Không thể cập nhật mật khẩu", task.exception)
                    Toast.makeText(this, "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            saveUserToDatabase(userId, originalData["user_pass"] ?: "", changes)
        }
    }

    private fun saveUserToDatabase(userId: String, password: String, changes: MutableList<String>) {
        val userReference = database.getReference("users").child(userId)

        if (password != originalData["user_pass"]) {
            val userData = mapOf("user_pass" to password)
            userReference.updateChildren(userData)
                .addOnSuccessListener {
                    Log.d(TAG, "Mật khẩu người dùng được lưu vào Database")
                    showUpdateResult(changes)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Lỗi lưu mật khẩu người dùng", e)
                    Toast.makeText(this, "Lưu thông tin thất bại", Toast.LENGTH_LONG).show()
                }
        } else {
            showUpdateResult(changes)
        }
    }

    private fun showUpdateResult(changes: List<String>) {
        val message = if (changes.isEmpty()) {
            "Không có thông tin nào được cập nhật"
        } else {
            "Cập nhật thành công: ${changes.joinToString(", ")}"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        setFieldsEnabled(false)
    }

    private fun setUserAndAdminData() {
        val userId = auth.currentUser?.uid ?: run {
            Log.w(TAG, "Người dùng chưa đăng nhập")
            redirectToLogin()
            return
        }

        val adminReference = database.getReference("admin")
        adminReference.orderByChild("user_id").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val adminSnapshot = snapshot.children.first()
                        val name = adminSnapshot.child("admin_name").getValue(String::class.java) ?: ""
                        val email = adminSnapshot.child("admin_email").getValue(String::class.java) ?: ""
                        val phone = adminSnapshot.child("admin_phone").getValue(String::class.java) ?: ""
                        val address = adminSnapshot.child("admin_address").getValue(String::class.java) ?: ""

                        originalData["admin_name"] = name
                        originalData["admin_email"] = email
                        originalData["admin_phone"] = phone
                        originalData["admin_address"] = address

                        binding.name.setText(name)
                        binding.email.setText(email)
                        binding.phone.setText(phone)
                        binding.address.setText(address)

                        val userReference = database.getReference("users").child(userId)
                        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                if (userSnapshot.exists()) {
                                    val password = userSnapshot.child("user_pass").getValue(String::class.java) ?: ""
                                    originalData["user_pass"] = password
                                    binding.password.setText(password)
                                } else {
                                    Log.w(TAG, "Không tìm thấy người dùng với userId: $userId")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Lỗi khi tải dữ liệu user", error.toException())
                            }
                        })
                    } else {
                        Log.w(TAG, "Không tìm thấy thông tin admin tương ứng")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Lỗi khi tải dữ liệu admin", error.toException())
                }
            })
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("🔒 Đăng xuất")
            .setMessage("Bạn chắc chắn muốn đăng xuất chứ?")
            .setPositiveButton("✅ Đồng ý") { _, _ ->
                auth.signOut()
                redirectToLogin()
            }
            .setNegativeButton("❌ Không", null)
            .setCancelable(false)
            .show()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, DangNhapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
