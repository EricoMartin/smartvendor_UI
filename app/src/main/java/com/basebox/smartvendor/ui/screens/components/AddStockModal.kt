package com.basebox.smartvendor.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.basebox.smartvendor.data.local.model.InventoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockModal(
    item: InventoryItem,
    onItemChange: (InventoryItem) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add New Stock", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = item.name,
                onValueChange = { onItemChange(item.copy(name = it)) },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = if (item.stock == 0) "" else item.stock.toString(),
                onValueChange = { onItemChange(item.copy(stock = it.toIntOrNull() ?: 0)) },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = item.category?: "",
                onValueChange = { onItemChange(item.copy(category = it)) },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = item.costPrice.toString(),
                onValueChange = { onItemChange(item.copy(costPrice = it.toDouble())) },
                label = { Text("Cost Price") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onSave, // Correctly call the onSave lambda
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(
                    text = "Add Stock",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            OutlinedButton(
                onClick = onDismiss, // Correctly call the onDismiss lambda
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}