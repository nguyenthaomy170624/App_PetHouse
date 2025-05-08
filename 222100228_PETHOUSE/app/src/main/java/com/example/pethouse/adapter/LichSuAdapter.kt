package com.example.pethouse.adapter
import OrderDetails
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pethouse.databinding.LichsuItemBinding
import com.google.firebase.database.FirebaseDatabase

class LichSuAdapter(
    private val orderList: MutableList<OrderDetails>,
    private val context: Context
) : RecyclerView.Adapter<LichSuAdapter.LichSuViewHolder>() {

    private val database = FirebaseDatabase.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LichSuViewHolder {
        val binding = LichsuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LichSuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LichSuViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size

    inner class LichSuViewHolder(private val binding: LichsuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderDetails) {
            val prodName = order.prodNames?.firstOrNull() ?: ""
            val prodPrice = order.prodPrices?.firstOrNull() ?: ""
            val prodImage = order.prodImages?.firstOrNull() ?: ""
            binding.TenSp.text = prodName
            binding.GiaSp.text = "$prodPrice VND"

            val uri = Uri.parse(prodImage)
            Glide.with(context).load(uri).into(binding.ImageView)

            val isOrderAccepted = order.orderAccepted == true
            Log.d("LichSuAdapter", "orderAccepted: $isOrderAccepted")

            if (isOrderAccepted) {
                binding.TrangThaiDH.background?.setTint(0xFF00FF00.toInt()) // Màu xanh
                binding.danhan.visibility = View.VISIBLE
            } else {
                binding.TrangThaiDH.background?.setTint(0xFFFF0000.toInt()) // Màu đỏ
                binding.danhan.visibility = View.GONE
            }

            binding.danhan.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("🎉 Xác nhận đơn hàng")
                    .setMessage("🎁 Bạn đã nhận được hàng?")
                    .setPositiveButton("✅ Có") { _, _ ->
                        updateOrderStatus(order)
                        Toast.makeText(context, "Đã nhận được hàng", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("❌ Chưa", null)
                    .show()
            }
        }

        private fun updateOrderStatus(order: OrderDetails) {
            val itemPushKey = order.itemPushKey
            if (!itemPushKey.isNullOrEmpty()) {
                val completeOrderReference = database.reference
                    .child("CompletedOrder")
                    .child(itemPushKey)

                completeOrderReference.child("paymentReceived").setValue(true)
                    .addOnSuccessListener {
                        Log.d("LichSuAdapter", "Cập nhật paymentReceived thành công.")
                    }
                    .addOnFailureListener {
                        Log.e("LichSuAdapter", "Lỗi khi cập nhật paymentReceived: ${it.message}")
                    }
            }
        }
    }
}
