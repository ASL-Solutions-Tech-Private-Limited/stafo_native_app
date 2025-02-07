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
import com.asl_emp_mng.app.base.model.PunchInType
import com.asl_emp_mng.app.databinding.ActivityEmpDashboardBinding
import com.asl_emp_mng.app.databinding.CustomBottomSheetAttendanceLayoutBinding
import com.asl_emp_mng.app.screens.EmployeePunchInActivity
import com.asl_emp_mng.app.screens.OtpVerifyActivity
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

                showCustomBottomSheet()


                /*  if (!isLocationEnabled()) {
                      showLocationServicesDialog()
                  } else {
                      checkLocationPermissionAndFind()
                  }*/
            }

            tvHeaderViewProfile.setOnClickListener {
                val intent = Intent(this@EmployeeDashboard, EmplyeeyerProfile::class.java)
                intent.putExtra("DASHBOARD_TYPE", DashboardType.EMPLOYEE.name)
                startActivity(intent)
            }


        }
    }

    private fun showCustomBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialogBinding = CustomBottomSheetAttendanceLayoutBinding.inflate(layoutInflater)
        //val view = layoutInflater.inflate(R.layout.custom_bottom_sheet_attendance_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)



        bottomSheetDialogBinding.bottomSheetCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialogBinding.llGeoAttendance.setOnClickListener {
            if (!isLocationEnabled()) {
                showLocationServicesDialog()
            } else {
                checkLocationPermissionAndFind()
            }
        }

        bottomSheetDialogBinding.llSelfieAttendance.setOnClickListener {
            val intent = Intent(this@EmployeeDashboard, EmployeePunchInActivity::class.java)
            intent.putExtra("Punch_TYPE", PunchInType.SELFIE.name)
            startActivity(intent)
        }

        bottomSheetDialogBinding.llQrAttendance.setOnClickListener {

            Toast.makeText(this@EmployeeDashboard, "work in progress", Toast.LENGTH_SHORT).show()
        }



        bottomSheetDialog.setContentView(bottomSheetDialogBinding.root)


        bottomSheetDialog.show()


    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private val locationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isLocationEnabled()) {
                checkLocationPermissionAndFind()
            }
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

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(this@EmployeeDashboard, EmployeePunchInActivity::class.java)
                intent.putExtra("Punch_TYPE", PunchInType.GEO.name)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkLocationPermissionAndFind() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(this@EmployeeDashboard, EmployeePunchInActivity::class.java)
            intent.putExtra("Punch_TYPE", PunchInType.GEO.name)
            startActivity(intent)
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}