package com.example.pethouse

import OrderDetails
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.databinding.ActivityThanhToanBinding
import com.example.pethouse.fragment.ChucMungFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ThanhToanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThanhToanBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var prodName: ArrayList<String>
    private lateinit var prodPrice: ArrayList<String>
    private lateinit var prodImage: ArrayList<String>
    private lateinit var prodDescription: ArrayList<String>
    private lateinit var prodQuantities: ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThanhToanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        setUserData()

        prodName = intent.getStringArrayListExtra("ItemName") ?: ArrayList()
        prodPrice = intent.getStringArrayListExtra("ItemPrice") ?: ArrayList()
        prodImage = intent.getStringArrayListExtra("ItemImage") ?: ArrayList()
        prodDescription = intent.getStringArrayListExtra("ItemDescription") ?: ArrayList()
        prodQuantities = intent.getIntegerArrayListExtra("ItemQuantities") ?: ArrayList()

        totalAmount = "${calculatetotalAmount()} VND"
        binding.edttongtien.setText(totalAmount)

        binding.btnTienhanhdathang.setOnClickListener {
            name = binding.edtname.text.toString().trim()
            phone = binding.edtsdt.text.toString().trim()
            address = binding.edtdiachi.text.toString().trim()
            if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }

        binding.btnback.setOnClickListener { finish() }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key ?: return

        val orderDetails = OrderDetails(
            userId, name, prodName, prodPrice, prodImage, prodQuantities, address, totalAmount, phone, time, itemPushKey, false, false
        )

        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = ChucMungFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi đặt hàng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("customers").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails)
    }

    private fun removeItemFromCart() {
        val cartItemsReference = databaseReference.child("customers").child(userId).child("CartItem")
        cartItemsReference.removeValue()
    }

    private fun calculatetotalAmount(): Int {
        var totalAmount = 0
        for (i in prodPrice.indices) {
            val price = prodPrice[i]
            val priceIntValue = if (price.endsWith("VND")) {
                price.removeSuffix("VND").trim().toIntOrNull() ?: 0
            } else {
                price.trim().toIntOrNull() ?: 0
            }
            val quantity = prodQuantities[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser ?: return
        userId = user.uid
        val userReference = databaseReference.child("customers").child(userId)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val names = snapshot.child("customer_name").getValue(String::class.java) ?: ""
                    val addresses = snapshot.child("customer_address").getValue(String::class.java) ?: ""
                    val phones = snapshot.child("customer_phone").getValue(String::class.java) ?: ""
                    binding.apply {
                        edtname.setText(names)
                        edtdiachi.setText(addresses)
                        edtsdt.setText(phones)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ThanhToanActivity, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}