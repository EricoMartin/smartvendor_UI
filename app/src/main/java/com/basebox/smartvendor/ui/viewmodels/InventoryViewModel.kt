package com.basebox.smartvendor.ui.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basebox.smartvendor.data.local.model.InventoryItem
import com.basebox.smartvendor.data.local.model.NotificationItem
import com.basebox.smartvendor.data.local.model.Product
import com.basebox.smartvendor.data.repository.InventoryRepository
import com.basebox.smartvendor.data.repository.NotificationRepository
import com.basebox.smartvendor.data.services.NotificationService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repo: InventoryRepository,
    private val notificationRepository: NotificationRepository,
    private val notificationService: NotificationService
) : ViewModel() {


    private fun requireUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }
        val vendorId = requireUserId() ?: ""

    private val _todaysProfit = mutableDoubleStateOf(0.0)
    val todaysProfit = _todaysProfit
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _state = MutableStateFlow(InventoryItem())
    val state: StateFlow<InventoryItem> = _state.asStateFlow()

    fun onNewItemChange(item: InventoryItem) {
        _state.value = item
    }

    fun resetNewItemState() {
        _state.value = InventoryItem()
    }

    fun loadTodaysProfit() {
        viewModelScope.launch {
            try {
                _todaysProfit.doubleValue = repo.getTodaysProfit(vendorId)
            } catch (e: Exception) {
                Log.e("SV", "Error loading today's profit", e)
            }
        }
    }

    fun saveItem(onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                val item = _state.value
                repo.addInventoryItem(
                    vendorId = vendorId,
                    name = item.name,
                    stock = item.stock,
                    price = item.price,
                    category = item.category,
                    receiptId = "" // No receipt for manually added items
                )
                resetNewItemState()
                onDone()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun restockItem(itemId: String, quantity: Int, costPrice: Double) {
        viewModelScope.launch {
            try {
                repo.restockItem(vendorId, itemId, quantity, costPrice)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                repo.deleteInventoryItem(vendorId, itemId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun recordSale(item: InventoryItem, quantity: Int, price: Double) {
        viewModelScope.launch {
            try {
                // 1. Record the sale in the inventory repository
                repo.recordSale(vendorId, item, quantity, price)

                // 2. Create and save the notification to Firestore
                val notification = NotificationItem(
                    title = "New Sale! 🤑",
                    body = "You just sold $quantity unit(s) of ${item.name} for ₦${price * quantity}.",
                    type = "SALE"
                )
                notificationRepository.addNotification(vendorId, notification)

                // 3. Show a system notification
                notificationService.showSaleNotification(item.name, quantity)

            } catch (e: Exception) {
                Log.d("SV", "Error recording sale: $e")
                // Handle error
            }
        }
    }

    val inventory: StateFlow<List<InventoryItem>> = repo.getInventory(vendorId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // For "items that need restocking"
    val lowStockItems: StateFlow<List<InventoryItem>> = inventory.map { list ->
        list.filter { it.stock <= it.reorderThreshold }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun productsFlow() {
        viewModelScope.launch {
            repo.productsFlow(vendorId).collect {
                _products.value = it
            }
        }
    }
}
