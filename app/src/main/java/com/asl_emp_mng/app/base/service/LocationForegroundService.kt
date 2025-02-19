package com.asl_emp_mng.app.base.service

import android.Manifest
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.asl_emp_mng.app.base.network.RetrofitInstance
import com.asl_emp_mng.app.base.notification.NotificationsHelper
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getUserAccessToken
import com.google.android.gms.location.*
import kotlinx.coroutines.*
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

    private var lat: Double? = null
    private var longi: Double? = null

    private var handler: Handler? = null
    private var runnable: Runnable? = null


    inner class LocalBinder : Binder() {
        fun getService(): LocationForegroundService = this@LocationForegroundService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        if (hasLocationPermission()) {
            startAsForegroundService()
            startLocationUpdates()

            startTimer()


        } else {
            Log.e(TAG, "Location permission not granted. The Activity should have handled this.")
        }

        return START_STICKY
    }


    private fun startTimer() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {

                startRecurringTimer()
            }
        }.start()
    }


    private fun postGeoLocation(lat: String, long: String) {


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = EmployeePostLocationRequest(
                    employee_id = getEmployeeDetails()?.id.toString(),
                    latitude = lat,
                    longitude = long
                )

                Log.d("res", "post geo: $request")

                val token = getUserAccessToken()
                if (token == null) {
                    withContext(Dispatchers.Main) {
                        Log.e("API Error", "Token is null")
                    }
                    return@launch
                }


                val response = RetrofitInstance.apiService.callPostGeoLocation("Bearer $token", request)

                withContext(Dispatchers.Main) {
                    if (response != null && response.isSuccessful) {
                        val locationResponse = response.body()
                        Log.d("res", "Location updated successfully: $locationResponse")
                    } else {
                        Log.e("res", "Failed to update location. Code: ${response?.code()}, Message: ${response?.message()}")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API Error", "Exception: ${e.message}")
                }
            }
        }


    }

    private fun startRecurringTimer() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                Log.d("res", "15 seconds passed")

                postGeoLocation(lat.toString(),longi.toString())

                handler?.postDelayed(this, 10000)
            }
        }

        handler?.post(runnable!!)
    }

    private fun stopRecurringTimer() {
        handler?.removeCallbacks(runnable!!)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        // Create the notification channel before starting the service
        NotificationsHelper.createNotificationChannel(this)

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
            Toast.makeText(
                this,
                "Location permission is required for this service.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    lat=location.latitude
                    longi=location.longitude
                }
            }
        }
    }

    private fun startServiceRunningTicker() {
        timerJob?.cancel() // Cancel any previous job
        timerJob = coroutineScope.launch {
            tickerFlow().collectLatest {
                withContext(Dispatchers.Main) {
                    /*   Toast.makeText(
                           this@LocationForegroundService,
                           "Foreground Service still running!",
                           Toast.LENGTH_SHORT
                       ).show()*/
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
        private const val NOTIFICATION_ID = 1
        private val LOCATION_UPDATES_INTERVAL_MS = 1.seconds.inWholeMilliseconds
        private val TICKER_PERIOD_SECONDS = 5.seconds
    }



    private fun isLocationServiceRunning(serviceClass: Class<out Service>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
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
