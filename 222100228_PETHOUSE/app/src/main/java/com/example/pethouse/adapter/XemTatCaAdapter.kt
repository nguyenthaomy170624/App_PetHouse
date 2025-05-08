package com.example.pethouse.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pethouse.databinding.XemtatcaItemBinding
import com.example.pethouse.model.ProductItem
import com.google.firebase.database.DatabaseReference

class XemTatCaAdapter(
    private val context: Context,
    private val prodList: ArrayList<ProductItem>,
    private val databaseReference: DatabaseReference,
    private val onDeleteClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<XemTatCaAdapter.XemTatCaViewHolder>() {

    internal val itemQuantities = MutableList(prodList.size) { 1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XemTatCaViewHolder {
        val binding = XemtatcaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return XemTatCaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: XemTatCaViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = prodList.size

    inner class XemTatCaViewHolder(private val binding: XemtatcaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val realPosition = bindingAdapterPosition
            if (realPosition == RecyclerView.NO_POSITION) return
            val prodItem = prodList[realPosition]
            val quantity = itemQuantities[realPosition] // Renamed for clarity
            val uri = Uri.parse(prodItem.prodImage)

            binding.apply {
                tenSp.text = prodItem.prodName
                giaSp.text = "${prodItem.prodPrice} VND"
                tvSoluong.text = quantity.toString()
                Glide.with(binding.root.context).load(uri).into(xemHinh)

                btnTang.setOnClickListener { increaseQuantity() }
                btnGiam.setOnClickListener { decreaseQuantity() }
                btnXoa.setOnClickListener { onDeleteClickListener(realPosition) } // Use realPosition
            }
        }

        private fun increaseQuantity() {
            val realPosition = bindingAdapterPosition
            if (realPosition != RecyclerView.NO_POSITION && realPosition < itemQuantities.size) {
                if (itemQuantities[realPosition] < 10) {
                    itemQuantities[realPosition]++
                    notifyItemChanged(realPosition)
                }
            }
        }

        private fun decreaseQuantity() {
            val realPosition = bindingAdapterPosition
            if (realPosition != RecyclerView.NO_POSITION && realPosition < itemQuantities.size) {
                if (itemQuantities[realPosition] > 1) {
                    itemQuantities[realPosition]--
                    notifyItemChanged(realPosition)
                }
            }
        }
    }
}