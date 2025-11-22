package com.basebox.smartvendor.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.basebox.smartvendor.data.local.model.InventoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.basebox.smartvendor.data.repository.InventoryRepository


@HiltViewModel
class StockViewModel @Inject constructor(
    private val repo: InventoryRepository
) : ViewModel() {

    private val _inventoryState = MutableStateFlow(InventoryState())
    val inventoryState = _inventoryState

    suspend fun loadInventory(vendorId: String) {
        val items = repo.getInventory(vendorId)
        items.collect { it ->
            _inventoryState.value = _inventoryState.value.copy(items = it)
        }
    }

    suspend fun restockItem(vendorId: String, itemId: String, qty: Int, costPrice: Double) {
        repo.restockItem(vendorId, itemId, qty, costPrice)
        loadInventory(vendorId)
    }
}

data class InventoryState(
    val items: List<InventoryItem> = emptyList(),
    val reorderSuggestions: List<InventoryItem> = emptyList()
)
