package com.basebox.smartvendor.ui.screens

//import com.basebox.smartvendor.ui.screens.stocks.AddToInventoryScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basebox.smartvendor.ui.screens.components.BottomNavigationBar
import com.basebox.smartvendor.ui.screens.dashboard.DashboardScreen
import com.basebox.smartvendor.ui.screens.insights.InsightsScreen
import com.basebox.smartvendor.ui.screens.notifications.NotificationsScreen
import com.basebox.smartvendor.ui.screens.profile.ProfileScreen
import com.basebox.smartvendor.ui.screens.receipts.ReceiptDetailsScreen
import com.basebox.smartvendor.ui.screens.sales.SalesScreen
import com.basebox.smartvendor.ui.screens.settings.SettingsScreen
import com.basebox.smartvendor.ui.screens.stocks.AddToInventoryScreen
import com.basebox.smartvendor.ui.screens.stocks.StocksScreen
import com.basebox.smartvendor.ui.viewmodels.AuthViewModel
import com.basebox.smartvendor.ui.viewmodels.HomeViewModel
import com.basebox.smartvendor.ui.viewmodels.InventoryViewModel
import com.basebox.smartvendor.ui.viewmodels.NotificationsViewModel
import com.basebox.smartvendor.ui.viewmodels.SettingsViewModel
import com.basebox.smartvendor.ui.viewmodels.VendorViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    vendorId: String,
    vendorViewModel: VendorViewModel,
    authViewModel: AuthViewModel,
    inventoryViewModel: InventoryViewModel,
    notificationsViewModel: NotificationsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()

    val vendor by vendorViewModel.vendorState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(vendorId) {
        vendorViewModel.loadVendor(vendorId)
    }
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .height(60.dp)
                    .background(Color(0xFF4CAF50))
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                DashboardScreen(
                    viewModel,
                    vendor,
                    vendorId,
                    authViewModel,
                    inventoryViewModel,
                    navController
                )
            }
            composable("sales") {
                SalesScreen(viewModel, inventoryViewModel, vendorId)
            }
            composable("insights") {
                InsightsScreen()
            }
            composable("stock") {
                StocksScreen(inventoryViewModel)
            }

            composable("Profile") {

                val v = vendor!!
                ProfileScreen(navController, v.fullName, v.email, v.phone, v.businessName)
            }
            composable("receipt/{receiptId}") { backStackEntry ->
                val receiptId = backStackEntry.arguments?.getString("receiptId") ?: ""
                ReceiptDetailsScreen(navController, vendorId, receiptId)
            }
            composable("addInventory/{vendorId}/{receiptId}/{name}/{qty}") {
                AddToInventoryScreen(
                    nav = navController,
                    vendorId = it.arguments?.getString("vendorId")!!,
                    receiptId = it.arguments?.getString("receiptId")!!,
                    itemName = it.arguments?.getString("name")!!,
                    quantity = it.arguments?.getString("qty")!!.toInt()
                )
            }

            composable("notifications") { NotificationsScreen(navController, notificationsViewModel) }
            composable("settings") { SettingsScreen(settingsViewModel, authViewModel) }
        }
    }
}
