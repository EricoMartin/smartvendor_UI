package com.basebox.smartvendor.ui.screens.notifications

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.basebox.smartvendor.ui.screens.components.NotificationCard
import com.basebox.smartvendor.ui.viewmodels.NotificationsViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationsScreen(
    nav: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val vendorId = Firebase.auth.currentUser!!.uid

    LaunchedEffect(vendorId) {
        viewModel.loadNotifications(vendorId)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Notifications", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        if (notifications.isEmpty()) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(notifications) { notification ->
                    NotificationCard(notification = notification)
                }
            }
        }
    }
}
