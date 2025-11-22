
package com.basebox.smartvendor.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.basebox.smartvendor.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationService @Inject constructor(@ApplicationContext private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showSaleNotification(itemName: String, quantity: Int) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name) // Make sure you have this drawable
            .setContentTitle("New Sale Recorded! 🤑")
            .setContentText("You just sold $quantity unit(s) of $itemName.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sales Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Receive notifications for new sales"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "sales_channel"
    }
}
