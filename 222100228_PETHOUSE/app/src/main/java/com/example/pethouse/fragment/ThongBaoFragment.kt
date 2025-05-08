package com.example.pethouse.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethouse.R
import com.example.pethouse.adapter.ThongBaoAdapter
import com.example.pethouse.databinding.FragmentThongBaoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ThongBaoFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentThongBaoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThongBaoBinding.inflate(layoutInflater, container, false)
        val notifications = listOf("Chào mừng bạn đến với Pet House","Chúc bạn ngày mới tốt lành")
        val notificationImages = listOf(R.drawable.happy, R.drawable.day)

        val adapter = ThongBaoAdapter(
            ArrayList(notifications),
            ArrayList(notificationImages)
        )

        binding.notificatonRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificatonRecyclerView.adapter = adapter

        return binding.root
    }

    companion object
}