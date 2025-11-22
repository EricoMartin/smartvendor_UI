package com.basebox.smartvendor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basebox.smartvendor.data.local.model.Vendor
import com.basebox.smartvendor.data.repository.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorViewModel @Inject constructor(
    private val repository: VendorRepository
) : ViewModel() {

    private val _vendorState = MutableStateFlow<Vendor?>(null)
    val vendorState = _vendorState.asStateFlow()

    fun loadVendor(vendorId: String) {
        viewModelScope.launch {
            val vendor = repository.getVendor(vendorId)
            _vendorState.value = vendor
        }
    }
}
