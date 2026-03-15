package com.basebox.smartvendor.data.repository

import android.util.Log
import com.basebox.smartvendor.data.local.dao.NotificationDao
import com.basebox.smartvendor.data.local.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val firestore: FirebaseFirestore, private val notificationDao: NotificationDao) {

    suspend fun addNotification(vendorId: String, notification: NotificationItem) {
        val docRef = firestore.collection("vendors").document(vendorId)
            .collection("notifications").document()

        firestore.collection("vendors").document(vendorId)
            .collection("notifications")
            .document(docRef.id)
            .set(notification.copy(id = docRef.id))
            .await()

        notificationDao.insertNotification(notification.copy(id = docRef.id))

    }

    fun getNotifications(vendorId: String): Flow<List<NotificationItem>> =
        notificationDao.getAllNotifications()

    suspend fun syncNotifications(vendorId: String) {
        val snapshot = firestore.collection("vendors")
            .document(vendorId)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        snapshot.toObjects(NotificationItem::class.java)
            .forEach { notificationDao.insertNotification(it) }
    }
}
