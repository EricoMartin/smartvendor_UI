package com.basebox.smartvendor.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.basebox.smartvendor.data.local.model.Receipt
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class ReceiptUiState {
    object Idle : ReceiptUiState()
    object Loading : ReceiptUiState()
    data class Success(val receipts: List<Receipt>) : ReceiptUiState()
    data class Error(val message: String) : ReceiptUiState()
    object Saved : ReceiptUiState()
    object Deleted : ReceiptUiState()
}

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _receiptState = MutableStateFlow<Receipt?>(null)
    val receiptState = _receiptState.asStateFlow()

    private var vendorId: String = ""
    private var receiptId: String = ""

    // ---------------------------------------------------------
    // LOAD RECEIPT
    // ---------------------------------------------------------
    fun loadReceipt(vendorId: String, receiptId: String) {
        this.vendorId = vendorId
        this.receiptId = receiptId

        firestore.collection("receipts")
            .document(receiptId)
            .addSnapshotListener { snap, error ->
                if (error != null) return@addSnapshotListener
                if (snap != null && snap.exists()) {
                    _receiptState.value = snap.toObject(Receipt::class.java)
                        ?.copy(id = snap.id)
                }
            }
    }

    // ---------------------------------------------------------
    // REMOVE ITEM FROM LIST
    // ---------------------------------------------------------
    fun removeItem(index: Int) {
        val current = _receiptState.value ?: return
        val updatedItems = current.items.toMutableList().apply { removeAt(index) }

        _receiptState.value = current.copy(items = updatedItems)
    }

    // ---------------------------------------------------------
    // SAVE EDITS BACK TO FIRESTORE
    // ---------------------------------------------------------
    fun saveReceiptEdits() {
        val r = _receiptState.value ?: return

        firestore.collection("receipts")
            .document(r.id)
            .update(
                mapOf(
                    "items" to r.items,
                    "totalAmount" to r.items.sumOf { it.price * it.quantity }
                )
            )
    }

    // ---------------------------------------------------------
    // ADD TO INVENTORY (basic stub)
    // ---------------------------------------------------------
    fun addToInventory() {
        val r = _receiptState.value ?: return

        r.items.forEach { item ->
            val ref = firestore.collection("inventory")
                .document("${r.vendorId}_${item.item}")

            ref.set(
                mapOf(
                    "name" to item.item,
                    "vendorId" to r.vendorId,
                    "quantity" to item.quantity,
                    "lastUpdated" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
        }
    }

    // ---------------------------------------------------------
    // REQUEST RE-PARSE WITH AI (stub)
    // ---------------------------------------------------------
    fun reparseWithAI() {
        val r = _receiptState.value ?: return

        // Ideally call your Cloud Run endpoint:
        // POST /parse-receipt with { imageUrl, vendorId }
        // Then update receiptState with new parsed items.

        Log.d("ReceiptViewModel", "Re-parse requested for ${r.id}")
    }
}

