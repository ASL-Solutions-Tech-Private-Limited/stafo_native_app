package com.asl_emp_mng.app.screens.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.base.request.AddBranchRequest
import com.asl_emp_mng.app.databinding.ActivityAddBranchBinding
import com.asl_emp_mng.app.screens.auth.AuthViewModel
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.screens.ui.PlaceSearchActivity
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getCompanyDetails
import com.asl_emp_mng.app.utils.getEmployeeComId
import java.util.Locale

class AddBranchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBranchBinding


    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private val PLACE_SEARCH_REQUEST_CODE = 101


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        onClickListener()



        observeViewModel()


    }



    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mCreateBranchResponse.observe(this) {
            CustomToast(this, it.message)
            onBackPressedDispatcher.onBackPressed()
        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }

            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
                )
            }

            val lastKnownGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastKnownNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (lastKnownGps != null && lastKnownNetwork != null) {
                currentLocation = if (lastKnownGps.accuracy <= lastKnownNetwork.accuracy) {
                    lastKnownGps
                } else {
                    lastKnownNetwork
                }
            } else if (lastKnownGps != null) {
                currentLocation = lastKnownGps
            } else if (lastKnownNetwork != null) {
                currentLocation = lastKnownNetwork
            }

            currentLocation?.let {
                latitude = it.latitude
                longitude = it.longitude

                val intent = Intent(this@AddBranchActivity, PlaceSearchActivity::class.java)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                startActivityForResult(intent, PLACE_SEARCH_REQUEST_CODE)
            }

        } else {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
            latitude = location.latitude
            longitude = location.longitude
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private val networkLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
            latitude = location.latitude
            longitude = location.longitude

        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun getAddressFromLocation(lat: Double, lon: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0].getAddressLine(0)

            binding.tieBranchAddress.setText(address)
        } else {
            Toast.makeText(this, "Unable to get address", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickListener() {
        binding?.apply {


            btnAddBranch.setOnClickListener {
                if (isValidate()) {
                     getEmployeeComId()?.let { it1 ->
                         val request =  AddBranchRequest(
                            company_id = it1.toInt(),
                            branch_name = binding.tieBranchName.text.toString(),
                            branch_address = binding.tieBranchAddress.text.toString(),
                            latitude = latitude.toString(),
                            longitude = longitude.toString(),
                            radar = binding.tieBranchRadius.text.toString()
                        )
                        settingsViewModel.createBranch(this@AddBranchActivity, request)
                    }

                }
            }

            tieBranchAddress.setOnClickListener {
                getLocation()
            }

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val lat = data?.getDoubleExtra("latitude", 0.0)
            val lng = data?.getDoubleExtra("longitude", 0.0)
            latitude = lat ?: 0.0
            longitude = lng ?: 0.0
            getAddressFromLocation(latitude, longitude)
        }
    }

    private fun isValidate(): Boolean {
        binding?.apply {
            if (tieBranchName.text.isNullOrEmpty()) {
                tieBranchName.error = "Please enter branch name"
                tieBranchName.requestFocus()
                return false
            } else if (tieBranchAddress.text.isNullOrEmpty()) {

                CustomToast(this@AddBranchActivity,"Please enter branch address")
                return false
            } else if (tieBranchRadius.text.isNullOrEmpty()) {
                tieBranchRadius.error = "Please enter radius"
                tieBranchRadius.requestFocus()
                return false
            }
        }
        return true
    }
}