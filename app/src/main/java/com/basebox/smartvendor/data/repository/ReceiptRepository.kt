package com.basebox.smartvendor.data.repository

import android.net.Uri
import android.util.Log
import com.basebox.smartvendor.data.local.model.Receipt
import com.basebox.smartvendor.data.local.model.ReceiptItem
import com.basebox.smartvendor.data.remote.api.ReceiptAPI
import com.basebox.smartvendor.data.remote.model.ParseRequest
import com.basebox.smartvendor.data.remote.model.ParseResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import java.io.File
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ReceiptRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val api: ReceiptAPI
) {
    suspend fun saveReceipt(vendorId: String) {
        val receipt = mapOf(
            "vendorId" to vendorId,
            "imageUrl" to "",
            "totalAmount" to 1000,
            "date" to FieldValue.serverTimestamp(),
            "aiExtracted" to false,
            "items" to listOf(
                mapOf("item" to "Test Item", "quantity" to 2, "price" to 500)
            )
        )
        db.collection("receipts").add(receipt).await()
    }

    suspend fun readReceipts(vendorId: String): List<Pair<String, Map<String, Any>>> {
        return db.collection("receipts")
            .whereEqualTo("vendorId", vendorId)
            .get()
            .await()
            .documents.mapNotNull { it.id to (it.data?: emptyMap()) }
    }

    suspend fun uploadReceiptImage(uri: Uri, vendorId: String): String {
        Log.d("UPLOAD", "URI to upload: $uri")
        val file = File(uri.path ?: "")
        Log.d("UPLOAD", "File exists: ${file.exists()} Size: ${file.length()}")
        val ref = storage.getReference("receipts/$vendorId/${UUID.randomUUID()}.jpg")
        ref.putFile(uri).await()
        parseReceipt(ref.downloadUrl.await().toString(), vendorId)
        return ref.downloadUrl.await().toString()
    }

    suspend fun parseReceipt(downloadUrl: String, vendorId: String): Response<ParseResponse> {
        val request = ParseRequest(
            imageUrl = downloadUrl,
            vendorId = vendorId,
            date = Date().toString()
        )
        return api.parseReceipt(request)
    }

    // -------------------------------------------------------------------------
    // RE-PARSE A RECEIPT AGAIN USING GEMINI (Cloud Run)
    // -------------------------------------------------------------------------
    suspend fun reparseWithAI(receipt: Receipt): Response<ParseResponse> {
        val request = ParseRequest(
            imageUrl = receipt.imageUrl,
            vendorId = receipt.vendorId,
            date = Date().toString()
        )
        return api.parseReceipt(request)
    }

    // -------------------------------------------------------------------------
    // ADD RECEIPT ITEMS TO INVENTORY
    // (Creates items in `inventory/{vendorId}/items`)
    // -------------------------------------------------------------------------
    suspend fun addToInventory(vendorId: String, items: List<ReceiptItem>) {
        val invRef = db.collection("inventory").document(vendorId)

        items.forEach { item ->
            val data = mapOf(
                "name" to item.item,
                "quantity" to item.quantity,
                "price" to item.price,
                "updatedAt" to Date()
            )
            invRef.collection("items").add(data).await()
        }
    }

    suspend fun saveReceiptEdits(receipt: Receipt) {
        val docData = mapOf(
            "totalAmount" to receipt.totalAmount,
            "items" to receipt.items.map { item ->
                mapOf(
                    "name" to item.item,
                    "quantity" to item.quantity,
                    "price" to item.price
                )
            }
        )

        db.collection("receipts")
            .document(receipt.id!!)
            .set(docData, SetOptions.merge())
            .await()
    }

    // -------------------------------------------------------------------------
    // REMOVE ONE ITEM FROM THE ITEMS LIST
    // -------------------------------------------------------------------------
    suspend fun removeItem(receipt: Receipt, index: Int) {
        val updatedItems = receipt.items.toMutableList()
        updatedItems.removeAt(index)

        db.collection("receipts")
            .document(receipt.id!!)
            .update("items", updatedItems)
            .await()
    }

}