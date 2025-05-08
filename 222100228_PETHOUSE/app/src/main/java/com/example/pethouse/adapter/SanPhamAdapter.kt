package com.example.pethouse.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pethouse.ThongTinSanPhamActivity
import com.example.pethouse.databinding.CuahangItemBinding
import com.example.pethouse.model.ProductItem

class SanPhamAdapter(
    private val prodItems: List<ProductItem>,
    private val requireContext: Context,
    private val onAddToCartClick: (ProductItem) -> Unit
): RecyclerView.Adapter<SanPhamAdapter.SanPhamViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SanPhamViewHolder {
        val binding = CuahangItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SanPhamViewHolder(binding)
    }
    override fun getItemCount(): Int = prodItems.size

    override fun onBindViewHolder(holder: SanPhamViewHolder, position: Int) {
        holder.bind(position)
    }
    inner class SanPhamViewHolder(private val binding: CuahangItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailActivity(position)
                }

            }
        }

        private fun openDetailActivity(position: Int) {
            val prodItem=prodItems[position]
            val intent= Intent(requireContext,ThongTinSanPhamActivity::class.java).apply{
                putExtra("ItemName", prodItem.prodName)
                putExtra("ItemPrice", prodItem.prodPrice)
                putExtra("ItemDescription", prodItem.prodDescription)
                putExtra("ItemImage", prodItem.prodImage)
                putExtra("ItemQuantity", prodItem.prodQuantity)

            }
            requireContext.startActivity(intent)

        }

        fun bind(position: Int) {
            val prodItem=prodItems[position]
            binding.apply {
                tensp.text = prodItem.prodName
                gia.text = "${prodItem.prodPrice} VND"
                val uri = Uri.parse(prodItem.prodImage)
                Glide.with(requireContext).load(uri).into(hinhanh)
                binding.themvaogio.setOnClickListener {
                    AlertDialog.Builder(requireContext)
                        .setTitle("🛒 Thêm vào giỏ hàng")
                        .setMessage("Bạn có muốn thêm sản phẩm này vào giỏ hàng không?")
                        .setPositiveButton("✅ Có") { _, _ ->
                            onAddToCartClick(prodItem)
                        }
                        .setNegativeButton("❌ Không", null)
                        .setCancelable(false)
                        .show()
                }

            }
        }


    }

}


