package com.basebox.smartvendor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basebox.smartvendor.data.local.model.NotificationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("SELECT * FROM notification_items ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("DELETE FROM notification_items WHERE id = :id")
    suspend fun deleteNotificationById(id: String)

    //get a notification by id
    @Query("SELECT * FROM notification_items WHERE id = :id")
    suspend fun getNotificationById(id: String): NotificationItem
}