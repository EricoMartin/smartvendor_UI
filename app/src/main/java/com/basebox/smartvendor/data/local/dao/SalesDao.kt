package com.basebox.smartvendor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basebox.smartvendor.data.local.model.Sales
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Sales>)

    @Query("SELECT * FROM sales")
    fun getAllItems(): Flow<List<Sales>>

    @Query("SELECT * FROM sales WHERE vendorId = :vendorId")
    fun getItemsByVendorId(vendorId: String): Flow<List<Sales>>

    @Query("SELECT * FROM sales WHERE vendorId = :vendorId ORDER BY timestamp DESC LIMIT 1")
    fun getLastItemByVendorId(vendorId: String): Flow<Sales?>

    @Query("SELECT * FROM sales WHERE itemId = :itemId")
    fun getItemById(itemId: String): Flow<Sales?>

    @Query("DELETE FROM sales WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM sales WHERE vendorId = :vendorId")
    suspend fun deleteByVendorId(vendorId: String)
}
