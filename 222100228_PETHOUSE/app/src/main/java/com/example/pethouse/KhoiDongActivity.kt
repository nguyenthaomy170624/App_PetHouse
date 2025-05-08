package com.example.pethouse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class KhoiDongActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_khoi_dong)
        Handler().postDelayed({
            val intent= Intent(this, BatDauActivity::class.java)
            startActivity(intent)
            finish()
        },3000)


    }
}