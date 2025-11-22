package com.basebox.smartvendor.data.repository

import com.basebox.smartvendor.data.local.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun addNotification(vendorId: String, notification: NotificationItem) {
        val docRef = firestore.collection("vendors").document(vendorId)
            .collection("notifications").document()
        firestore.collection("vendors").document(vendorId)
            .collection("notifications")
            .document(docRef.id)
            .set(notification.copy(id = docRef.id))
            .await()
    }

    fun getNotifications(vendorId: String): Flow<List<NotificationItem>> = flow {
        val querySnapshot = firestore.collection("vendors").document(vendorId)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
        val notifications = querySnapshot.toObjects(NotificationItem::class.java)
        emit(notifications)
    }
}
