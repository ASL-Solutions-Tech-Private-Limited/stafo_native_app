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


                val token = getUserAccessToken()
                if (token == null) {
                    withContext(Dispatchers.Main) {
                    }
                    return@launch
                }


                val response = RetrofitInstance.apiService.callPostGeoLocation("Bearer $token", request)

                withContext(Dispatchers.Main) {
                    if (response != null && response.isSuccessful) {
                        val locationResponse = response.body()
                        Log.d("res", "Location updated successfully: $locationResponse")
                    } else {
                      //  Log.e("res", "Failed to update location. Code: ${response?.code()}, Message: ${response?.message()}")
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
                postGeoLocation(lat.toString(),longi.toString())

                handler?.postDelayed(this, 10000)
            }
        }

        handler?.post(runnable!!)
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
    }


    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel()
        coroutineScope.coroutineContext.cancelChildren()
        stopRecurringTimer()
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
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            tickerFlow().collectLatest {
                withContext(Dispatchers.Main) {
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

        if (!isServiceRunning(LocationForegroundService::class.java)) {
            Log.w(TAG, "Service is not running, skipping stopForegroundService()")
            return
        }

        stopRecurringTimer()
        coroutineScope.cancel()
        timerJob?.cancel()
        timerJob = null

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
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.getRunningServices(Int.MAX_VALUE).any {
            it.service.className == serviceClass.name
        }
    }




}



