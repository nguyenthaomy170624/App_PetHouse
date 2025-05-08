package com.example.pethouse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.admin.AdminMainActivity
import com.example.pethouse.databinding.ActivityDangNhapBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DangNhapActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private val binding: ActivityDangNhapBinding by lazy {
        ActivityDangNhapBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Tạo Firebase Auth và Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.tvDangky.setOnClickListener {
            val intent = Intent(this, DangKyActivity::class.java)
            startActivity(intent)
        }
        // đăng nhập bằng Google
        binding.btnGg.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
        // đăng nhập bằng email và password
        binding.btnDangnhap.setOnClickListener {
            email = binding.edtEmail.text.toString().trim()
            password = binding.edtPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signInUser(email, password)
        }
    }
    // đăng nhập bằng email/password
    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        database.child("admin").orderByChild("user_id").equalTo(userId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(adminSnapshot: DataSnapshot) {
                                    if (adminSnapshot.exists()) {
                                        Toast.makeText(this@DangNhapActivity, "Chào mừng Admin!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@DangNhapActivity, AdminMainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@DangNhapActivity, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                        updateUi(auth.currentUser)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("DangNhapActivity", "Lỗi kiểm tra admin: ${error.message}")
                                }
                            })
                    } else {
                        Log.e("DangNhapActivity", "Lỗi: Không lấy được UID")
                    }
                } else {
                    Toast.makeText(this@DangNhapActivity, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)

                task.addOnSuccessListener { account ->
                    Log.d("GoogleSignIn", "Đăng nhập Google thành công: ${account.email}")

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Log.d("GoogleSignIn", "Firebase Auth thành công với Google")

                            val user = authTask.result?.user
                            if (user != null) {
                                Log.d("GoogleSignIn", "Người dùng Firebase UID: ${user.uid}")
                                checkAndSaveGoogleUser(user, account)
                            } else {
                                Log.e("GoogleSignIn", "Firebase user null")
                            }

                            updateUi(user)
                        } else {
                            Log.e("GoogleSignIn", "Firebase Auth thất bại", authTask.exception)
                            Toast.makeText(this, "Lỗi đăng nhập Google", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener {
                    Log.e("GoogleSignIn", "Lỗi lấy tài khoản từ Google", it)
                    Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("GoogleSignIn", "Đăng nhập thất bại - resultCode: ${result.resultCode}")
                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
            }
        }


    private fun checkAndSaveGoogleUser(user: FirebaseUser, account: GoogleSignInAccount) {
        val userId = user.uid
        Log.d("GoogleSignIn", "checkAndSaveGoogleUser được gọi với uid: $userId")

        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("GoogleSignIn", "Snapshot exists: ${snapshot.exists()}")
                Log.d("GoogleSignIn", "Dữ liệu hiện tại trong users/$userId: ${snapshot.value}")

                if (!snapshot.exists()) {
                    val email = account.email ?: ""
                    val displayName = account.displayName ?: ""

                    Log.d("GoogleSignIn", "Người dùng chưa tồn tại, tiến hành lưu")

                    val userMap = mapOf(
                        "user_id" to userId,
                        "user_email" to email,
                        "user_pass" to null
                    )

                    val customerMap = mapOf(
                        "customer_id" to userId,
                        "user_id" to userId,
                        "customer_name" to displayName,
                        "customer_email" to email
                    )

                    database.child("users").child(userId).setValue(userMap)
                        .addOnSuccessListener {
                            Log.d("GoogleSignIn", "Đã lưu vào users/$userId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("GoogleSignIn", "Lỗi lưu vào users: ${e.message}", e)
                        }

                    database.child("customers").child(userId).setValue(customerMap)
                        .addOnSuccessListener {
                            Log.d("GoogleSignIn", "Đã lưu vào customers/$userId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("GoogleSignIn", "Lỗi lưu vào customers: ${e.message}", e)
                        }
                } else {
                    Log.d("GoogleSignIn", "Người dùng đã tồn tại, không lưu nữa")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GoogleSignIn", "Lỗi kiểm tra tồn tại người dùng: ${error.message}", error.toException())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            database.child("admin").orderByChild("user_id").equalTo(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            startActivity(Intent(this@DangNhapActivity, AdminMainActivity::class.java))
                        } else {
                            updateUi(currentUser)
                        }
                        finish()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("DangNhapActivity", "Lỗi: ${error.message}")
                    }
                })
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}