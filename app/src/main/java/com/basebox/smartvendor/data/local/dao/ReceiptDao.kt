package com.basebox.smartvendor.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.basebox.smartvendor.data.local.model.Receipt
import com.basebox.smartvendor.data.local.model.ReceiptItem

@Dao
interface ReceiptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReceipt(receipt: Receipt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReceiptItems(items: List<ReceiptItem>)

    // Store JSON items automatically through TypeConverter
    @Transaction
    suspend fun upsertReceiptWithItems(receipt: Receipt) {
        upsertReceipt(receipt)
        upsertReceiptItems(receipt.items)
    }

    @Query("SELECT * FROM receipts ORDER BY date DESC")
    suspend fun getAllReceipts(): List<Receipt>

    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    suspend fun getReceipt(id: String): Receipt?

    @Delete
    suspend fun deleteReceipt(receipt: Receipt)

    @Query("DELETE FROM receipts WHERE id = :id")
    suspend fun deleteReceiptById(id: String)
}
