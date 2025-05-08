package com.example.pethouse.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pethouse.databinding.GandayItemBinding
import com.example.pethouse.model.ProductItem

class DaMuaGanDayAdapter(
    private var context: Context,
    private var prodNameList: ArrayList<String>,
    private var prodImageList: ArrayList<String>,
    private var prodPriceList: ArrayList<String>,
    private var prodQuantityList: ArrayList<Int>,
    private val onAddToCartClick: (ProductItem) -> Unit
): RecyclerView.Adapter<DaMuaGanDayAdapter.DaMuaGanDayViewHolder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DaMuaGanDayViewHolder {
        val binding= GandayItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DaMuaGanDayViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DaMuaGanDayViewHolder,
        position: Int,
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int= prodNameList.size
    inner class DaMuaGanDayViewHolder(private val binding: GandayItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tensp.text=prodNameList[position]
                gia.text=prodPriceList[position]
                tvSoluong.text=prodQuantityList[position].toString()
                val uriString= prodImageList[position]
                val uri= Uri.parse(uriString)
                Glide.with(context).load(uri).into(anhsp)
                binding.themvaogio.setOnClickListener {
                    val productItem = ProductItem(
                        prodName = prodNameList[position],
                        prodImage = uriString,
                        prodPrice = prodPriceList[position],
                        prodQuantity = prodQuantityList[position].toString()
                    )
                    AlertDialog.Builder(context)
                        .setTitle("🛒 Mua lại")
                        .setMessage("Bạn có muốn mua lại sản phẩm này không?")
                        .setPositiveButton("✅ Có") { _, _ ->
                            onAddToCartClick(productItem)
                        }
                        .setNegativeButton("❌ Không", null)
                        .setCancelable(false)
                        .show()
                }
            }
        }

    }
}