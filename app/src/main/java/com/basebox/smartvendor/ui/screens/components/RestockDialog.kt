package com.basebox.smartvendor.ui.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.basebox.smartvendor.data.local.model.InventoryItem

@Composable
fun RestockDialog(
    item: InventoryItem,
    onDismiss: () -> Unit,
    onConfirm: (Int, Double) -> Unit
) {
    var qty by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restock ${item.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Quantity") }
                )

                OutlinedTextField(
                    value = costPrice,
                    onValueChange = { costPrice = it },
                    label = { Text("Cost Price") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(qty.toIntOrNull() ?: 0, costPrice.toDoubleOrNull() ?: 0.0)
                },
            ) { Text("Add item", color = Color(0xFF4CAF50)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF4CAF50)) } }
    )
}
