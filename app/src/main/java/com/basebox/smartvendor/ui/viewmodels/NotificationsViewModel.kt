package com.basebox.smartvendor.ui.viewmodels

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basebox.smartvendor.data.local.model.NotificationItem
import com.basebox.smartvendor.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    val myVendorId = FirebaseAuth.getInstance().currentUser?.uid


    init {
        viewModelScope.launch {
            repository.syncNotifications(myVendorId ?: "")
        }
    }
    fun loadNotifications(vendorId: String) {
        viewModelScope.launch {
            repository.getNotifications(vendorId).collect { notificationList ->
                _notifications.value = notificationList
            }
        }
    }
}
