package com.example.pethouse.fragment
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.ThanhToanActivity
import com.example.pethouse.adapter.GioHangAdapter
import com.example.pethouse.databinding.FragmentGioHangBinding
import com.example.pethouse.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GioHangFragment : Fragment() {

    private lateinit var binding: FragmentGioHangBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var prodNames: MutableList<String>
    private lateinit var prodPrices: MutableList<String>
    private lateinit var prodImagesUri: MutableList<String>
    private lateinit var prodDescriptions: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: GioHangAdapter
    private lateinit var userId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGioHangBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        retrieveCartItems()

        binding.btnMuahang.setOnClickListener {
            getOrderItemsDetail()

        }

        return binding.root
    }

    private fun getOrderItemsDetail() {
        val orderReference = database.reference.child("customers").child(userId).child("CartItem")
        val prodName = mutableListOf<String>()
        val prodPrice = mutableListOf<String>()
        val prodImagesUri = mutableListOf<String>()
        val prodDescription = mutableListOf<String>()
        val prodQuantities=cartAdapter.getUpdateItemsQuantities()
        orderReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (prodSnapshot in snapshot.children) {
                    val cartItems = prodSnapshot.getValue(CartItems::class.java)
                    cartItems?.prodName?.let { prodName.add(it) }
                    cartItems?.prodPrice?.let { prodPrice.add(it) }
                    cartItems?.prodImage?.let { prodImagesUri.add(it) }
                    cartItems?.prodDescription?.let { prodDescription.add(it) }
                }
                orderNow(prodName,prodPrice,prodImagesUri,prodDescription,prodQuantities)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Đặt hàng thất bại. Vui lòng thử lại", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun orderNow(
        prodName: MutableList<String>,
        prodPrice: MutableList<String>,
        prodImagesUri: MutableList<String>,
        prodDescription: MutableList<String>,
        prodQuantities: MutableList<Int>,
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), ThanhToanActivity::class.java)
            intent.putExtra("ItemName", prodName as ArrayList<String>)
            intent.putExtra("ItemPrice", prodPrice as ArrayList<String>)
            intent.putExtra("ItemImage", prodImagesUri as ArrayList<String>)
            intent.putExtra("ItemDescription", prodDescription as ArrayList<String>)
            intent.putExtra("ItemQuantities", prodQuantities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val prodReference = database.reference.child("customers").child(userId).child("CartItem")
        prodNames = mutableListOf()
        prodPrices = mutableListOf()
        prodImagesUri = mutableListOf()
        prodDescriptions = mutableListOf()
        quantity = mutableListOf()

        prodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (prodSnapshot in snapshot.children) {
                    val cartItems = prodSnapshot.getValue(CartItems::class.java)
                    cartItems?.prodName?.let { prodNames.add(it) }
                    cartItems?.prodPrice?.let { prodPrices.add(it) }
                    cartItems?.prodImage?.let { prodImagesUri.add(it) }
                    cartItems?.prodDescription?.let { prodDescriptions.add(it) }
                    cartItems?.prodQuantity?.let { quantity.add(it) }
                }
                setAdapter()
            }

            private fun setAdapter() {

                cartAdapter = GioHangAdapter(
                    requireContext(),
                    prodNames,
                    prodPrices,
                    prodImagesUri,
                    prodDescriptions,
                    quantity
                )
                binding.cartRecyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
                binding.cartRecyclerView.adapter = cartAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("GioHangFragment", "Lỗi Firebase: ${error.message}")
            }
        })

    }

}