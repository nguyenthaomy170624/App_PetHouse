package com.example.pethouse.fragment

import OrderDetails
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pethouse.DaMuaGanDayActivity
import com.example.pethouse.adapter.LichSuAdapter
import com.example.pethouse.databinding.FragmentLichSuBinding
import com.example.pethouse.model.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LichSuFragment : Fragment() {
    private lateinit var binding: FragmentLichSuBinding
    private lateinit var historyAdapter: LichSuAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLichSuBinding.inflate(layoutInflater, container, false)
        auth= FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()
        retrieveBuyHistory()
        binding.recentbuyitem.setOnClickListener {
            seeItemsRecentBuy()
        }
        return binding.root
    }


    private fun seeItemsRecentBuy() {
        listOfOrderItem.firstOrNull()?.let { recentBuy->
            val intent = Intent(requireContext(), DaMuaGanDayActivity::class.java)
            intent.putExtra("DaMuaGanDay", ArrayList(listOfOrderItem))
            startActivity(intent)
        }
    }

    private fun retrieveBuyHistory() {
        binding.recentbuyitem.visibility= View.INVISIBLE
        userId= auth.currentUser?.uid?:""
        val buyItemReference = database.reference.child("customers").child(userId).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentTime")
        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }

                listOfOrderItem.reverse()

                if (listOfOrderItem.isNotEmpty()) {
                    setDataInRecentBuyItem()
                    setPreviousBuyItemsRecycleView()

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun setDataInRecentBuyItem() {
        binding.recentbuyitem.visibility = View.VISIBLE
        val recentOrderItem= listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                TenSp.text = it.prodNames?.firstOrNull() ?: ""
                GiaSp.text = "${it.prodPrices?.firstOrNull() ?: ""} VND"
                val image= it.prodImages?.firstOrNull() ?: ""
                val uri= Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(ImageView)
            }
        }
    }
    private fun setPreviousBuyItemsRecycleView() {
        val previousOrders = listOfOrderItem.toMutableList()
        if (previousOrders.isNotEmpty()) {
            val rv = binding.LichSuRecyclerView
            rv.layoutManager = LinearLayoutManager(requireContext())
            historyAdapter = LichSuAdapter(previousOrders, requireContext())
            rv.adapter = historyAdapter
        }
    }
}

