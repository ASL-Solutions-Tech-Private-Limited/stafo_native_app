package com.stafo.app.base.service

import android.Manifest
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.stafo.app.base.network.RetrofitInstance
import com.stafo.app.base.notification.NotificationsHelper
import com.stafo.app.database.AppDatabase
import com.stafo.app.database.dao.LocationDao
import com.stafo.app.database.dataClass.LocationEntity
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.stafo.app.utils.getAndroidVersion
import com.stafo.app.utils.getBatteryPercentage
import com.stafo.app.utils.getDeviceName
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getUserAccessToken
import com.tanodxyz.gdownload.isNetworkAvailable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration
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

    private lateinit var locationDao: LocationDao

    inner class LocalBinder : Binder() {
        fun getService(): LocationForegroundService = this@LocationForegroundService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_FOREGROUND_SERVICE") {
            stopForegroundService()
            return START_NOT_STICKY
        }

        if (hasLocationPermission()) {
            startAsForegroundService()
            startLocationUpdates()
            startTimer()
        } else {
            Log.e(TAG, "Location permission not granted.")
        }

        return START_NOT_STICKY
    }



    private fun startTimer() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                startRecurringTimer()
            }
        }.start()
    }

/*    private fun postGeoLocation(lat: String, long: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = EmployeePostLocationRequest(
                    employee_id = getEmployeeDetails()?.id.toString(),
                    latitude = lat,
                    longitude = long
                )
                val token = getUserAccessToken() ?: return@launch

                val response = RetrofitInstance.apiService.callPostGeoLocation("Bearer $token", request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("res", "Location updated successfully: ${response.body()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API Error", "Exception: ${e.message}")
            }
        }
    }*/

    private suspend fun postGeoLocation(lat: String, long: String): Boolean {
        return try {
            val request = EmployeePostLocationRequest(
                employee_id = getEmployeeDetails()?.id.toString(),
                latitude = lat,
                longitude = long
            )

            CoroutineScope(Dispatchers.IO).launch {
                val all = locationDao.getAllLocations()
                all.forEachIndexed { index, location ->
                    Log.d("DB_LOG", "Location #$index: $location")
                }
            }





            val token = getUserAccessToken() ?: return false

            val response = RetrofitInstance.apiService.callPostGeoLocation("Bearer $token", request)

            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    Log.d("res", "Location updated successfully: ${response.body()}")
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("API Error", "Exception: ${e.message}")
            false
        }
    }


    /*  private fun startRecurringTimer() {
          handler = Handler(Looper.getMainLooper())
          runnable = object : Runnable {
              override fun run() {
                  postGeoLocation(lat.toString(), longi.toString())
                  handler?.postDelayed(this, 10000)
              }
          }
          handler?.post(runnable!!)
      }*/

    private fun startRecurringTimer() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {


                if (lat != null && longi != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val location = LocationEntity(
                            latitude = lat.toString(),
                            longitude = longi.toString(),
                            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                            isNetwork = true,
                            isGpsTurn = true,
                            deviceName = getDeviceName(),
                            batteryPercentage = getBatteryPercentage(this@LocationForegroundService),
                            androidVersion = getAndroidVersion()
                        )
                        locationDao.insertLocation(location)
                    }

                    // Try to sync if network available
                    if (isNetworkAvailable()) {
                        syncLocationsToServer()
                    }
                } else {
                    Log.w(TAG, "Skipped saving location: lat/long not yet available.")
                }

                handler?.postDelayed(this, 10000)
            }

        }
        handler?.post(runnable!!)
    }

    private fun syncLocationsToServer() {
        CoroutineScope(Dispatchers.IO).launch {
            val unsynced = locationDao.getUnsyncedLocations()
            if (unsynced.isNotEmpty()) {
                val idsSynced = mutableListOf<Int>()
                for (item in unsynced) {
                    val success = postGeoLocation(item.latitude, item.longitude)
                    if (success) {
                        idsSynced.add(item.id)
                    }
                }

                if (idsSynced.isNotEmpty()) {
                    locationDao.markLocationsAsSynced(idsSynced)
                }
            }
        }
    }


    private fun stopRecurringTimer() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        runnable = null
    }

    override fun onCreate() {
        super.onCreate()
        NotificationsHelper.createNotificationChannel(this)
        startAsForegroundService()
        setupLocationUpdates()
        startServiceRunningTicker()


        locationDao = AppDatabase.getDatabase(this).locationDao()


        CoroutineScope(Dispatchers.IO).launch {
            val all = locationDao.getAllLocations()
            all.forEachIndexed { index, location ->
                Log.d("DB_LOG", "Location #$index: $location")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed.")

        stopForeground(true)
        stopLocationUpdates()
        stopRecurringTimer()
        coroutineScope.cancel()
        timerJob?.cancel()

        stopSelf()
    }







    private fun startAsForegroundService() {
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
    }

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(LOCATION_UPDATES_INTERVAL_MS)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

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
                for (location in locationResult.locations) {
                    if (location.latitude != 0.0 && location.longitude != 0.0) {
                        lat = location.latitude
                        longi = location.longitude
                        Log.d(TAG, "Live location: $lat, $longi")

                        if (handler == null) {
                            startRecurringTimer()
                        }
                    }
                }
            }

        }
    }

    private fun startServiceRunningTicker() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            tickerFlow().collectLatest {
                withContext(Dispatchers.Main) {}
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
        if (!isServiceRunning(LocationForegroundService::class.java)) {
            Log.w(TAG, "Service is not running, skipping stopForegroundService()")
            return
        }

        Log.d(TAG, "Stopping foreground service...")

        stopRecurringTimer()
        stopLocationUpdates()
        handler?.removeCallbacksAndMessages(null)
        handler = null
        runnable = null
        coroutineScope.cancel()
        timerJob?.cancel()

        try {
            stopForeground(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error while stopping foreground service: ${e.message}")
        }

        stopSelf()
    }


    companion object {
        private const val TAG = "LocationForegroundService"
        private const val NOTIFICATION_ID = 1
        private val LOCATION_UPDATES_INTERVAL_MS = 1.seconds.inWholeMilliseconds
        private val TICKER_PERIOD_SECONDS = 5.seconds
    }

    private fun isServiceRunning(serviceClass: Class<out Service>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.runningAppProcesses.any { it.processName == packageName }
        } else {
            manager.getRunningServices(Integer.MAX_VALUE).any { it.service.className == serviceClass.name }
        }
    }
}
