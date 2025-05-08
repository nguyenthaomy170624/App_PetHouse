package com.example.pethouse

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class XemBannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xem_banner)
        val imageResId = intent.getIntExtra("image_res_id", R.drawable.banner1)

        val imageView = findViewById<ImageView>(R.id.imageView)
        Glide.with(this)
            .load(imageResId)
            .into(imageView)

    }
}