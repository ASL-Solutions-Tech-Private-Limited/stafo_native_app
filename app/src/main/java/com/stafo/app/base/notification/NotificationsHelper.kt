package com.stafo.app.base.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.stafo.app.R
import com.stafo.app.screens.ui.MainActivity

object NotificationsHelper {

    private const val NOTIFICATION_CHANNEL_ID = "general_notification_channel"

    // Create Notification Channel
    fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if the channel exists before creating
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.foreground_service_sample_notification_channel_general_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    // Build Notification
    fun buildNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.foreground_service_sample_notification_title))
            .setContentText(context.getString(R.string.foreground_service_sample_notification_description))
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(Intent(context, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            })
            .build()
    }
}


/*internal object NotificationsHelper {

    private const val NOTIFICATION_CHANNEL_ID = "general_notification_channel"
    fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if the channel exists before creating
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.foreground_service_sample_notification_channel_general_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    // Build notification
    fun buildNotification(context: Context): Notification {
        val notificationIntent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.foreground_service_sample_notification_title))
            .setContentText(context.getString(R.string.foreground_service_sample_notification_description))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }
}*/
