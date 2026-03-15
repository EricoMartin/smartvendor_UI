package com.basebox.smartvendor.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        private val LAST_USER_EMAIL = stringPreferencesKey("last_user_email")
        private val LOGGED_IN_USER_UID = stringPreferencesKey("logged_in_user_uid")
    }

    // 🔥 Read the last user's UID
    val loggedInUserUid: Flow<String?> =
        context.userPrefsDataStore.data.map { prefs ->
            prefs[LOGGED_IN_USER_UID]
        }

    // 🔥 Read the last user's email
    val lastUserEmail: Flow<String> =
        context.userPrefsDataStore.data.map { prefs ->
            prefs[LAST_USER_EMAIL] ?: ""
        }

    // 🔥 Read the onboarding state as a Flow<Boolean>
    val hasSeenOnboarding: Flow<Boolean> =
        context.userPrefsDataStore.data.map { prefs ->
            prefs[HAS_SEEN_ONBOARDING] ?: false
        }

    // 🔥 Save that onboarding has been completed
    suspend fun setOnboardingSeen() {
        context.userPrefsDataStore.edit { prefs ->
            prefs[HAS_SEEN_ONBOARDING] = true
        }
    }
    // 🔥 Save the last user's email
    suspend fun saveLastUserEmail(email: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[LAST_USER_EMAIL] = email
        }
    }

    // 🔥 Save the last logged-in user's UID
    suspend fun saveLoggedInUserUid(uid: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[LOGGED_IN_USER_UID] = uid
        }
    }

    // 🔥 Clear the logged-in user's UID (on logout)
    suspend fun clearLoggedInUser() {
        context.userPrefsDataStore.edit { prefs ->
            prefs.remove(LOGGED_IN_USER_UID)
            // Optional: also clear the last email if desired
            // prefs.remove(LAST_USER_EMAIL)
        }
    }
}