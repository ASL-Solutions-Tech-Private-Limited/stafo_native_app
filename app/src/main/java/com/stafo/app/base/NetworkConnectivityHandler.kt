package com.stafo.app.base

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.stafo.app.screens.ui.SplashActivity

class NetworkConnectivityHandler(private val application: Application) :
    Application.ActivityLifecycleCallbacks {

    private val dialogMap = mutableMapOf<Activity, AlertDialog>()
    private var currentActivity: Activity? = null

    init {
        NetworkMonitor.register(application)

        NetworkMonitor.isConnected.observeForever { isConnected ->
            Log.d("NetworkHandler", "Network connected: $isConnected")

            if (!isConnected) {
                Handler(Looper.getMainLooper()).postDelayed({
                    currentActivity?.let { activity ->
                        if (activity is SplashActivity) {
                            Log.d("NetworkHandler", "Skipping dialog on SplashActivity")
                            return@postDelayed
                        }

                        if (!activity.isFinishing && !activity.isDestroyed) {
                            if (dialogMap[activity]?.isShowing != true) {
                                val dialog =
                                    AlertDialog.Builder(activity).setTitle("No Internet Connection")
                                        .setMessage("Please check your internet connection.")
                                        .setCancelable(false).setPositiveButton("OK", null).create()
                                dialog.show()
                                dialogMap[activity] = dialog
                                Log.d(
                                    "NetworkHandler", "Dialog shown on: ${activity.localClassName}"
                                )
                            }
                        }
                    } ?: Log.d("NetworkHandler", "No current activity to show dialog")
                }, 500)
            } else {
                currentActivity?.let { activity ->
                    dismissDialog(activity)
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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun dismissDialog(activity: Activity) {
        dialogMap[activity]?.takeIf { it.isShowing }?.dismiss()
        dialogMap.remove(activity)
        Log.d("NetworkHandler", "Dialog dismissed on: ${activity.localClassName}")
    }


    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity

        Handler(Looper.getMainLooper()).postDelayed({
            if (NetworkMonitor.isConnected.value == false) {
                showAlertDialog(activity, "No Internet", "Please check your internet connection.")
            }

            if (!hasLocationPermission(activity)) {
                showAlertDialog(activity, "Location Permission", "Location permission is required.")
            }

            if (!isLocationEnabled(activity)) {
                showAlertDialog(
                    activity, "Enable Location", "Location services are OFF. Please enable GPS."
                )
            }

        }, 300)
    }


    override fun onActivityPaused(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d("NetworkHandler", "onActivityStarted: ${activity.localClassName}")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityDestroyed(activity: Activity) {
        dismissDialog(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}


    private fun showAlertDialog(activity: Activity, title: String, message: String) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val dialog = AlertDialog.Builder(activity).setTitle(title).setMessage(message)
                .setCancelable(false).setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }.create()
            dialog.show()
        }
    }


}

