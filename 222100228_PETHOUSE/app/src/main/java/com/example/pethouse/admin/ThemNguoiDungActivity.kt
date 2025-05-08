package com.example.pethouse.admin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.databinding.ActivityThemNguoiDungBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ThemNguoiDungActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private val binding: ActivityThemNguoiDungBinding by lazy {
        ActivityThemNguoiDungBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.btnBacknha.setOnClickListener { finish() }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.btnTaotaikhoan.setOnClickListener {
            username = binding.edtName.text.toString().trim()
            email = binding.edtEmail.text.toString().trim()
            password = binding.edtPassword.text.toString().trim()

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createTask ->
                if (createTask.isSuccessful) {
                    Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show()
                    saveUserData()
                    // Xóa các intent chuyển hướng
                } else {
                    Toast.makeText(this, "Tạo tài khoản thất bại!", Toast.LENGTH_SHORT).show()
                    Log.e("Account", "Tạo tài khoản thất bại", createTask.exception)
                }
            }
    }

    private fun saveUserData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userMap = mapOf(
            "user_id" to userId,
            "user_email" to email
        )
        val customerMap = mapOf(
            "customer_id" to userId,
            "user_id" to userId,
            "customer_name" to username,
            "customer_email" to email
        )

        database.child("users").child(userId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    database.child("customers").child(userId).setValue(customerMap)
                        .addOnCompleteListener { customerTask ->
                            if (!customerTask.isSuccessful) {
                                Toast.makeText(this, "Lỗi lưu dữ liệu khách hàng", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Lỗi lưu dữ liệu người dùng", Toast.LENGTH_SHORT).show()
                }
            }
    }
}