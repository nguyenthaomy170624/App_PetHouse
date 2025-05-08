package com.example.pethouse.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.adapter.SanPhamAdapter
import com.example.pethouse.databinding.FragmentSanPhamCuaHangBinding
import com.example.pethouse.model.CartItems
import com.example.pethouse.model.ProductItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SanPhamCuaHangFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSanPhamCuaHangBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var prodItems: MutableList<ProductItem>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSanPhamCuaHangBinding.inflate(inflater, container, false)
        binding.btnBack.setOnClickListener {
            dismiss()
        }
        retrieveProdItems()
        return binding.root

    }

    private fun retrieveProdItems() {
        database= FirebaseDatabase.getInstance()
        val prodRef: DatabaseReference = database.reference.child("product")
        prodItems=mutableListOf()
        prodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (prodSnapshot in snapshot.children) {
                    val prodItem = prodSnapshot.getValue(ProductItem::class.java)
                    prodItem?.let { prodItems.add(it) }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun setAdapter() {
        val adapter = SanPhamAdapter(prodItems, requireContext()) { product ->
            addToCart(product)
        }
        binding.SanPhamRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.SanPhamRecyclerView.adapter = adapter
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
                Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Lỗi khi thêm vào giỏ", Toast.LENGTH_SHORT).show()
            }
    }



    companion object{

    }
}
