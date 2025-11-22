package com.basebox.smartvendor.ui.screens.receipts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.basebox.smartvendor.ui.viewmodels.ReceiptViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptDetailsScreen(
    nav: NavController,
    vendorId: String,
    receiptId: String,
    viewModel: ReceiptViewModel = hiltViewModel()
) {
    val state by viewModel.receiptState.collectAsState()
    val scrollState = rememberScrollState()
    LaunchedEffect(receiptId) { viewModel.loadReceipt(vendorId, receiptId) }

//    Scaffold(topBar = {
//        TopAppBar(title = { Text("Receipt Details") }, navigationIcon = {
//            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
//        })
//    },
////        floatingActionButton = {
////        FloatingActionButton(onClick = { viewModel.saveReceiptEdits() }) { Text("Save") }
////    }
//    ) { padding ->

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .padding(16.dp)
    ) {
        state?.let { r ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        null
                    )
                }
                Text(
                    text = "Receipt Details",
                    fontSize = 28.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            AsyncImage(
                model = r.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Total: ₦${r.totalAmount ?: "—"}",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Items",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(6.dp))
            LazyColumn {
                itemsIndexed(r.items) { idx, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                item.item,
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Qty: ${item.quantity}  •  ₦${item.price}",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        IconButton(onClick = { viewModel.removeItem(idx) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), 2.dp, Color(0xFF4CAF50))

                }
            }
            Spacer(Modifier.height(12.dp))
            if (r.items.isNotEmpty()) {
                Button(
                    onClick = {
                        nav.navigate("addInventory/${vendorId}/${receiptId}/${r.items.first().item}/${r.items.first().quantity}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50) // Set the background color to green
                    )
                ) {
                    Text("Add to Inventory")
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { viewModel.reparseWithAI() }) { Text("Re-parse with AI") }
        } ?: Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
        Spacer(Modifier.height(148.dp))
    }

}

