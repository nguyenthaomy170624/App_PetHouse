package com.example.pethouse

import OrderDetails
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.adapter.DaMuaGanDayAdapter
import com.example.pethouse.adapter.SanPhamAdapter
import com.example.pethouse.databinding.ActivityDaMuaGanDayBinding
import com.example.pethouse.model.CartItems
import com.example.pethouse.model.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DaMuaGanDayActivity : AppCompatActivity() {
    private val binding: ActivityDaMuaGanDayBinding by lazy {
        ActivityDaMuaGanDayBinding.inflate(layoutInflater)
    }
    private lateinit var allProdNames: ArrayList<String>
    private lateinit var allProdImages: ArrayList<String>
    private lateinit var allProdPrices: ArrayList<String>
    private lateinit var allProdQuantities: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBacknha.setOnClickListener {
            finish()
        }

        allProdNames = ArrayList()
        allProdImages = ArrayList()
        allProdPrices = ArrayList()
        allProdQuantities = ArrayList()

        val recentOrderBuyItems = intent.getSerializableExtra("DaMuaGanDay") as? ArrayList<OrderDetails>
        recentOrderBuyItems?.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                for (orderItem in orderDetails) {
                    orderItem.prodNames?.let { allProdNames.addAll(it) }
                    orderItem.prodImages?.let { allProdImages.addAll(it) }
                    orderItem.prodPrices?.let { allProdPrices.addAll(it) }
                    orderItem.prodQuantities?.let { allProdQuantities.addAll(it) }
                }
            }
        }

        setAdapter()
    }

    private fun setAdapter() {
        val rv = binding.GanDayRecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = DaMuaGanDayAdapter(this, allProdNames, allProdImages, allProdPrices, allProdQuantities) { product ->
            addToCart(product)
        }
        rv.adapter = adapter
    }
    private fun addToCart(product: ProductItem) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        val cartItem = CartItems(
            prodName = product.prodName ?: "",
            prodPrice = product.prodPrice ?: "",
            prodImage = product.prodImage ?: "",
            prodDescription = product.prodDescription ?: "",
            prodQuantity = 1
        )
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("customers").child(userId).child("CartItem").push()
            .setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi thêm vào giỏ", Toast.LENGTH_SHORT).show()
            }
    }
}