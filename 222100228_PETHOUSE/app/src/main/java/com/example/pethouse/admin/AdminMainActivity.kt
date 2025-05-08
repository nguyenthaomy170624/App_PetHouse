package com.example.pethouse.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.DangNhapActivity
import com.example.pethouse.databinding.ActivityAdminMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminMainActivity : AppCompatActivity() {
    private val binding: ActivityAdminMainBinding by lazy {
        ActivityAdminMainBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completedOrderReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        completedOrderReference = database.reference.child("CompletedOrder")

        binding.addsp.setOnClickListener {
            startActivity(Intent(this, ThemSanPhamActivity::class.java))
        }
        binding.viewsp.setOnClickListener {
            startActivity(Intent(this, XemTatCaActivity::class.java))
        }
        binding.order.setOnClickListener {
            startActivity(Intent(this, TrangThaiDonHangActivity::class.java))
        }
        binding.hoso.setOnClickListener {
            startActivity(Intent(this, HoSoActivity::class.java))
        }
        binding.adduser.setOnClickListener {
            startActivity(Intent(this, ThemNguoiDungActivity::class.java))
        }
        binding.logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("🔒 Đăng xuất")
                .setMessage("Bạn chắc chắn muốn đăng xuất chứ?")
                .setPositiveButton("✅ Đồng ý") { _, _ ->
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this, DangNhapActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("❌ Không", null)
                .setCancelable(false)
                .show()
        }
        binding.choxuly.setOnClickListener {
            startActivity(Intent(this, ChoXuLyActivity::class.java))
        }

        pendingOrders()
        completedOrders()
        wholeTimeEarning()
    }

    private fun wholeTimeEarning() {
        val listOfTotalPay = mutableListOf<Int>()
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.totalPrice?.replace("VND", "")?.trim()?.toIntOrNull()?.let { price ->
                        listOfTotalPay.add(price)
                    }
                }
                binding.tvTongtien.text = "${listOfTotalPay.sum()} VND"
            }

            override fun onCancelled(error: DatabaseError) {
                binding.tvTongtien.text = "Error: ${error.message}"
            }
        })
    }

    private fun completedOrders() {
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val completeOrderItemCount = snapshot.childrenCount.toInt()
                binding.tvDagiao.text = completeOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                binding.tvDagiao.text = "Error: ${error.message}"
            }
        })
    }

    private fun pendingOrders() {
        val pendingOrderReference = database.reference.child("OrderDetails")
        pendingOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pendingOrderItemCount = snapshot.childrenCount.toInt()
                binding.tvchoxuly.text = "$pendingOrderItemCount"
            }

            override fun onCancelled(error: DatabaseError) {
                binding.tvchoxuly.text = "Error: ${error.message}"
            }
        })
    }
}