package com.example.pethouse

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pethouse.admin.ThemSanPhamActivity
import com.example.pethouse.databinding.ActivityMainBinding
import com.example.pethouse.fragment.ThongBaoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerview) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)
        binding.thongbao.setOnClickListener {
            val bottomSheetDialog = ThongBaoFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
        }
        binding.vitri.setOnClickListener {
            startActivity(Intent(this, ThongTinPetHouseActivity::class.java))

        }
    }
}


