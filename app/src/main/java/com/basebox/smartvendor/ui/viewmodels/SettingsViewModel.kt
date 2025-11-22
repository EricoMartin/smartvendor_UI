package com.basebox.smartvendor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basebox.smartvendor.data.local.model.Settings
import com.basebox.smartvendor.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val settingsState: StateFlow<Settings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, Settings())

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            repository.updateLanguage(language)
        }
    }

    fun toggleSync(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSync(enabled)
        }
    }

    fun exportData() {
        viewModelScope.launch {
            val success = repository.exportData()
            // Optional: Show toast/snackbar if needed
        }
    }
}
