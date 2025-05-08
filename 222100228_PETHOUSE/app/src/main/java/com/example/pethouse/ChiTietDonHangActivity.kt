package com.example.pethouse

import OrderDetails
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.adapter.ChiTietDonHangAdapter
import com.example.pethouse.databinding.ActivityChiTietDonHangBinding

class ChiTietDonHangActivity : AppCompatActivity() {
    private val binding: ActivityChiTietDonHangBinding by lazy {
        ActivityChiTietDonHangBinding.inflate(layoutInflater)
    }
    private var userName: String?=null
    private var address: String?=null
    private var phoneNumber: String?=null
    private var totalPrice: String?=null
    private var prodNames: ArrayList<String> =arrayListOf()
    private var prodImages: ArrayList<String> = arrayListOf()
    private var prodQuantity: ArrayList<Int> = arrayListOf()
    private var prodPrices: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnback.setOnClickListener { finish() }
        getDataFromIntent()
    }

    private fun getDataFromIntent() {
        val receivedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as? OrderDetails
        if (receivedOrderDetails != null) {
            userName= receivedOrderDetails.userName
            prodNames= receivedOrderDetails.prodNames as ArrayList<String>
            prodImages= receivedOrderDetails.prodImages as ArrayList<String>
            address = receivedOrderDetails.address
            prodQuantity = receivedOrderDetails.prodQuantities as ArrayList<Int>
            phoneNumber = receivedOrderDetails.phoneNumber
            prodPrices = receivedOrderDetails.prodPrices as ArrayList<String>
            totalPrice = receivedOrderDetails.totalPrice
            setUserOrderDetails()
            setAdapter()
        }
    }

    private fun setUserOrderDetails() {
        binding.tvname.text= userName
        binding.tvdiachi.text= address
        binding.tvsdt.text= phoneNumber
        binding.tvtongtien.text= totalPrice



    }
    private fun setAdapter() {
        binding.ChiTietDonHangRecycleView.layoutManager = LinearLayoutManager(this)
        val adapter= ChiTietDonHangAdapter(this, prodNames, prodImages, prodQuantity,prodPrices)
        binding.ChiTietDonHangRecycleView.adapter=adapter

    }
}