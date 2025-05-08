package com.example.pethouse.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.adapter.SanPhamAdapter
import com.example.pethouse.databinding.FragmentTimKiemBinding
import com.example.pethouse.model.CartItems
import com.example.pethouse.model.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TimKiemFragment : Fragment() {
    private lateinit var binding: FragmentTimKiemBinding
    private lateinit var adapter: SanPhamAdapter
    private lateinit var database: FirebaseDatabase
    private val originalProdItems = mutableListOf<ProductItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimKiemBinding.inflate(inflater, container, false)
        setupSearchView()
        retrieveProdItem()


        return binding.root
    }

    private fun retrieveProdItem() {
        database= FirebaseDatabase.getInstance()
        val prodReference: DatabaseReference= database.reference.child("product")
        prodReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(prodSnapshot in snapshot.children)
                {
                    val prodItem=prodSnapshot.getValue(ProductItem::class.java)
                    prodItem?.let {
                        originalProdItems.add(it)
                    }
                }
                showAllProd()
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun showAllProd() {
        val filterProdItem= ArrayList(originalProdItems)
        setAdapter(filterProdItem)
    }

    private fun setAdapter(filterProdItem: List<ProductItem>) {
        adapter = SanPhamAdapter(filterProdItem, requireContext()) { product ->
            addToCart(product)
        }
        binding.sanphamRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.sanphamRecyclerView.adapter = adapter
    }
    private fun addToCart(product: ProductItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
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
                Toast.makeText(requireContext(), "Thêm vào giỏ thất bại", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterItems(newText)
                return true
            }
        })

    }

    private fun filterItems(query: String) {
        val filterProductItems =originalProdItems.filter{
            it.prodName?.contains(query,ignoreCase = true)==true
        }
        setAdapter(filterProductItems)
    }


    companion object{

    }
}