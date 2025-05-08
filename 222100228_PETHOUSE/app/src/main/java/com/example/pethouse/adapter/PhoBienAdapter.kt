package com.example.pethouse.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pethouse.ThongTinSanPhamActivity
import com.example.pethouse.databinding.PhobienItemBinding

class PhoBienAdapter (private val items:List<String>,
                      private val price:List<String>,
                      private val image:List<Int>,
                      private val requireContext: Context
): RecyclerView.Adapter<PhoBienAdapter.PhoBienViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoBienViewHolder {
        return  PhoBienViewHolder(PhobienItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: PhoBienViewHolder, position: Int) {
        val item=items[position]
        val images=image[position]
        val price=price[position]
        holder.bind(item,price,images)
        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, ThongTinSanPhamActivity::class.java)
            intent.putExtra( "ItemName", item)
            intent.putExtra("ItemImage", images)
            requireContext.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }

    class PhoBienViewHolder(private  val binding: PhobienItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val imagesView =binding.anhsp
        fun bind(item: String,price:String,images: Int) {
            binding.tensp.text=item
            binding.gia.text="${price} VND"
            imagesView.setImageResource(images)
        }

    }

}
