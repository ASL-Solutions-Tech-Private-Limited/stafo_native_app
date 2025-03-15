package com.stafo.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.stafo.app.R
import com.stafo.app.screens.ui.SplashActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "my_channel_id"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // Check if message contains a data payload.
        /*p0?.data?.let {
            Log.d("@@", "Message data payload: " + p0.data)
        }*/

        // Check if message contains a notification payload.
//        remoteMessage.notification?.let {
//            sendNotification(it.body!!)
//        }
//        remoteMessage.data.let {
//            showNotification()
//        }

        try {
            remoteMessage.notification?.let {
                showNotification(it.body!!)
            }
        } catch (e: Exception) {
            println(e.message)
            remoteMessage.notification?.let {
                showNotification(it.body!!)
            }
        }


    }


    override fun onNewToken(p0: String) {
//        sendRegistrationToServer(token)
        setFBToken(p0)
    }


    private fun showNotification(body: String?) {
        val intent = Intent(this, SplashActivity::class.java)


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val channelId = "Default"
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(body).setAutoCancel(true)
                .setContentIntent(pendingIntent)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Default channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                manager!!.createNotificationChannel(channel)
            }
            manager!!.notify(0, builder.build())
        } else {
            val pendingIntent =
                PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            val channelId = "Default"
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(body).setAutoCancel(true)
                .setContentIntent(pendingIntent)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Default channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                manager!!.createNotificationChannel(channel)
            }
            manager!!.notify(0, builder.build())
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "My Channel"
            val description = "My Notification Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            // Register the channel with the system
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}