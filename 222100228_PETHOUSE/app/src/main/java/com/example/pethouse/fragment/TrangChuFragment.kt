package com.example.pethouse.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.pethouse.R
import com.example.pethouse.XemBannerActivity
import com.example.pethouse.adapter.SanPhamAdapter
import com.example.pethouse.databinding.FragmentTrangChuBinding
import com.example.pethouse.model.CartItems
import com.example.pethouse.model.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TrangChuFragment : Fragment() {
    private lateinit var binding: FragmentTrangChuBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var prodItems: MutableList<ProductItem>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentTrangChuBinding.inflate(inflater,container,false)
        binding.xemcuahang.setOnClickListener {
            val bottomSheetDialog = SanPhamCuaHangFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }
        retrieveAndDisplayPopularItems()
        return binding.root
    }
    private fun retrieveAndDisplayPopularItems()
    {
        database= FirebaseDatabase.getInstance()
        val prodRef: DatabaseReference = database.reference.child("product")
        prodItems=mutableListOf()
        prodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val prodItem = foodSnapshot.getValue(ProductItem::class.java)
                    prodItem?.let { prodItems.add(it) }
                }
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private  fun randomPopularItems()
    {
        val Index: List<Int> = prodItems.indices.toList().shuffled()
        val numItemsToShow = 6
        val subsetProdItems: List<ProductItem> = Index.take(numItemsToShow).map { prodItems[it] }
        setPopularItemsAdapter(subsetProdItems)
    }

    private fun setPopularItemsAdapter(subsetProdItems: List<ProductItem>) {
        val adapter = SanPhamAdapter(subsetProdItems, requireContext()) { product ->
            addToCart(product)
        }

        binding.PhoBienRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.PhoBienRecycleView.adapter = adapter
    }
    private fun addToCart(product: ProductItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartItem = CartItems(
            prodName = product.prodName ?: "",
            prodPrice = product.prodPrice ?: "",
            prodImage = product.prodImage ?: "",
            prodDescription = product.prodDescription ?: "",
            prodQuantity = 1
        )
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("customers").child(userId).child("CartItem").push()
            .setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Thêm vào giỏ thất bại", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner4, ScaleTypes.FIT))
        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
            }

            override fun onItemSelected(position: Int) {
                val imageResId = when (position) {
                    0 -> R.drawable.banner1
                    1 -> R.drawable.banner2
                    2 -> R.drawable.banner3
                    3 -> R.drawable.banner4
                    else -> R.drawable.banner1
                }

                val intent = Intent(requireContext(), XemBannerActivity::class.java)
                intent.putExtra("image_res_id", imageResId)
                startActivity(intent)
            }
        })

    }
}