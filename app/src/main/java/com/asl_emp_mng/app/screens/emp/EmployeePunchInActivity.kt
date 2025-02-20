package com.asl_emp_mng.app.screens.emp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.PunchInType
import com.asl_emp_mng.app.base.service.LocationForegroundService
import com.asl_emp_mng.app.databinding.ActivityEmployeePunchInBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.PunchInRequest
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.github.dhaval2404.imagepicker.ImagePicker
import org.osmdroid.config.Configuration
import com.mmi.MapmyIndiaMapView
import com.mmi.layers.Marker
import com.mmi.layers.UserLocationOverlay
import com.mmi.layers.location.GpsLocationProvider


class EmployeePunchInActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEmployeePunchInBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var userLocationOverlay: UserLocationOverlay
    private var getLati: Double? = null
    private var getLongi: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeePunchInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }

        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm_prefs", MODE_PRIVATE))








        onClickListener()
        observeViewModel()
       // startLocationService()
    }

    private fun onClickListener() {

        val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
        val mapView = mapmyIndiaMapView.mapView

        // Enable User Location Tracking
        userLocationOverlay = UserLocationOverlay(GpsLocationProvider(this), mapView)

        // Use default location marker icon by not setting a custom one
        // If you still want to set a custom location marker, uncomment the below code and ensure the drawable exists
        /*
        val locationIconResId = R.drawable.ic_launcher_background  // Your custom marker drawable
        val locationIcon = resources.getDrawable(locationIconResId, theme)
        if (locationIcon != null) {
            userLocationOverlay.setCurrentLocationResId(locationIconResId)
        } else {
            Log.e("MainActivity", "Location icon not found.")
        }
        */

        userLocationOverlay.enableMyLocation()
        mapView.overlays.add(userLocationOverlay)
        mapView.invalidate()

        // Set Marker at User's Location
        userLocationOverlay.runOnFirstFix {
            val userLocation = userLocationOverlay.myLocation
            getLati=userLocation.latitude
            getLongi=userLocation.longitude
            if (userLocation != null) {
                runOnUiThread {
                    val marker = Marker(mapView)
                    marker.position = userLocation
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)
                    mapView.invalidate()
                    mapView.setCenter(userLocation)
                    mapView.setZoom(13)
                }
            }
        }


        val punchType =
            PunchInType.valueOf(intent.getStringExtra("Punch_TYPE") ?: PunchInType.SELFIE.name)

        /*if (punchType == PunchInType.SELFIE) {
            binding.clEmpAttendSelfie.visibility = View.VISIBLE
        } else {
            binding.clEmpAttendSelfie.visibility = View.GONE
            if (!isLocationEnabled()) {
                showLocationServicesDialog()
            } else {
                checkLocationPermissionAndFind()
            }
        }*/

        binding.imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }



        binding.rlSelfiePunchIn.visibility = View.GONE






        binding?.apply {


            tvTakeSelfie.setOnClickListener {
                openPicker(1101)
            }


        }





        binding.btnPunchIn.setOnClickListener {
            val request = PunchInRequest(
                employeeId = getEmployeeDetails()?.id.toString(),
                latitude = getLati.toString(),
                longitude = getLongi.toString()
            )
            Log.d("res","post: $getLati $getLongi")
            settingsViewModel.punchInRequest(this, request)


        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mPunchInResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else {
                CustomToast(this, it.message)
            }


        }


    }



    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    private fun openPicker(req: Int) {
        Log.e("TAG", "openPicker: $req")
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(
                1080,
                1080
            )
            .start(req)
    }


    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }


}