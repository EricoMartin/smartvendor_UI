package com.basebox.smartvendor.ui.screens.stocks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.basebox.smartvendor.data.local.model.InventoryItem
import com.basebox.smartvendor.ui.screens.components.AddStockModal
import com.basebox.smartvendor.ui.screens.components.InventoryCard
import com.basebox.smartvendor.ui.screens.components.ReorderSuggestionsBanner
import com.basebox.smartvendor.ui.screens.components.RestockDialog
import com.basebox.smartvendor.ui.screens.components.SalesDialog
import com.basebox.smartvendor.ui.screens.components.SearchBar
import com.basebox.smartvendor.ui.viewmodels.InventoryViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StocksScreen(
    inventoryViewModel: InventoryViewModel = hiltViewModel()
) {
    val inventoryItems by inventoryViewModel.inventory.collectAsState()
    val lowStockItems by inventoryViewModel.lowStockItems.collectAsState()
    val newItem by inventoryViewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var search by remember { mutableStateOf("") }
    var showRestockDialog by remember { mutableStateOf(false) }
    var showSaleDialog by remember { mutableStateOf(false) }
    var selectedItemForRestock by remember { mutableStateOf<InventoryItem?>(null) }
    var showAddStock by remember { mutableStateOf(false) }
    var showSaleSuccessDialog by remember { mutableStateOf(false) }

    // Only check `showAddStock` to display the modal.
    if (showAddStock) {
        AddStockModal(
            item = newItem, // Pass state from ViewModel
            onItemChange = { inventoryViewModel.onNewItemChange(it) }, // Notify ViewModel of changes
            onDismiss = { showAddStock = false },
            onSave = { // Simplified and correct save logic
                inventoryViewModel.saveItem {
                    showAddStock = false
                }
            }
        )
    }

    if (showSaleSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSaleSuccessDialog = false },
            title = { Text("Success") },
            text = { Text("The sale was recorded successfully.") },
            confirmButton = {
                TextButton(onClick = { showSaleSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showSaleDialog && selectedItemForRestock != null) {
        SalesDialog(
            item = selectedItemForRestock!!, // Pass state from ViewModel // Notify ViewModel of changes
            onDismiss = {
                showSaleDialog = false
                selectedItemForRestock = null
            },
            onSaveSale = { quantity, pricePerUnit ->
                // Construct the item to be saved here
                inventoryViewModel.recordSale(selectedItemForRestock!!, quantity, pricePerUnit)
                showSaleDialog = false
                showSaleSuccessDialog = true
            }
        )

    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    inventoryViewModel.resetNewItemState() // Reset state in ViewModel
                    showAddStock = true
                },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("Stocks", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            // 🔍 Search Field
            SearchBar(search = search, onChange = { search = it })

            Spacer(Modifier.height(12.dp))

            // 🟩 Reorder Suggestions Banner
            ReorderSuggestionsBanner(
                suggestedCount = lowStockItems.size,
                onClick = { /* Navigate to suggestions */ }
            )

            Spacer(Modifier.height(16.dp))

            // 📦 Inventory List
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(inventoryItems.filter {
                    it.name.contains(search, ignoreCase = true)
                }) { item ->
                    InventoryCard(
                        item = item,
                        onRestockClick = {
                            selectedItemForRestock = item
                            showRestockDialog = true
                        },
                        onSaleClick = {
                            // Handle sale click
                            selectedItemForRestock = item
                            showSaleDialog = true
                        },
                        onDeleteClick = {
                            inventoryViewModel.deleteItem(item.id)
                        }
                    )
                }
            }
        }
    }

    // ➕ Restock Modal
    if (showRestockDialog && selectedItemForRestock != null) {
        val currentItem = selectedItemForRestock!!
        RestockDialog(
            item = currentItem,
            onDismiss = { showRestockDialog = false },
            onConfirm = { qty, costPrice ->
                scope.launch {
                    inventoryViewModel.restockItem(currentItem.id, qty, costPrice)
                }
                showRestockDialog = false
            }
        )
    }
}
