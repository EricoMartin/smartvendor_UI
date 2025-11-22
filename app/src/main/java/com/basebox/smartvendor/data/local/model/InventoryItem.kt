package com.basebox.smartvendor.data.local.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class InventoryItem(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    var price: Double = 0.0,
    val category: String? = null,
    val reorderThreshold: Int = 5,
    val receiptId: String = "",
    var costPrice: Double = 0.0,
    var sellingPrice: Double = 0.0,
)
