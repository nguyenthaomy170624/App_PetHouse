package com.example.pethouse.admin

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.databinding.ActivityThemSanPhamBinding
import com.example.pethouse.model.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class ThemSanPhamActivity : AppCompatActivity() {
    private lateinit var prodName: String
    private lateinit var prodPrice: String
    private var prodImage: Uri? = null
    private lateinit var prodDescription: String
    private lateinit var prodQuantity: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding: ActivityThemSanPhamBinding by lazy {
        ActivityThemSanPhamBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.btnThem.setOnClickListener {
            prodName = binding.edtTensp.text.toString().trim()
            prodPrice = binding.edtGia.text.toString().trim()
            prodDescription = binding.edtMota.text.toString().trim()
            prodQuantity = binding.edtSoluong.text.toString().trim()

            if (prodName.isNotBlank() && prodPrice.isNotBlank() && prodDescription.isNotBlank() && prodQuantity.isNotBlank()) {
                uploadData()
            } else {
                Toast.makeText(this, "Vui lòng cập nhật đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.chonanh.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnBacknha.setOnClickListener { finish() }
    }

    private fun uploadData() {
        if (prodImage == null) {
            Toast.makeText(this, "Bạn chưa chọn ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        val prodRef = database.getReference("product")
        val newItemKey = prodRef.push().key ?: return

        uploadImageToImgBB(newItemKey)
    }

    private fun uploadImageToImgBB(newItemKey: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val file = contentResolver.openInputStream(prodImage!!)?.let { inputStream ->
            val tempFile = File(cacheDir, "temp_image_$newItemKey.jpg")
            tempFile.outputStream().use { inputStream.copyTo(it) }
            tempFile
        } ?: return

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .addFormDataPart("key", "15084bc0901f6a1673f28ccd3d9da2b4") // Thay bằng API Key của bạn
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(requestBody)
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    val imageUrl = json.getJSONObject("data").getString("url")

                    val newItem = ProductItem(
                        prodName = prodName,
                        prodPrice = prodPrice,
                        prodDescription = prodDescription,
                        prodQuantity = prodQuantity,
                        prodImage = imageUrl
                    )

                    database.getReference("product").child(newItemKey).setValue(newItem)
                        .addOnSuccessListener {
                            runOnUiThread {
                                Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            runOnUiThread {
                                Toast.makeText(this, "Cập nhật dữ liệu thất bại", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Upload ảnh thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    binding.hienthichonanh.setImageURI(uri) // Hiển thị ảnh trên ImageView hienthichonanh
                    prodImage = uri // Lưu Uri để upload sau
                } catch (e: Exception) {
                    Toast.makeText(this, "Lỗi khi hiển thị ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
}