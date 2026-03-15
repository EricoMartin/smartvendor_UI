package com.basebox.smartvendor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basebox.smartvendor.data.local.model.InventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryItem>)

    @Query("SELECT * FROM inventory_items")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE isSynced = 0")
    suspend fun getUnsyncedItems(): List<InventoryItem>

    @Query("UPDATE inventory_items SET isSynced = 1 WHERE id IN (:itemIds)")
    suspend fun markItemsAsSynced(itemIds: List<String>)
}
