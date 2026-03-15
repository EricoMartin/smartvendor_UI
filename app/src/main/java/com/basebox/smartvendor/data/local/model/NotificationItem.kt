package com.basebox.smartvendor.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "notification_items")
data class NotificationItem(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "SALE", // e.g., SALE, RESTOCK, LOW_STOCK
    @ServerTimestamp
    val timestamp: Date? = Date()
)
