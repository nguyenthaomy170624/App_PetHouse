package com.example.pethouse.model

import com.google.firebase.database.DataSnapshot

data class ProductItem(
    val key: String? = null,
    val prodName: String? = null,
    val prodPrice: String? = null,
    val prodImage: String? = null,
    val prodDescription: String? = null,
    val prodQuantity: String? = null
) {
    constructor(snapshot: DataSnapshot) : this(
        key = snapshot.key,
        prodName = snapshot.child("prodName").getValue(String::class.java),
        prodPrice = snapshot.child("prodPrice").getValue(String::class.java),
        prodImage = snapshot.child("prodImage").getValue(String::class.java),
        prodDescription = snapshot.child("prodDescription").getValue(String::class.java),
        prodQuantity = snapshot.child("prodQuantity").getValue(String::class.java)
    )
}