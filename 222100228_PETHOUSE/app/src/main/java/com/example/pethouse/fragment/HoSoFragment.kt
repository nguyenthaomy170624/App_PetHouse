package com.example.pethouse.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.pethouse.DangNhapActivity
import com.example.pethouse.databinding.FragmentHoSoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HoSoFragment : Fragment() {
    private lateinit var binding: FragmentHoSoBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val TAG = "HoSoFragment"
    private var originalData = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoSoBinding.inflate(inflater, container, false)

        setFieldsEnabled(false)
        setUserAndCustomerData()

        binding.btnEdit.setOnClickListener {
            val isEnable = !binding.name.isEnabled
            setFieldsEnabled(isEnable)
            if (isEnable) binding.name.requestFocus()
        }

        binding.luuthongtin.setOnClickListener {
            if (auth.currentUser == null) {
                Toast.makeText(context, "Phiên đăng nhập đã hết, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
                redirectToLogin()
                return@setOnClickListener
            }

            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val address = binding.address.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val password = binding.password.text.toString()

            if (validateInput(name, email, phone, password)) {
                updateUserAndCustomerData(name, email, address, phone, password)
            }
        }

        binding.btnDangxuat.setOnClickListener {
            showLogoutDialog()
        }

        return binding.root
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
            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isNotEmpty() && password.length < 6) {
            Toast.makeText(context, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updateUserAndCustomerData(name: String, email: String, address: String, phone: String, password: String) {
        val userId = auth.currentUser?.uid ?: return
        val customerReference = database.getReference("customers")
        val changes = mutableListOf<String>()

        customerReference.orderByChild("user_id").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val customerSnapshot = snapshot.children.first()
                        val customerId = customerSnapshot.key

                        if (customerId != null) {
                            val customerData = mutableMapOf<String, Any>()

                            if (name != originalData["customer_name"]) {
                                customerData["customer_name"] = name
                                changes.add("Họ tên")
                            }
                            if (email != originalData["customer_email"]) {
                                customerData["customer_email"] = email
                                changes.add("Email")
                            }
                            if (address != originalData["customer_address"] && address.isNotEmpty()) {
                                customerData["customer_address"] = address
                                changes.add("Địa chỉ")
                            }
                            if (phone != originalData["customer_phone"]) {
                                customerData["customer_phone"] = phone
                                changes.add("Số điện thoại")
                            }

                            if (customerData.isNotEmpty()) {
                                customerReference.child(customerId).updateChildren(customerData)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Customer data updated: $customerData")
                                        updateUserData(userId, password, changes)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Failed to update customer data", e)
                                        Toast.makeText(context, "Cập nhật thông tin thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                updateUserData(userId, password, changes)
                            }
                        } else {
                            Log.e(TAG, "Customer key is null")
                            Toast.makeText(context, "Không tìm thấy khóa khách hàng", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.w(TAG, "Customer snapshot does not exist for userId: $userId")
                        Toast.makeText(context, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Database error", error.toException())
                    Toast.makeText(context, "Lỗi cơ sở dữ liệu: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateUserData(userId: String, password: String, changes: MutableList<String>) {
        val user = auth.currentUser ?: return
        val userReference = database.getReference("users").child(userId)

        if (password.isNotBlank() && password != originalData["user_pass"]) {
            user.updatePassword(password).addOnCompleteListener { passwordTask ->
                if (passwordTask.isSuccessful) {
                    Log.d(TAG, "Password updated successfully in Firebase Auth")
                    changes.add("Mật khẩu")
                    saveUserToDatabase(userId, password, changes)
                } else {
                    Log.e(TAG, "Failed to update password in Firebase Auth", passwordTask.exception)
                    when (passwordTask.exception) {
                        is FirebaseAuthRecentLoginRequiredException -> {
                            Toast.makeText(context, "Vui lòng đăng nhập lại để cập nhật mật khẩu", Toast.LENGTH_LONG).show()
                            redirectToLogin()
                        }
                        else -> {
                            Toast.makeText(context, "Lỗi cập nhật mật khẩu: ${passwordTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
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
                    Log.d(TAG, "User password updated successfully")
                    showUpdateResult(changes)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to save user data", e)
                    Toast.makeText(context, "Lưu thông tin thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            showUpdateResult(changes)
        }
    }

    private fun showUpdateResult(changes: List<String>) {
        val message = if (changes.isEmpty()) {
            "Không có thông tin nào được cập nhật"
        } else {
            "Đã cập nhật: ${changes.joinToString(", ")}"
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        setFieldsEnabled(false)
    }

    private fun setUserAndCustomerData() {
        val userId = auth.currentUser?.uid ?: run {
            Log.w(TAG, "User not logged in")
            Toast.makeText(context, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            redirectToLogin()
            return
        }

        val customerReference = database.getReference("customers")
        customerReference.orderByChild("user_id").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val customerSnapshot = snapshot.children.first()
                        val customerId = customerSnapshot.key

                        if (customerId != null) {
                            val name = customerSnapshot.child("customer_name").getValue(String::class.java) ?: ""
                            val email = customerSnapshot.child("customer_email").getValue(String::class.java) ?: ""
                            val phone = customerSnapshot.child("customer_phone").getValue(String::class.java) ?: ""
                            val address = customerSnapshot.child("customer_address").getValue(String::class.java) ?: ""

                            originalData["customer_name"] = name
                            originalData["customer_email"] = email
                            originalData["customer_phone"] = phone
                            originalData["customer_address"] = address

                            binding.name.setText(name)
                            binding.email.setText(email)
                            binding.phone.setText(phone)
                            binding.address.setText(address)
                            Log.d(TAG, "Customer data loaded successfully for key: $customerId")
                        } else {
                            Log.e(TAG, "Customer key is null")
                            Toast.makeText(context, "Không tìm thấy khóa khách hàng", Toast.LENGTH_SHORT).show()
                        }

                        val userReference = database.getReference("users").child(userId)
                        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                if (userSnapshot.exists()) {
                                    val password = userSnapshot.child("user_pass").getValue(String::class.java) ?: ""
                                    originalData["user_pass"] = password
                                    binding.password.setText(password)
                                    Log.d(TAG, "User data loaded successfully")
                                } else {
                                    Log.w(TAG, "User data not found for userId: $userId")
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Failed to load user data", error.toException())
                                Toast.makeText(context, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        Log.w(TAG, "Customer data not found for userId: $userId")
                        Toast.makeText(context, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to load customer data", error.toException())
                    Toast.makeText(context, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
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
        val intent = Intent(requireContext(), DangNhapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    companion object
}