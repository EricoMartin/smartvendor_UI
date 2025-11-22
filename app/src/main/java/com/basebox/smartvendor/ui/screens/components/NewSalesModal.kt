package com.basebox.smartvendor.ui.screens.components

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
import com.basebox.smartvendor.data.local.model.InventoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSalesModal(
    item: InventoryItem,
//    onItemChange: (InventoryItem) -> Unit,
    onDismiss: () -> Unit,
    onSaveSale: (name: String, quantity: Int, pricePerUnit: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var quantity by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf(item.name) }

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

            if (item.name.isNotEmpty()) {
                // If item name exists, show it as static text
                Text(
                    text = "Item: $itemName",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // If item name is empty, provide a text field to enter it
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it
                    },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
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
                    if (qty > 0 && price > 0 && itemName.isNotEmpty()) {
                        onSaveSale(itemName, qty, price)
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
