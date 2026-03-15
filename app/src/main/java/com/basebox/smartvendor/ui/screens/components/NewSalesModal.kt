package com.basebox.smartvendor.ui.screens.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.basebox.smartvendor.data.local.model.InventoryDraft
import com.basebox.smartvendor.data.local.model.InventoryItem
import com.basebox.smartvendor.ui.viewmodels.InventoryViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSalesModal(
    item: InventoryItem,
    onDismiss: () -> Unit,
    onSaveSale: (inventoryItem: InventoryItem, name: String, quantity: Int, pricePerUnit: Double) -> Unit,
    inventoryViewModel: InventoryViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var quantity by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }

    // Load inventory items
    val inventoryItems by inventoryViewModel.inventory.collectAsState()

    // For dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedItemName by remember { mutableStateOf(item.name) }
    var itemId by remember { mutableStateOf(item.id)}

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Record Sale",
                style = MaterialTheme.typography.headlineSmall
            )

            // 👉 If item name provided (e.g. clicked from the list), show static
            if (item.name.isNotEmpty()) {
                Text(
                    text = "Item: ${item.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // 👉 Dropdown menu to select inventory item
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedItemName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Item") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        inventoryItems.forEach { inv ->
                            DropdownMenuItem(
                                text = { Text(inv.name) },
                                onClick = {
                                    selectedItemName = inv.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Quantity Sold") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pricePerUnit,
                onValueChange = { pricePerUnit = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Selling Price (per unit)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: 0
                    val price = pricePerUnit.toDoubleOrNull() ?: 0.0
                    val newItem = InventoryItem()
                    Log.d("SV", "Item ID: $itemId")
                    Log.d("SV", "Inventory Items: $inventoryItems")
                    newItem.name = selectedItemName

                    for (items in inventoryItems) {
                        if (items.name == newItem.name){
                            newItem.id = items.id
                            newItem.stock = items.stock - qty
                            newItem.price = items.price
                            newItem.category = items.category
                            newItem.receiptId = items.receiptId
                            newItem.costPrice = items.costPrice
                            newItem.sellingPrice = items.sellingPrice
                            newItem.isSynced = items.isSynced
                        }
                    }
                    if (qty > 0 && price > 0 && selectedItemName.isNotEmpty()) {
                        onSaveSale(newItem, selectedItemName, qty, price)
                        Log.d("SV", "My Inventory Item: $newItem")
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Save Sale", color = Color.White)
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
