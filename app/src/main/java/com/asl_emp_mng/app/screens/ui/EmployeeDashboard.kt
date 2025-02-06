package com.asl_emp_mng.app.screens.ui

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
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.DashboardType
import com.asl_emp_mng.app.databinding.ActivityEmpDashboardBinding
import com.asl_emp_mng.app.databinding.CustomBottomSheetAttendanceLayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetDialog

class EmployeeDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmpDashboardBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetDialogBinding: CustomBottomSheetAttendanceLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        onClickListener()
    }

    private fun onClickListener() {
        binding?.apply {


            btnPunchIn.setOnClickListener {

                if (!isLocationEnabled()) {
                    showLocationServicesDialog()
                } else {
                    checkLocationPermissionAndFind()
                }
            }

            tvHeaderViewProfile.setOnClickListener {
                val intent = Intent(this@EmployeeDashboard, EmplyeeyerProfile::class.java)
                intent.putExtra("DASHBOARD_TYPE", DashboardType.EMPLOYEE.name)
                startActivity(intent)
            }


        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showLocationServicesDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Location Services")
            .setMessage("This app requires location services to be enabled. Please turn on location services.")
            .setPositiveButton("OK") { _, _ ->
                locationSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private val locationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isLocationEnabled()) {
                checkLocationPermissionAndFind()
            }
        }

    private fun checkLocationPermissionAndFind() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            findLocation()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                findLocation()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun findLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(15000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                fusedLocationClient.removeLocationUpdates(this)

                if (locationResult.locations.isNotEmpty()) {
                    val lastLocation: Location = locationResult.locations.last()
                    val latitude = lastLocation.latitude
                    val longitude = lastLocation.longitude
                    openGoogleMaps(latitude,longitude)
                } else {
                    Toast.makeText(this@EmployeeDashboard, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun openGoogleMaps(latitude: Double, longitude: Double) {
        val mapUri = Uri.parse("https://maps.google.com/maps/search/$latitude,$longitude")
        val intent = Intent(Intent.ACTION_VIEW, mapUri)
        startActivity(intent)
    }

 /*   private fun showCustomBottomSheet(){
        bottomSheetDialog=BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.custom_bottom_sheet_attendance_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)

        edtShiftName = view.findViewById(R.id.edt_shift_name)
        edtShiftStartTime = view.findViewById(R.id.edt_shift_start_time)
        edtShiftEndTime = view.findViewById(R.id.edt_shift_end_time)
        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)

        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btn_add_shift)

        btnSubmit.setOnClickListener {
            if (isValidate()){

            }
        }
        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()



    }*/



}