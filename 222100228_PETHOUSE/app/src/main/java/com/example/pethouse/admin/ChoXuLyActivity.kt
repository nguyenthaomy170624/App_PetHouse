package com.example.pethouse.admin

import OrderDetails
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.ChiTietDonHangActivity
import com.example.pethouse.adapter.ChoXuLyAdapter
import com.example.pethouse.databinding.ActivityChoXuLyBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChoXuLyActivity : AppCompatActivity(), ChoXuLyAdapter.OnItemClicked {
    private val binding: ActivityChoXuLyBinding by lazy {
        ActivityChoXuLyBinding.inflate(layoutInflater)
    }
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstProdOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnBacknha.setOnClickListener { finish() }
        database = FirebaseDatabase.getInstance()
        databaseOrderDetails = database.reference.child("OrderDetails")
        getOrderDetails()

    }

    private fun getOrderDetails() {
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ordersnapshot in snapshot.children) {
                    val orderDetails = ordersnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecycleView()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addDataToListForRecycleView() {
        for (orderItem in listOfOrderItem) {
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.prodImages?.filterNot { it.isEmpty() }?.forEach {
                listOfImageFirstProdOrder.add(it)
            }

        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.PendingRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter =
            ChoXuLyAdapter(this, listOfName, listOfTotalPrice,listOfImageFirstProdOrder, this)
        binding.PendingRecyclerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, ChiTietDonHangActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails)
        startActivity(intent)

    }

    override fun onItemAcceptClickListener(position: Int) {
        val childItemPushKey = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)


    }
    override fun onItemDispatchClickListener(position: Int) {
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
        val dispatchItemOrderReference =
            database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderReference.setValue(listOfOrderItem[position])
            .addOnSuccessListener {
                deleteThisItemFromOrderDetails(dispatchItemPushKey)
            }


    }

    private fun updateOrderAcceptStatus(position: Int) {
        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
        val buyHistoryReference =
            database.reference.child("customers").child(userIdOfClickedItem!!).child("BuyHistory")
                .child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)

    }

    fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemsReference =
            database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemsReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Đơn hàng đã được gửi đi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không thể gửi đơn hàng", Toast.LENGTH_SHORT).show()
            }
    }


}