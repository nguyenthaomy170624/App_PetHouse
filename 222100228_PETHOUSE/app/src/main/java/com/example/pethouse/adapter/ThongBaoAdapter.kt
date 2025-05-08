package com.example.pethouse.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pethouse.databinding.ThongbaoItemBinding

class ThongBaoAdapter (private var notification: ArrayList<String>, private var notificationImage: ArrayList<Int> ) : RecyclerView.Adapter<ThongBaoAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ThongbaoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = notification.size

    inner class NotificationViewHolder(private val binding: ThongbaoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                notificationTextView.text = notification[position]
                notificationImageView.setImageResource(notificationImage[position])
            }
        }
    }
}