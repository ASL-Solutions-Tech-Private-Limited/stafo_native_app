package com.stafo.app.base.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.stafo.app.R
import com.stafo.app.base.network.RetrofitInstance
import com.stafo.app.base.notification.NotificationsHelper
import com.stafo.app.database.AppDatabase
import com.stafo.app.database.dao.LocationDao
import com.stafo.app.database.dataClass.LocationEntity
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.stafo.app.screens.settings.dataClass.LocationLogRequest
import com.stafo.app.screens.tripPlan.dataClass.TripGeoLocationRequest
import com.stafo.app.utils.*
import com.tanodxyz.gdownload.isNetworkAvailable
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.seconds

class LocationForegroundService : Service() {

    private val binder = LocalBinder()
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var timerJob: Job? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private var lat: Double? = null
    private var longi: Double? = null
    private var tripSource: Boolean = false

    private lateinit var locationDao: LocationDao
    private var tripLocationCounter = 0

    inner class LocalBinder : Binder() {
        fun getService(): LocationForegroundService = this@LocationForegroundService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }


    override fun onCreate() {
        super.onCreate()
        NotificationsHelper.createNotificationChannel(this)
        startAsForegroundService()
        setupLocationUpdates()
        locationDao = AppDatabase.getDatabase(this).locationDao()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAsForegroundService()

        if (intent?.action == "STOP_FOREGROUND_SERVICE") {
            stopForegroundService()
            return START_NOT_STICKY
        }

        val isFromTripDetails = intent?.getBooleanExtra("FROM_TRIP_DETAILS", false) == true
        tripSource = isFromTripDetails

        if (hasLocationPermission()) {
            startLocationUpdates()
            startRecurringTimer()
        } else {
            Log.e(TAG, "Location permission not granted. Stopping foreground service.")
            stopForegroundService()
        }

        return START_STICKY
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved called. Service will rely on START_STICKY to restart if needed.")
    }


    fun stopForegroundService() {
        if (!isServiceRunning(LocationForegroundService::class.java)) {
            return
        }

        stopRecurringTimer()
        stopLocationUpdates()
        coroutineScope.cancel()
        timerJob?.cancel()

        try {
            stopForeground(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping foreground: ${e.message}")
        }

        stopSelf()
    }




    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed.")
        stopForeground(true)
        stopLocationUpdates()
        stopRecurringTimer()
        coroutineScope.cancel()
        timerJob?.cancel()
        // no stopSelf() here!
    }




    private fun startAsForegroundService() {
        try {
            NotificationsHelper.createNotificationChannel(this)
            val notification = NotificationsHelper.buildNotification(this)

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
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground service: ${e.message}")
        }
    }


    private fun hasLocationPermission(): Boolean = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(LOCATION_UPDATES_INTERVAL_MS)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission not granted", e)
            Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping location updates: ${e.message}")
        }
    }

    private fun setupLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.firstOrNull()?.let { location ->
                    if (location.latitude != 0.0 && location.longitude != 0.0) {
                        lat = location.latitude
                        longi = location.longitude
                        Log.d(TAG, "Live location: $lat, $longi")
                    }
                }
            }
        }
    }


    private fun startRecurringTimer() {
        if (timerJob != null) return

        timerJob = CoroutineScope(Dispatchers.IO).launch {
            var firstTripSent = false

            //  Wait until location is available
            while (lat == null || longi == null) {
                delay(500)
            }

            while (isActive) {
                val gpsOn = isGpsEnabled(applicationContext)
                val networkOn = isNetworkAvailable()
                val isTrip = getTripServiceAction(applicationContext)

                val defaultDelayMillis = 30_000L
                val tripDelayMillis = 300_000L
                var delayMillis = defaultDelayMillis

                if (networkOn) {
                    val synced = syncLocationsToServer()
                    if (synced) {
                        if (isTrip) {
                            if (!firstTripSent) {
                                postTripGeoLocation(lat.toString(), longi.toString())
                                firstTripSent = true
                                delayMillis = tripDelayMillis
                            } else {
                                postTripGeoLocation(lat.toString(), longi.toString())
                                delayMillis = tripDelayMillis
                            }
                        } else {
                            postGeoLocation(lat.toString(), longi.toString())
                            delayMillis = defaultDelayMillis
                        }
                    }
                } else {
                    saveLocationOffline(gpsOn)
                    delayMillis = defaultDelayMillis
                }

                delay(delayMillis)
            }
        }
    }


    private fun stopRecurringTimer() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        runnable = null
    }

    private suspend fun syncLocationsToServer(): Boolean {
        return withContext(Dispatchers.IO) {
            val all = locationDao.getAllLocations()
            if (all.isNullOrEmpty()) {
                Log.d(TAG, "No offline locations to sync.")
                return@withContext true
            }
            all.forEachIndexed { index, location ->
                Log.d(TAG, "Offline location #$index: $location")
            }
            postLocationLog(all)
        }
    }

    private suspend fun postLocationLog(list: List<LocationEntity>): Boolean {
        return try {
            val jsonLogData = Gson().toJson(list)
            val request = LocationLogRequest(
                company_id = getEmployeeComId().toString(),
                employee_id = getEmployeeDetails()?.id.toString(),
                log_data = jsonLogData
            )

            val response = RetrofitInstance.getApiService(applicationContext).callDeviceLog(request)

            if (response.isSuccessful) {
                Log.d(TAG, "Location log uploaded successfully: ${response.body()}")
                locationDao.clearAllLocations()
                true
            } else {
                Log.e(TAG, "Failed to upload location log: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception uploading location log: ${e.message}")
            false
        }
    }

    private suspend fun postGeoLocation(lat: String, long: String): Boolean {
        return try {
            val request = EmployeePostLocationRequest(
                employee_id = getEmployeeDetails()?.id.toString(),
                latitude = lat,
                longitude = long,
                battery_status = "${getBatteryPercentage(this@LocationForegroundService)}%"
            )

            val token = getUserAccessToken() ?: return false

            val response = RetrofitInstance.getApiService(applicationContext)
                .callPostGeoLocation("Bearer $token", request)

            if (response.isSuccessful) {
                Log.d(TAG, "Location updated successfully: ${response.body()}")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "API error: ${e.message}")
            false
        }
    }


    private suspend fun postTripGeoLocation(lat: String, long: String): Boolean {
        return try {


            val request = TripGeoLocationRequest(
                trip_id = getTripId().toString(), latitude = lat, longitude = long
            )

            val response =
                RetrofitInstance.getApiService(applicationContext).callTripGeoLocation(request)

            if (response.isSuccessful) {
                Log.d(TAG, "Trip Location updated successfully: ${response.body()}")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "API error: ${e.message}")
            false
        }
    }


    private suspend fun saveLocationOffline(gpsOn: Boolean) {
        val location = LocationEntity(
            latitude = lat?.toString() ?: "N/A",
            longitude = longi?.toString() ?: "N/A",
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            isNetwork = false,
            isGpsTurn = gpsOn,
            deviceName = getDeviceName(),
            batteryPercentage = getBatteryPercentage(this@LocationForegroundService),
            androidVersion = getAndroidVersion()
        )
        locationDao.insertLocation(location)
        Log.d(
            TAG,
            if (gpsOn) "Offline location stored." else "GPS off - fallback location stored once."
        )

        if (!gpsOn) {
            stopRecurringTimer()
        }
    }


    private fun isServiceRunning(serviceClass: Class<out Service>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }

    companion object {
        private const val TAG = "LocationForegroundService"
        private const val NOTIFICATION_ID = 1
        private val LOCATION_UPDATES_INTERVAL_MS = 15.seconds.inWholeMilliseconds
    }
}


