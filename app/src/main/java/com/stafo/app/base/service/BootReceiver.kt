package com.stafo.app.base.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getUserAccessToken

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val token = getUserAccessToken()
            val empDetails = getEmployeeDetails()
            if (!token.isNullOrBlank() && empDetails != null) {
                val serviceIntent = Intent(context, LocationForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        context.startForegroundService(serviceIntent)
                    } catch (e: Exception) {
                        // Ignored if OS blocks it
                    }
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}
