package com.basebox.smartvendor.ui.screens.sales

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basebox.smartvendor.ui.screens.camera.CameraScreen
import com.basebox.smartvendor.ui.screens.components.NewSalesModal
import com.basebox.smartvendor.ui.viewmodels.HomeViewModel
import com.basebox.smartvendor.ui.viewmodels.InventoryViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SalesScreen(
    viewModel: HomeViewModel,
    inventoryViewModel: InventoryViewModel,
    vendorId: String
) {
    var openCamera by remember { mutableStateOf(false) }
    var showSaleDialog by remember { mutableStateOf(false) }
    val newItem by inventoryViewModel.state.collectAsState()
    var showReceiptSuccessDialog by remember { mutableStateOf(false) }
    var showSaleSuccessDialog by remember { mutableStateOf(false) }

    if (showReceiptSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showReceiptSuccessDialog = false },
            title = { Text("Success") },
            text = { Text("Receipt uploaded successfully and is being processed.") },
            confirmButton = {
                TextButton(onClick = { showReceiptSuccessDialog = false }) {
                    Text("OK")
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

    if (openCamera) {
        CameraScreen(
            onPhotoCaptured = { uri ->
                openCamera = false
                viewModel.apply {
                    uploadReceipt(uri, vendorId)
                }
                showReceiptSuccessDialog = true
            },
            onClose = {
                openCamera = false
            }
        )
    } else {

        if (showSaleDialog) {
            NewSalesModal(
                item = newItem, // Pass state from ViewModel // Notify ViewModel of changes
                onDismiss = {
                    showSaleDialog = false
                    inventoryViewModel.resetNewItemState()
                },
                onSaveSale = { name, quantity, pricePerUnit ->
                    // Construct the item to be saved here
                    val saleItem = newItem.copy(name = name)
                    inventoryViewModel.recordSale(saleItem, quantity, pricePerUnit)
                    showSaleDialog = false
                    showSaleSuccessDialog = true
                }
            )

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Receipt Scan", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { openCamera = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50))
                    ) {
                        Text("Upload Receipt")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chat bubbles
//            Text(
//                "What's up on Ada's shop this week",
//                modifier = Modifier.align(Alignment.End),
//                color = Color.Gray
//            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.align(Alignment.Start)
            ) {
//                Text(
//                    text = "Your profit this week is ₦32,500 — up 12% from last week.",
//                    modifier = Modifier.padding(12.dp)
//                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom input actions
//            ChatInputButton(text = "Show low-stock items", icon = Icons.Default.Mic)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    // Handle sale click
                    showSaleDialog = true
                }, // Correctly call the onDismiss lambda
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Manual Sale",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun ChatInputButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OutlinedButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text, modifier = Modifier.weight(1f))
        Icon(icon, contentDescription = null)
    }
}
