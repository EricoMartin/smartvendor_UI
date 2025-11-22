package com.basebox.smartvendor.data.repository

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    fun signOut() {
        firebaseAuth.signOut()
    }
}
