package com.basebox.smartvendor.data.local.model

import com.google.firebase.Timestamp
import java.util.Date
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReceiptItem(
    val productId: String = "",
    val item: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class Receipt(
    val id: String = "",
    val vendorId: String = "",
    val imageUrl: String = "",
    val totalAmount: Double = 0.0,
    val date: Timestamp? = null,
    val items: List<ReceiptItem> = emptyList(),
    val rawText: String = "",
    val aiExtracted: Boolean = true
)