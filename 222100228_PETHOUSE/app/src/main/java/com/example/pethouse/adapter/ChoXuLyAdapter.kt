package com.example.pethouse.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pethouse.admin.ChoXuLyActivity
import com.example.pethouse.databinding.ChoxulyItemBinding

class ChoXuLyAdapter(
    private val context: Context,
    private val customer: MutableList<String>,
    private val price: MutableList<String>,
    private val productImage: MutableList<String>,
    private val itemClicked: ChoXuLyActivity
) : RecyclerView.Adapter<ChoXuLyAdapter.ChoXuLyViewHolder>() {
    interface OnItemClicked {
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoXuLyViewHolder {
        val binding = ChoxulyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChoXuLyViewHolder(binding)
    }

    override fun getItemCount(): Int = customer.size

    override fun onBindViewHolder(holder: ChoXuLyViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ChoXuLyViewHolder(private val binding: ChoxulyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position: Int) {
            binding.apply {
                tenkhachhang.text = customer[position]
                tongtien.text = price[position]
                var uriString= productImage[position]
                var uri= Uri.parse(uriString)
                Glide.with(context).load(uri).into(image)
                btnXacnhan.apply {
                    if (!isAccepted) {
                        text = "Xác nhận"
                    } else {
                        text = "Gửi hàng"
                    }
                    setOnClickListener {
                        if (!isAccepted) {
                            text = "Gửi hàng"
                            isAccepted = true
                            showToast("Đơn hàng đã được xác nhận, vui lòng chờ giao hàng")
                            itemClicked.onItemAcceptClickListener(position)
                        }
                        else {
                            customer.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showToast("Đơn hàng của bạn đã được gửi đi")
                            itemClicked.onItemDispatchClickListener(position)
                        }
                    }
                }
                itemView.setOnClickListener {
                    itemClicked.onItemClickListener(position)
                }

            }

        }
        private fun showToast(message: String){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}