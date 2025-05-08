package com.example.pethouse.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pethouse.databinding.ChitietdonhangItemBinding


class ChiTietDonHangAdapter(
    private var context: Context,
    private var prodNames: ArrayList<String>,
    private var prodImages: ArrayList<String>,
    private var prodQuantities: ArrayList<Int>,
    private var prodPrices: ArrayList<String>,

    ): RecyclerView.Adapter<ChiTietDonHangAdapter.ChiTietDonHangViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ChiTietDonHangViewHolder {
        val binding = ChitietdonhangItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChiTietDonHangViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChiTietDonHangViewHolder,
        position: Int,
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = prodNames.size
    inner  class ChiTietDonHangViewHolder(
        private  val binding: ChitietdonhangItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                name.text = prodNames[position]
                tvSoluong.text= prodQuantities[position].toString()
                var uriString= prodImages[position]
                var uri= Uri.parse(uriString)
                Glide.with(context).load(uri).into(anhsp)
                giasp.text= "${prodPrices[position]} VND"

            }
        }
    }

}