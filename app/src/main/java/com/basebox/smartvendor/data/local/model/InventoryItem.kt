package com.basebox.smartvendor.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey
    @DocumentId
    var id: String = "",
    var name: String = "",
    val imageUrl: String = "",
    var stock: Int = 0,
    var price: Double = 0.0,
    var category: String? = null,
    val reorderThreshold: Int = 5,
    var receiptId: String = "",
    var costPrice: Double = 0.0,
    var sellingPrice: Double = 0.0,
    var isSynced: Boolean = false,
)

