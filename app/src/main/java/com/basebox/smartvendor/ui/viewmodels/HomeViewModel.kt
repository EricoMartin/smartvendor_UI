package com.basebox.smartvendor.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basebox.smartvendor.data.repository.DashboardAnalytics
import com.basebox.smartvendor.data.repository.DashboardRepository
import com.basebox.smartvendor.data.repository.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: ReceiptRepository, private val dashRepo: DashboardRepository) : ViewModel() {
    private val _receipts = MutableStateFlow<List<Pair<String, Map<String, Any>>>>(emptyList())
    val receipts = _receipts.asStateFlow()

    private val _dashboardState = MutableStateFlow<DashboardAnalytics?>(null)
    val dashboardState = _dashboardState.asStateFlow()

    fun saveReceipt(vendorId: String) = viewModelScope.launch {
        repo.saveReceipt(vendorId)
    }

    fun loadReceipts(vendorId: String) = viewModelScope.launch(Dispatchers.IO) {
        _receipts.value = repo.readReceipts(vendorId)
    }

    fun loadDashboard(vendorId: String) {
        viewModelScope.launch {
            val analytics = dashRepo.getAnalytics(vendorId)
            _dashboardState.value = analytics
        }
    }

    fun uploadReceipt(uri: Uri, vendorId: String) = viewModelScope.launch(Dispatchers.IO) {
        repo.uploadReceiptImage(uri, vendorId)
    }
    fun parseReceipt(url: String, vendorId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = repo.parseReceipt(url, vendorId)
            if (response.isSuccessful) {
                val body = response.body()
                // Handle parsed receipt data here
                println("Parse success: $body")
            } else {
                println("Parse error: ${response.code()}")
            }
        } catch (e: Exception) {
            println("Parse call failed: ${e.message}")
        }
    }
}
