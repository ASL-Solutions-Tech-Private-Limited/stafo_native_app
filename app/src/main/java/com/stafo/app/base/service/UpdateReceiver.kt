package com.stafo.app.base.service

import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getUserAccessToken

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val token = getUserAccessToken()
        val empDetails = getEmployeeDetails()
        if (token.isNullOrBlank() || empDetails == null) return

        if (!isServiceRunning(context, LocationForegroundService::class.java)) {
            val serviceIntent = Intent(context, LocationForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    context.startForegroundService(serviceIntent)
                    Log.e("service", " true startForegroundService")
                } catch (e: Exception) {
                    Log.e("service", "Failed to start foreground service: ${e.message}")
                }
            } else {
                Log.e("service", " else startService")
                context.startService(serviceIntent)
            }
        } else Log.e("service", "else part of receiver")
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<out Service>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}


