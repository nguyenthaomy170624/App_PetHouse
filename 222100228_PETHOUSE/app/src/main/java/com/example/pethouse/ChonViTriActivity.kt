package com.example.pethouse

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.admin.AdminMainActivity
import com.example.pethouse.databinding.ActivityChonViTriBinding

class ChonViTriActivity : AppCompatActivity() {
    private val binding: ActivityChonViTriBinding by lazy {
        ActivityChonViTriBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList: Array<String> = arrayOf("Ấn độ", "Việt Nam", "Trung Quốc", "Nhật Bản")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        val autoCompleteTextView: AutoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
        binding.listOfLocation.setOnClickListener {
            binding.listOfLocation.showDropDown()
        }
        binding.btnNe.setOnClickListener {
            val intent = Intent(this, AdminMainActivity::class.java)
            startActivity(intent)
        }

    }


}