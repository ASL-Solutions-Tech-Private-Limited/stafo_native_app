package com.stafo.app.base

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.stafo.app.screens.ui.SplashActivity
import com.stafo.app.utils.isNetworkAvailable

class NetworkConnectivityHandler(private val application: Application) :
    Application.ActivityLifecycleCallbacks {

    private val dialogMap = mutableMapOf<String, AlertDialog>()
    private var currentActivity: Activity? = null
    private var hasRestartedAfterNetworkRestore = false

    init {
        NetworkMonitor.startMonitoring(application)

        NetworkMonitor.isConnected.observeForever { isConnected ->
            currentActivity?.let { activity ->
                if (activity is SplashActivity || activity.isFinishing || activity.isDestroyed) return@observeForever

                if (isConnected) {
                    dismissDialog(activity, "internet")
                    if (!hasRestartedAfterNetworkRestore) {
                        hasRestartedAfterNetworkRestore = true
                        activity.recreate()
                    }

                } else {
                    hasRestartedAfterNetworkRestore = false
                    showCustomDialog(
                        activity,
                        "No Internet Connection",
                        "Please check your internet connection.",
                        "internet"
                    )
                }
            }
        }
    }

    private fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showCustomDialog(activity: Activity, title: String, message: String, tag: String) {

        if (activity.isFinishing || activity.isDestroyed) {
            return
        }

        if (dialogMap[tag]?.isShowing == true) {
            return
        }

        try {
            val dialog = AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    activity.startActivity(intent)
                }
                .create()
            dialog.show()
            dialogMap[tag] = dialog
        } catch (e: Exception) {
            Log.e("NetworkHandler", "Dialog [$tag] failed to show", e)
        }
    }

    private fun dismissDialog(activity: Activity, tag: String) {
        val dialog = dialogMap[tag]
        if (dialog != null) {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            dialogMap.remove(tag)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity

        Handler(Looper.getMainLooper()).postDelayed({
            if (activity is SplashActivity) return@postDelayed

            val isConnected = NetworkMonitor.isConnected.value
            val fallbackConnected = isNetworkAvailable(activity)

            if (isConnected == false || !fallbackConnected) {
                showCustomDialog(
                    activity,
                    "No Internet Connection",
                    "Please check your internet connection.",
                    "internet"
                )
            } else {
                dismissDialog(activity, "internet")
            }

            if (!isLocationEnabled(activity)) {
                showCustomDialog(
                    activity,
                    "Enable Location",
                    "Location services are OFF. Please enable GPS.",
                    "gps"
                )
            } else {
                dismissDialog(activity, "gps")
            }

            if (!hasLocationPermission(activity)) {
                showCustomDialog(
                    activity,
                    "Location Permission",
                    "Location permission is required.",
                    "permission"
                )
            } else {
                dismissDialog(activity, "permission")
            }

        }, 300)
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity == activity) currentActivity = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        Log.d("NetworkHandler", "onActivityStarted: ${activity.localClassName}")
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityDestroyed(activity: Activity) {
        val iterator = dialogMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val dialog = entry.value
            if (dialog.isShowing && dialog.context === activity) {
                dialog.dismiss()
                iterator.remove()
            }
        }
    }


    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}
