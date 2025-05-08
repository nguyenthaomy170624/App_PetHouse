package com.example.pethouse.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pethouse.MainActivity
import com.example.pethouse.databinding.FragmentChucMungBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChucMungFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChucMungBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChucMungBinding.inflate(layoutInflater,container, false)
        binding.trangchu.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    companion object
}