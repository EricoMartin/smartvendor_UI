package com.basebox.smartvendor.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendors")
data class Vendor(
    @PrimaryKey
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val businessName: String = ""
)
