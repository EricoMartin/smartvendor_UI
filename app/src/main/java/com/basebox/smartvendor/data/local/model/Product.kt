package com.basebox.smartvendor.data.local.model

data class Product(
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val sold: Int = 0,
    val category: String = "",
    val imageUrl: String = "",
)

