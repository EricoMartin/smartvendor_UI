package com.basebox.smartvendor.ui.screens.dashboard

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.basebox.smartvendor.data.local.model.Receipt
import com.basebox.smartvendor.data.local.model.ReceiptItem
import com.basebox.smartvendor.data.local.model.Vendor
import com.basebox.smartvendor.ui.screens.components.InfoCard
import com.basebox.smartvendor.ui.screens.components.InfoRow
import com.basebox.smartvendor.ui.viewmodels.AuthViewModel
import com.basebox.smartvendor.ui.viewmodels.HomeViewModel
import com.basebox.smartvendor.ui.viewmodels.InventoryViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    viewModel: HomeViewModel,
    vendor: Vendor?,
    vendorId: String,
    authViewModel: AuthViewModel,
    inventoryViewModel: InventoryViewModel,
    navController: NavController
) {

    val receiptsFromDb by viewModel.receipts.collectAsState()
    val dashboard by viewModel.dashboardState.collectAsState()
    val lowStock by inventoryViewModel.lowStockItems.collectAsState()
    val products by inventoryViewModel.products.collectAsState()
    var showReceipts by remember { mutableStateOf(false) }


    LaunchedEffect(vendorId) {
        inventoryViewModel.loadTodaysProfit()
        viewModel.loadDashboard(vendorId)
        inventoryViewModel.productsFlow()
        viewModel.loadReceipts(vendorId)
    }

    val receipts = receiptsFromDb.map { (id, map) ->
        Log.d("SV", "Receipt map: $map")
        Receipt(
            id = id,
            vendorId = map["vendorId"] as? String ?: "",
            imageUrl = map["imageUrl"] as? String ?: "",
            totalAmount = (map["totalAmount"] as? Number)?.toDouble() ?: 0.0,
            date = map["date"] as? Timestamp ?: Timestamp.now(),
            items = (map["items"] as? List<Map<String, Any>>)?.map {
                ReceiptItem(
                    item = cleanText(it["item"] as? String ?: ""),
                    quantity = (it["quantity"] as? Number)?.toInt() ?: 0,
                    price = (it["price"] as? Number)?.toDouble() ?: 0.0
                )
            } ?: emptyList(), // You may need to parse this as well if needed
            rawText =  cleanText(map["rawText"] as? String ?: ""),
            aiExtracted = map["aiExtracted"] as? Boolean ?: false
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (showReceipts) {
            ReceiptsList(navController = navController, vendorId = vendorId, receipts = receipts) {
                showReceipts = false
            }
        } else {
            Text(
                text = "Hi,\n${vendor?.businessName}!",
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Today's sales", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Items sold", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoCard(
                            title = "₦${dashboard?.dailySales?.get(todayKey()) ?: 0}",
                            modifier = Modifier.weight(1f)
                        )
                        Log.d("SV", "Daily sales: ${dashboard?.dailySales?.values?.sum() ?: 0}")
                        InfoCard(title = "${dashboard?.itemsSold?.toString() ?: 0}", modifier = Modifier.weight(0.5f))
                        Log.d("SV", "Items sold: ${dashboard?.itemsSold?.toString() ?: 0}")
                        Log.d("SV", "Items available: ${dashboard?.totalAvailable}")
//                        Log.d("SV", "Items sold: ${dashboard!!}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Profit", value = "₦${String.format(Locale.getDefault(), "%.2f", (inventoryViewModel.todaysProfit.doubleValue))}")
            Log.d("SV", "Profit: ${dashboard?.totalProfit}")


            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Low-stock Items", value = "${lowStock.size}")

            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Available Items", value = "${dashboard?.totalAvailable ?: 0}")
            Log.d("SV", "Available Items: ${dashboard?.totalAvailable ?: 0}")


            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Intelligence", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = dashboard?.aiSuggestion ?: "No insights yet")
                    Log.d("SV", "${dashboard?.aiSuggestion}")
                }
            }

            Spacer(modifier = Modifier.weight(1f).height(8.dp))

            Button(
                onClick = { showReceipts = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("View Receipts", fontSize = 16.sp, color = Color.White)
            }
//            Spacer(modifier = Modifier.weight(1f).height(148.dp))
        }
    }
}

@Composable
fun ReceiptsList(
    navController: NavController,
    vendorId: String,
    receipts: List<Receipt>,
    onBack: () -> Unit
) {
    Column {
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50) // Set the background color to green
        )) {
            Text("Back to Dashboard")
        }
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(receipts) { receipt ->
                ReceiptCard(receipt = receipt) {
                    navController.navigate("receipt/${receipt.id}")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ReceiptCard(receipt: Receipt, onClick: () -> Unit) {
    val dateFormat = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Receipt #${receipt.id.take(6)}...", fontWeight = FontWeight.Bold)
                Text(text = dateFormat.format(receipt.date!!.toDate()), style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "₦${receipt.totalAmount}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun cleanText(input: String): String {
    return input
        .replace("\n", "")   // remove line breaks
        .replace("\r", "")
        .replace("\\s+".toRegex(), "") // collapse multiple spaces
        .trim()
}

@RequiresApi(Build.VERSION_CODES.O)
fun todayKey(): String {
    val today = LocalDate.now()
    return today.toString()  // "2025-11-21"
}

