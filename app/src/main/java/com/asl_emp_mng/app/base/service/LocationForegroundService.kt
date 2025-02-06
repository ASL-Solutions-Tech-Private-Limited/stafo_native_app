package com.asl_emp_mng.app.base.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.asl_emp_mng.app.base.notification.NotificationsHelper
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class LocationForegroundService : Service() {

    private val binder = LocalBinder()
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var timerJob: Job? = null

    private val _locationFlow = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _locationFlow

    inner class LocalBinder : Binder() {
        fun getService(): LocationForegroundService = this@LocationForegroundService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        if (hasLocationPermission()) {
            startAsForegroundService() // Start as a foreground service
            startLocationUpdates()     // Start location updates
        } else {
            // Log or handle the error, since permission should already be granted by Activity
            Log.e(TAG, "Location permission not granted. The Activity should have handled this.")
        }

        return START_STICKY // Keep service running until explicitly stopped
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        // Create the notification channel before starting the service
        NotificationsHelper.createNotificationChannel(this)

        Toast.makeText(this, "Foreground Service created", Toast.LENGTH_SHORT).show()

        setupLocationUpdates() // Set up the location updates
        startServiceRunningTicker() // Start service running ticker
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel() // Cancel ticker job
        coroutineScope.coroutineContext.cancelChildren() // Cancel coroutines

        Toast.makeText(this, "Foreground Service destroyed", Toast.LENGTH_SHORT).show()
    }

    private fun startAsForegroundService() {
        // Create the notification immediately to avoid issues
        val notification = NotificationsHelper.buildNotification(this)

        // Start the service as a foreground service with the notification
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            } else {
                0
            }
        )
    }

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(LOCATION_UPDATES_INTERVAL_MS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission not granted", e)
            Toast.makeText(this, "Location permission is required for this service.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // Update location flow with new location
                for (location in locationResult.locations) {
                    _locationFlow.value = location
                }
            }
        }
    }

    private fun startServiceRunningTicker() {
        timerJob?.cancel() // Cancel any previous job
        timerJob = coroutineScope.launch {
            tickerFlow().collectLatest {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LocationForegroundService,
                        "Foreground Service still running!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun tickerFlow(
        period: Duration = TICKER_PERIOD_SECONDS,
        initialDelay: Duration = TICKER_PERIOD_SECONDS
    ) = flow {
        delay(initialDelay.inWholeMilliseconds)
        while (true) {
            emit(Unit)
            delay(period.inWholeMilliseconds)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Method to stop the foreground service and remove the notification
    fun stopForegroundService() {
        stopForeground(true) // Stop the foreground service and remove the notification
        stopSelf() // Stop the service itself
        Log.d(TAG, "Foreground service stopped.")
    }

    companion object {
        private const val TAG = "LocationForegroundService"
        private const val NOTIFICATION_ID = 1 // Unique ID for the notification
        private val LOCATION_UPDATES_INTERVAL_MS = 1.seconds.inWholeMilliseconds
        private val TICKER_PERIOD_SECONDS = 5.seconds
    }
}






/*class LocationForegroundService : Service() {

    private val binder = LocalBinder()
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var timerJob: Job? = null

    private val _locationFlow = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _locationFlow

    inner class LocalBinder : Binder() {
        fun getService(): LocationForegroundService = this@LocationForegroundService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        if (hasLocationPermission()) {
            startAsForegroundService() // Start as a foreground service
            startLocationUpdates()     // Start location updates
        } else {
            // Log or handle the error, since permission should already be granted by Activity
            Log.e(TAG, "Location permission not granted. The Activity should have handled this.")
        }

        return START_STICKY // Keep service running until explicitly stopped
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        Toast.makeText(this, "Foreground Service created", Toast.LENGTH_SHORT).show()

        setupLocationUpdates() // Set up the location updates
        startServiceRunningTicker() // Start service running ticker
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel() // Cancel ticker job
        coroutineScope.coroutineContext.cancelChildren() // Cancel coroutines

        Toast.makeText(this, "Foreground Service destroyed", Toast.LENGTH_SHORT).show()
    }

    private fun startAsForegroundService() {
        NotificationsHelper.createNotificationChannel(this)

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            NotificationsHelper.buildNotification(this),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            } else {
                0
            }
        )
    }

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(LOCATION_UPDATES_INTERVAL_MS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission not granted", e)
            Toast.makeText(this, "Location permission is required for this service.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // Update location flow with new location
                for (location in locationResult.locations) {
                    _locationFlow.value = location
                }
            }
        }
    }

    private fun startServiceRunningTicker() {
        timerJob?.cancel() // Cancel any previous job
        timerJob = coroutineScope.launch {
            tickerFlow().collectLatest {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LocationForegroundService,
                        "Foreground Service still running!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun tickerFlow(
        period: Duration = TICKER_PERIOD_SECONDS,
        initialDelay: Duration = TICKER_PERIOD_SECONDS
    ) = flow {
        delay(initialDelay.inWholeMilliseconds)
        while (true) {
            emit(Unit)
            delay(period.inWholeMilliseconds)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun stopForegroundService() {
        stopForeground(true)
        stopSelf() // Stop the service itself
        Log.d(TAG, "Foreground service stopped.")
    }

    companion object {
        private const val TAG = "com.asl_emp_mng.app.base.service.LocationForegroundService"
        private const val NOTIFICATION_ID = 1
        private val LOCATION_UPDATES_INTERVAL_MS = 1.seconds.inWholeMilliseconds
        private val TICKER_PERIOD_SECONDS = 5.seconds
    }
}*/
