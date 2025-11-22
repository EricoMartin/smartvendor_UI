package com.basebox.smartvendor.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun getVendorId(): String {
        return auth.currentUser?.uid ?: ""
    }

    suspend fun getAnalytics(vendorId: String): DashboardAnalytics? {
        val docRef = db.collection("analytics").document(vendorId)
        val doc = docRef.get().await()

        if (!doc.exists()) {
            val defaultAnalytics = DashboardAnalytics(
                totalRevenue = 0.0,
                dailySales = emptyMap(),
                topItems = emptyList(),
                aiSuggestion = "No insights yet",
                itemsSold = 0,
                totalAvailable = 0,
                totalProfit = 0.0,
            )

            docRef.set(defaultAnalytics, SetOptions.merge()).await()
            return defaultAnalytics
        }

        return doc.toObject(DashboardAnalytics::class.java)!!
    }
}

data class DashboardAnalytics(
    val totalRevenue: Double = 0.0,
    val dailySales: Map<String, Double> = emptyMap(),
    val topItems: List<TopItem> = emptyList(),
    val aiSuggestion: String = "",
    val itemsSold: Long = 0,
    val totalProfit: Double = 0.0,
    val totalAvailable: Long = 0,
)

data class TopItem(
    val name: String = "",
    val count: Int = 0
)
