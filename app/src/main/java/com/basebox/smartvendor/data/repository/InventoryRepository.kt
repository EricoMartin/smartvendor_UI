package com.basebox.smartvendor.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.basebox.smartvendor.data.local.model.InventoryItem
import com.basebox.smartvendor.data.local.model.Product
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun getInventory(vendorId: String): Flow<List<InventoryItem>> = callbackFlow {
        val listener = db.collection("vendors")
            .document(vendorId)
            .collection("inventory")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.map {
                    InventoryItem(
                        id = it.id,
                        name = it.getString("name") ?: "",
                        imageUrl = it.getString("imageUrl") ?: "",
                        stock = (it.getLong("stock") ?: 0).toInt(),
                        reorderThreshold = (it.getLong("reorderThreshold") ?: 5).toInt()
                    )
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    suspend fun restockItem(vendorId: String, itemId: String, qty: Int, costPrice: Double) {
        val ref = db.collection("vendors")
            .document(vendorId)
            .collection("inventory")
            .document(itemId)

        db.runTransaction { tx ->
            val snap = tx.get(ref)

            val currentStock = (snap.getLong("stock") ?: 0).toInt()
            val newStock = currentStock + qty

            tx.update(ref, mapOf(
                "stock" to newStock,
                "price" to costPrice
            ))
        }.await()
    }

    suspend fun addInventoryItem(
        vendorId: String,
        name: String,
        stock: Int,
        price: Double,
        category: String?,
        receiptId: String
    ) {
        val data = mapOf(
            "name" to name,
            "stock" to stock,
            "price" to price,
            "category" to category,
            "sourceReceiptId" to receiptId,
            "lastRestocked" to FieldValue.serverTimestamp()
        )

        db.collection("vendors")
            .document(vendorId)
            .collection("inventory")
            .add(data)
            .await()
    }

    fun getLowStockItems(vendorId: String): Flow<List<InventoryItem>> =
        getInventory(vendorId).map { items ->
            items.filter { it.stock <= it.reorderThreshold }
        }

    fun productsFlow(vendorId: String): Flow<List<Product>> = callbackFlow {
        val colRef = db.collection("vendors")
            .document(vendorId)
            .collection("products")
        val listener = colRef.addSnapshotListener { snap, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snap?.documents
                ?.mapNotNull { it.toObject(Product::class.java) }
                ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun deleteInventoryItem(vendorId: String, itemId: String) {
        db.collection("vendors")
            .document(vendorId)
            .collection("inventory")
            .document(itemId)
            .delete()
            .await()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun recordSale(vendorId: String, item: InventoryItem, qty: Int, price: Double = 0.0) {
        item.sellingPrice = price
        item.costPrice = item.price

        val revenue = item.sellingPrice * qty
        val cost = item.costPrice * qty
        val profit = revenue - cost

        item.price = price

        val amount = item.price * qty
        val saleData = mapOf(
            "item" to item.name,
            "vendorId" to vendorId,
            "itemId" to item.id,
            "quantity" to qty,
            "amount" to amount,
            "timestamp" to Timestamp.now(),
            "costPrice" to item.costPrice,
            "sellingPrice" to item.sellingPrice,
            "profit" to profit
        )

        db.collection("vendors")
            .document(vendorId)
            .collection("sales")
            .add(saleData)
            .await()

        db.collection("vendors")
            .document(vendorId)
            .collection("inventory")
            .document(item.id)
            .update(
                "stock", FieldValue.increment(-qty.toLong()),
                "sold", FieldValue.increment(qty.toLong()),
                "name", item.name,
                "price", item.price,
                "sourceReceiptId", vendorId + System.currentTimeMillis(),
                "category", item.category,
            )

        // Update analytics
        val analyticsRef = db.collection("analytics").document(vendorId)
        analyticsRef.update(
            "totalRevenue", FieldValue.increment(revenue),
            "totalProfit", FieldValue.increment(profit),
            "dailySales.${todayKey()}", FieldValue.increment(amount.toDouble()),
            "itemsSold", FieldValue.increment(qty.toLong()),
            "lastUpdated", FieldValue.serverTimestamp()
        ).await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodaysProfit(vendorId: String): Double {
        val today = LocalDate.now().toString() // e.g. "2025-11-22"

        val startOfDay = com.google.firebase.Timestamp(
            java.util.Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
        )

        // tomorrow 00:00 for upper bound
        val endOfDay = com.google.firebase.Timestamp(
            java.util.Date.from(LocalDate.now().plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
        )

        val querySnapshot = db.collection("vendors")
            .document(vendorId)
            .collection("sales")
            .whereGreaterThanOrEqualTo("timestamp", startOfDay)
            .whereLessThan("timestamp", endOfDay)
            .get()
            .await()

        var totalProfit = 0.0
        for (doc in querySnapshot.documents) {
            val profit = doc.getDouble("profit") ?: 0.0
            totalProfit += profit
        }

        return totalProfit
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun todayKey(): String {
        val today = LocalDate.now()
        return today.toString()  // "2025-11-21"
    }

}
