package com.basebox.smartvendor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.basebox.smartvendor.data.local.dao.InventoryDao
import com.basebox.smartvendor.data.local.dao.NotificationDao
import com.basebox.smartvendor.data.local.dao.ReceiptDao
import com.basebox.smartvendor.data.local.dao.SalesDao
import com.basebox.smartvendor.data.local.dao.VendorDao
import com.basebox.smartvendor.data.local.dto.DateConverter
import com.basebox.smartvendor.data.local.dto.ReceiptConverter
import com.basebox.smartvendor.data.local.model.InventoryItem
import com.basebox.smartvendor.data.local.model.NotificationItem
import com.basebox.smartvendor.data.local.model.Receipt
import com.basebox.smartvendor.data.local.model.ReceiptItem
import com.basebox.smartvendor.data.local.model.Sales
import com.basebox.smartvendor.data.local.model.Vendor

@Database(
    entities = [InventoryItem::class, Sales::class, Receipt::class, ReceiptItem::class, Vendor::class, NotificationItem::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(ReceiptConverter::class, DateConverter::class)
abstract class SmartVendorDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao
    abstract fun salesDao(): SalesDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun vendorDao(): VendorDao
    abstract fun notificationDao(): NotificationDao
}