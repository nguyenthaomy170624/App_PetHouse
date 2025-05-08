package com.example.pethouse.admin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.adapter.XemTatCaAdapter
import com.example.pethouse.databinding.ActivityXemTatCaBinding
import com.example.pethouse.model.ProductItem
import com.google.firebase.database.*

class XemTatCaActivity : AppCompatActivity() {
    private val binding: ActivityXemTatCaBinding by lazy {
        ActivityXemTatCaBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: XemTatCaAdapter
    private var prodItems: ArrayList<ProductItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnBacknha.setOnClickListener { finish() }
        databaseReference = FirebaseDatabase.getInstance().reference.child("product")
        retrieveProdItem()
    }

    private fun retrieveProdItem() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                prodItems.clear()
                for (prodSnapshot in snapshot.children) {
                    val prodItem = ProductItem(prodSnapshot)
                    prodItems.add(prodItem)
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    private fun setAdapter() {
        adapter = XemTatCaAdapter(this@XemTatCaActivity, prodItems, databaseReference) { position ->
            deleteProdItems(position)
        }
        binding.XemTatCaRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.XemTatCaRecyclerView.adapter = adapter
    }

    private fun deleteProdItems(position: Int) {
        val prodItemToDelete = prodItems[position]
        val prodItemKey = prodItemToDelete.key
        if (prodItemKey != null) {
            val prodReference = databaseReference.child(prodItemKey)
            prodReference.removeValue()
                .addOnSuccessListener {
                    Log.d("DeleteSuccess", "Product item deleted successfully")
                    prodItems.removeAt(position)
                    adapter.itemQuantities.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, prodItems.size)
                    Toast.makeText(this@XemTatCaActivity, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Log.d("DeleteError", "Error deleting product: ${error.message}")
                    Toast.makeText(this@XemTatCaActivity, "Xóa thất bại: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this@XemTatCaActivity, "Không tìm thấy khóa sản phẩm!", Toast.LENGTH_SHORT).show()
        }
    }
}