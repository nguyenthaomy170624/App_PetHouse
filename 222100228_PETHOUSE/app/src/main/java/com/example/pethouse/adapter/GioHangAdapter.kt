package com.example.pethouse.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pethouse.databinding.GiohangItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GioHangAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private var cartImages: MutableList<String>,
    private var cartDescriptions: MutableList<String>,
    private val cartQuantity: MutableList<Int>
) : RecyclerView.Adapter<GioHangAdapter.GioHangViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber= cartItems.size
        itemQuantities = IntArray(cartItemNumber){1}
        cartItemsReference = database.reference.child("customers").child(userId).child("CartItem")

    }
    companion object {
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GioHangViewHolder {
        val binding = GiohangItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GioHangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GioHangViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount(): Int= cartItems.size
    fun getUpdateItemsQuantities(): MutableList<Int> {
        val itemQuantity=mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

    inner class GioHangViewHolder(private val binding: GiohangItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                carttensp.text = cartItems[position]
                cartgia.text = "${cartItemPrices[position]} VND"
                val uriString = cartImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(cartimage)
                tvSoluong.text = quantity.toString()

                btnGiam.setOnClickListener { deceaseQuantity(position) }
                btnTang.setOnClickListener { increaseQuantity(position) }
                btnXoa.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        AlertDialog.Builder(context)
                            .setTitle("🛒 Xóa sản phẩm")
                            .setMessage("Bạn có muốn xóa sản phẩm này ra giỏ hàng không?")
                            .setPositiveButton("✅ Có") { _, _ ->
                                deleteItem(itemPosition)
                            }
                            .setNegativeButton("❌ Không", null)
                            .setCancelable(false)
                            .show()
                    }
                }

            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.tvSoluong.text = itemQuantities[position].toString()
            }
        }

        private fun deceaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.tvSoluong.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            val positionRetriever = position
            getUniqueKeyAtPosition(positionRetriever) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if(uniqueKey!=null)
            { cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                cartItems.removeAt(position)
                cartImages.removeAt(position)
                cartDescriptions.removeAt(position)
                cartQuantity.removeAt(position)
                cartItemPrices.removeAt(position)
                Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show()
                itemQuantities = itemQuantities.filterIndexed { index, i -> index != position}.toIntArray()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)
            }.addOnFailureListener {
                Toast.makeText(context, "Lỗi xóa sản phẩm", Toast.LENGTH_SHORT).show()
            }
            }

        }



        private fun getUniqueKeyAtPosition(positionRetriever: Int, onComplete: (String?) -> Unit) {
            cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey:String? = null
                    snapshot.children.forEachIndexed { index,dataSnapshot->
                        if(index == positionRetriever){
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }

                    onComplete(uniqueKey)

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}