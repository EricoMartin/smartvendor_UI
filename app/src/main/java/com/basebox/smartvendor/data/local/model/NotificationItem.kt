package com.basebox.smartvendor.data.local.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "SALE", // e.g., SALE, RESTOCK, LOW_STOCK
    @ServerTimestamp
    val timestamp: Date? = null
)
