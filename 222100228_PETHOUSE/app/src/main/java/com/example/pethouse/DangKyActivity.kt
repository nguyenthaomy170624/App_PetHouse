package com.example.pethouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.databinding.ActivityDangKyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DangKyActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String

    private val binding: ActivityDangKyBinding by lazy {
        ActivityDangKyBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.btnDangky.setOnClickListener {
            username = binding.edtName.text.toString().trim()
            email = binding.edtEmail.text.toString().trim()
            password = binding.edtPassword.text.toString().trim()

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            } else {
                if (validateInput()) {
                    createAccount(email, password)
                }
            }
        }

        binding.tvDangnhap.setOnClickListener {
            startActivity(Intent(this, DangNhapActivity::class.java))
        }
    }

    private fun validateInput(): Boolean {
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

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createTask ->
                if (createTask.isSuccessful) {
                    Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show()
                    saveUserData()
                    val intent = Intent(this, DangNhapActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Email này đã được đăng ký. Vui lòng đăng nhập hoặc dùng email khác.", Toast.LENGTH_SHORT).show()
                    Log.e("Account", "Tạo tài khoản thất bại", createTask.exception)
                }
            }
    }

    private fun saveUserData() {
        val userId = auth.currentUser!!.uid
        val userMap = mapOf(
            "user_id" to userId,
            "user_email" to email,
            "user_pass" to password
        )
        val customerMap = mapOf(
            "customer_id" to userId,
            "user_id" to userId,
            "customer_name" to username,
            "customer_email" to email
        )

        database.child("users").child(userId).setValue(userMap)
        database.child("customers").child(userId).setValue(customerMap)
    }
}