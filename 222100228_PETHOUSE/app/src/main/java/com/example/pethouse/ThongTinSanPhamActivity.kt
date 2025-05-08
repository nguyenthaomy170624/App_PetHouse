package com.example.pethouse
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pethouse.databinding.ActivityThongTinSanPhamBinding
import com.example.pethouse.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ThongTinSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThongTinSanPhamBinding
    private var prodName: String? = null
    private var prodDescription: String? = null
    private var prodQuality: String? = null
    private var prodPrice: String? = null
    private var prodImage: String? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThongTinSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= FirebaseAuth.getInstance()
        prodName = intent.getStringExtra("ItemName")
        prodDescription = intent.getStringExtra("ItemDescription")
        prodQuality = intent.getStringExtra("ItemQuality")
        prodPrice = intent.getStringExtra("ItemPrice")
        prodImage = intent.getStringExtra("ItemImage")

        with(binding) {
            detailName.text = prodName
            Glide.with(this@ThongTinSanPhamActivity).load(Uri.parse(prodImage)).into(detailImage)
            detailDescription.text = prodDescription
            detailPrice.text = "${prodPrice} VND"
        }

        binding.imageButton.setOnClickListener {
            finish()
        }
        binding.btnThemvaogio.setOnClickListener{
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database= FirebaseDatabase.getInstance().reference
        val userId=auth.currentUser?.uid?:""
        val cartItem= CartItems(prodName.toString(),prodPrice.toString(),prodImage.toString(),prodDescription.toString(),1)
        database.child("customers").child(userId).child("CartItem").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this,"Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"Lỗi thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show()
        }
    }
}