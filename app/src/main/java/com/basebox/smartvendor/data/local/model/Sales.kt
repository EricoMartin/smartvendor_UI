package com.basebox.smartvendor.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sales")
data class Sales(
    @PrimaryKey
    val id: String = "",
    val itemId: String = "",
    val item: String = "",
    val profit: Int = 0,
    var amount: Double = 0.0,
    val quantity: Int = 0,
    val sellingPrice: Double = 0.0,
    var costPrice: Double = 0.0,
    val vendorId: String = "",
    val timestamp: Date = Date()
)
