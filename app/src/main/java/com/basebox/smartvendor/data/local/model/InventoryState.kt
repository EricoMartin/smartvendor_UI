package com.basebox.smartvendor.data.local.model

data class InventoryState(
    val name: String = "",
    val stock: String = "",
    val price: String = "",
    val category: String = "",
    val loading: Boolean = false
)