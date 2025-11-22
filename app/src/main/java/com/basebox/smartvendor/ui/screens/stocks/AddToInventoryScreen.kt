package com.basebox.smartvendor.ui.screens.stocks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.basebox.smartvendor.ui.viewmodels.InventoryViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToInventoryScreen(
    nav: NavController,
    vendorId: String,
    receiptId: String,
    itemName: String,
    quantity: Int,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    // Initialize the state with data from the route.
    // This runs once when the screen is first displayed.
    LaunchedEffect(key1 = itemName, key2 = quantity) {
        viewModel.onNewItemChange(
            uiState.copy(name = itemName, stock = quantity)
        )
    }

//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Add To Inventory") },
//                navigationIcon = {
//                    IconButton(onClick = { nav.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = null)
//                    }
//                }
//            )
//        }
//    ) { padding ->

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    null
                )
            }
            Text(
                text = "Add To Inventory",
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNewItemChange(uiState.copy(name = it)) },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = if (uiState.stock == 0) "" else uiState.stock.toString(),
            onValueChange = {
                viewModel.onNewItemChange(uiState.copy(stock = it.toIntOrNull() ?: 0))
            },
            label = { Text("Stock Quantity") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = if (uiState.price == 0.0) "" else uiState.price.toString(),
            onValueChange = {
                viewModel.onNewItemChange(uiState.copy(price = it.toDoubleOrNull() ?: 0.0))
            },
            label = { Text("Unit Price (₦)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.category ?: "", // Safely handle nullable category
            onValueChange = { viewModel.onNewItemChange(uiState.copy(category = it)) },
            label = { Text("Category (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true
                viewModel.saveItem {
                    isLoading = false
                    nav.popBackStack() // go back after saving
                }
            },
            modifier = Modifier.fillMaxWidth(),
            // Enable button only if the item has a name and is not currently saving
            enabled = uiState.name.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50) // Set the background color to green
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp))
            } else {
                Text("Add To Inventory")
            }
        }
    }
}
