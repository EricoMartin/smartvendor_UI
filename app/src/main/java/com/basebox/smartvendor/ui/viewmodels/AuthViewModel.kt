package com.basebox.smartvendor.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.basebox.smartvendor.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {



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
                                onSuccess()
                            } else {
                                onError("Vendor profile not found. Contact support.")
                            }
                        }
                        .addOnFailureListener { e -> onError(e.message ?: "Firestore error") }
                }
                .addOnFailureListener { e -> onError(e.message ?: "Invalid login credentials") }
        }

    fun signOut() {
        authRepository.signOut()
    }
}
