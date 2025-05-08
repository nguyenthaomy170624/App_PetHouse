package com.example.pethouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.databinding.ActivityBatDauBinding

class BatDauActivity : AppCompatActivity() {
    private  val binding: ActivityBatDauBinding by lazy{
        ActivityBatDauBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
        }
    }
}