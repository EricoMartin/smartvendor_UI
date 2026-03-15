package com.basebox.smartvendor.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.Date
import com.squareup.moshi.JsonClass

@Entity(tableName = "receipt_items")
@JsonClass(generateAdapter = true)
data class ReceiptItem(
    @PrimaryKey
    val productId: String = "",
    val item: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)


@Entity(tableName = "receipts")
@JsonClass(generateAdapter = true)
data class Receipt(
    @PrimaryKey
    val id: String = "",
    val vendorId: String = "",
    val imageUrl: String = "",
    val totalAmount: Double = 0.0,
    val date: Timestamp? = null,
    val items: List<ReceiptItem> = emptyList(),
    val rawText: String = "",
    val aiExtracted: Boolean = true
)