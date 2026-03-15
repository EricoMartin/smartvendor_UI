package com.basebox.smartvendor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basebox.smartvendor.data.datastore.UserPreferences
import com.basebox.smartvendor.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.io.path.exists

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Expose the email flow
    val lastUserEmail: Flow<String> = userPreferences.lastUserEmail

    val hasSeenOnboarding = userPreferences.hasSeenOnboarding
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()
    private val _authState = MutableStateFlow(auth.currentUser)
        val authState = _authState.asStateFlow()

        init {
            auth.addAuthStateListener { _authState.value = it.currentUser }
        }

        fun registerVendor(
            fullName: String,
            email: String,
            phone: String,
            businessName: String,
            password: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val vendorId = result.user?.uid ?: return@addOnSuccessListener

                    val vendorData = mapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "phone" to phone,
                        "businessName" to businessName,
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    db.collection("vendors").document(vendorId)
                        .set(vendorData)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onError(e.message ?: "Firestore error") }
                }
                .addOnFailureListener { e -> onError(e.message ?: "Auth error") }
        }

        fun loginVendor(
            email: String,
            password: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val vendorId = result.user?.uid ?: return@addOnSuccessListener

                    // Ensure vendor profile exists
                    db.collection("vendors").document(vendorId).get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                // 🔥 Save UID to DataStore on successful login
                                viewModelScope.launch {
                                    userPreferences.saveLoggedInUserUid(vendorId)
                                }
                                onSuccess()
                            } else {
                                onError("Vendor profile not found. Contact support.")
                            }
                        }
                        .addOnFailureListener { e -> onError(e.message ?: "Firestore error") }
                }
                .addOnFailureListener { e -> onError(e.message ?: "Invalid login credentials") }
        }

    fun saveLastUserEmail(email: String) {
        viewModelScope.launch {
            userPreferences.saveLastUserEmail(email)
        }
    }
    fun setOnboardingSeen() {
        viewModelScope.launch {
            userPreferences.setOnboardingSeen()
        }
    }
    fun signOut() {
        // 🔥 Clear the stored UID on sign out
        viewModelScope.launch {
            userPreferences.clearLoggedInUser()
        }
        authRepository.signOut()
        _isLoggedOut.value = true
    }
}
