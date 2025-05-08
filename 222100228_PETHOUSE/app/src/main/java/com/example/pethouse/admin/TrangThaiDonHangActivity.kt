package com.example.pethouse.admin

import OrderDetails
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.adapter.GiaoHangAdapter
import com.example.pethouse.databinding.ActivityTrangThaiDonHangBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TrangThaiDonHangActivity : AppCompatActivity() {
    private val binding: ActivityTrangThaiDonHangBinding by lazy {
        ActivityTrangThaiDonHangBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList: ArrayList<OrderDetails> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnBacknha.setOnClickListener { finish() }
        retrieveCompleteOrderDetail()
    }
    private fun retrieveCompleteOrderDetail() {
        database = FirebaseDatabase.getInstance()
        val completeOrderReference= database.reference.child("CompletedOrder")
            .orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfCompleteOrderList.clear()

                for (orderSnapshot: DataSnapshot in snapshot.children) {
                    val completeOrder: OrderDetails? = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }
                listOfCompleteOrderList.reversed()
                setDataIntoRecyclerView()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun setDataIntoRecyclerView() {
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()

        for (order: OrderDetails in listOfCompleteOrderList) {
            order.userName?.let {
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
        }
        val adapter = GiaoHangAdapter(customerName,moneyStatus)
        binding.DonHangCaRecyclerView.adapter = adapter
        binding.DonHangCaRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}