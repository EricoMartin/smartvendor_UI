package com.basebox.smartvendor.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.basebox.smartvendor.data.datastore.UserPreferences
import com.basebox.smartvendor.data.local.dao.InventoryDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore,
    private val inventoryDao: InventoryDao,
    private val userPreferences: UserPreferences
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            syncInventory()
            // Add other sync functions here (e.g., syncSales())
            Result.success()
        } catch (e: Exception) {
            // Use Result.retry() for transient network errors
            Result.retry()
        }
    }

    private suspend fun syncInventory() {
        val vendorId = userPreferences.loggedInUserUid.first()
        if (vendorId.isNullOrBlank()) return

        val unsyncedItems = inventoryDao.getUnsyncedItems()
        if (unsyncedItems.isEmpty()) return



        // This is a simplified example. A real implementation would handle
        // conflicts and updates more gracefully using batched writes.
        val writeBatch = firestore.batch()
        val syncedIds = mutableListOf<String>()

        unsyncedItems.forEach { item ->
            val docRef = firestore.collection("vendors").document(vendorId)
                .collection("inventory").document(item.id)
            writeBatch.set(docRef, item) // Uploads the local item
            syncedIds.add(item.id)
        }

        writeBatch.commit().await()
        inventoryDao.markItemsAsSynced(syncedIds)
    }
}
