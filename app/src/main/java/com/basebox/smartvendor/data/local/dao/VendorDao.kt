package com.basebox.smartvendor.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basebox.smartvendor.data.local.model.Vendor
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: Vendor)

    @Query("SELECT * FROM vendors WHERE id = :vendorId")
    suspend fun getVendorById(vendorId: String): Vendor?

    @Query("SELECT * FROM vendors WHERE id = :vendorId")
    fun getVendorByIdFlow(vendorId: String): Flow<Vendor?>

    //get all vendors
    @Query("SELECT * FROM vendors")
    fun getAllVendors(): Flow<List<Vendor>>

    //delete a vendor
    @Delete
    suspend fun deleteVendor(vendor: Vendor)
}
