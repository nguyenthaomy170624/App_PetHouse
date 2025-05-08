package com.example.pethouse.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pethouse.R
import com.example.pethouse.databinding.GiaohangItemBinding

class GiaoHangAdapter(
    private val customerNames: MutableList<String>,
    private val moneyStatus: MutableList<Boolean>
) : RecyclerView.Adapter<GiaoHangAdapter.GiaoHangViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiaoHangViewHolder {
        val binding = GiaohangItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GiaoHangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GiaoHangViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class GiaoHangViewHolder(private val binding: GiaohangItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tenkh.text = customerNames[position]
                if (moneyStatus[position]==true)
                {
                    trangthai.text= "Thanh toán thành công"
                }
                else{
                    trangthai.text="Chưa nhận được thanh toán"
                }
                val colorMap = mapOf(
                    true to R.color.xanhla,
                    false to R.color.maudo
                )
                val imageMap = mapOf(
                    true to R.drawable.dathanhtoan,
                    false to R.drawable.chuathanhtoan
                )
                moneyStatus[position].let { status ->
                    val context = trangthai.context
                    trangthai.setTextColor(ContextCompat.getColor(context, colorMap[status] ?: R.color.black))
                    anhtrangthai.setBackgroundResource(imageMap[status] ?: R.drawable.loading)
                }
            }
        }

    }
}
