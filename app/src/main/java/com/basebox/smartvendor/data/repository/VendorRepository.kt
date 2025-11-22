package com.basebox.smartvendor.data.repository

import com.basebox.smartvendor.data.local.model.Vendor
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VendorRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getVendor(vendorId: String): Vendor? {
        return try {
            val doc = firestore.collection("vendors")
                .document(vendorId)
                .get()
                .await()

            doc.toObject(Vendor::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
