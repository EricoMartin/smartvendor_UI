package com.basebox.smartvendor.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.basebox.smartvendor.data.local.model.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings_prefs")

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val LANGUAGE = stringPreferencesKey("language")
    private val SYNC_ENABLED = booleanPreferencesKey("sync_enabled")

    val settingsFlow: Flow<Settings> = context.dataStore.data.map { prefs ->
        Settings(
            language = prefs[LANGUAGE] ?: "English",
            syncEnabled = prefs[SYNC_ENABLED] ?: true
        )
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE] = language
        }
    }

    suspend fun updateSync(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SYNC_ENABLED] = enabled
        }
    }

    // Placeholder for CSV export logic
    suspend fun exportData(): Boolean {
        // TODO: Implement your CSV export logic (local or backend)
        return true
    }
}
