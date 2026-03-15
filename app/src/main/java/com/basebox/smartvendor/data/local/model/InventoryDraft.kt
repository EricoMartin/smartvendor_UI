package com.basebox.smartvendor.data.local.model

data class InventoryDraft(
    val name: String = "",
    val stock: Int = 0,
    val price: Double = 0.0,
    val category: String? = null
)
