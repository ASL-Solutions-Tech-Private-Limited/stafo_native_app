package com.stafo.app.screens.emp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityEmployeePunchInBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.PunchInRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeDetails
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.stafo.app.screens.settings.dataClass.GetAttendanceBranchRequest
import com.stafo.app.utils.getIsCOMPANYLogin


class EmployeePunchInActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var binding: ActivityEmployeePunchInBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var getLati: Double? = null
    private var getLongi: Double? = null

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mEmpID = ""
    private var branchLat: Double = 0.0
    private var branchLong: Double = 0.0
    private var radar: Float = 0.0f
    private var checkBranch: Boolean = false

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mEmpID = getEmployeeDetails()?.id.toString() ?: ""

        mEmpID.let { empId ->

            val request = GetAttendanceBranchRequest(
                employee_id = empId
            )
            settingsViewModel.getEmpAttendanceBranch(
                this, request
            )
        }

        onClickListener()
        observeViewModel()

    }

    private fun onClickListener() {
        binding.imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        binding.btnPunchIn.setOnClickListener {
            if (getLati == null || getLongi == null) {
                CustomToast(this, "Location not available. Please wait or enable GPS.")
                return@setOnClickListener
            }

            if (checkBranch) {
                val distance = calculateDistance(getLati!!, getLongi!!, branchLat, branchLong)

                Log.d("res", "post: $getLati $getLongi   distance: $distance")

                if (distance <= radar) {

                    val request = PunchInRequest(
                        employeeId = getEmployeeDetails()?.id.toString(),
                        latitude = getLati.toString(),
                        longitude = getLongi.toString()
                    )

                    /* val request = PunchInRequest(
                         employeeId = getEmployeeDetails()?.id.toString(),
                         latitude = "23.6304733",
                         longitude = "88.4355422"
                     )*/
                    settingsViewModel.punchInRequest(this, request)


                } else {
                    CustomToast(
                        this@EmployeePunchInActivity,
                        "Please move closer to the branch area to punch attendance."
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        onBackPressedDispatcher.onBackPressed()
                        finish()
                    }, 700)
                }
            } else {

                val request = PunchInRequest(
                    employeeId = getEmployeeDetails()?.id.toString(),
                    latitude = getLati.toString(),
                    longitude = getLongi.toString()
                )
                Log.d("res", "post: $getLati $getLongi   distance:")/* val request = PunchInRequest(
                     employeeId = getEmployeeDetails()?.id.toString(),
                     latitude = "23.6304733",
                     longitude = "88.4355422"
                 )*/
                settingsViewModel.punchInRequest(this, request)
                Log.d("res", "post: branch not assign")
            }


            /*   val request = PunchInRequest(
                   employeeId = getEmployeeDetails()?.id.toString(),
                   latitude = getLati.toString(),
                   longitude = getLongi.toString()
               )

              *//* val request = PunchInRequest(
                employeeId = getEmployeeDetails()?.id.toString(),
                latitude = "23.6304733",
                longitude = "88.4355422"
            )*/


            // settingsViewModel.punchInRequest(this, request)







        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation()
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

        settingsViewModel.mGetAttendanceBranchResponse.observe(this) { response ->
            if (response?.status == true) {
                Log.e("LocationDebug", "observeViewModel: ${response.data}")
                val branchData = response.data
                if (branchData != null && branchData.latitude != null && branchData.longitude != null) {

                    branchLat = branchData.latitude.toDouble()
                    branchLong = branchData.longitude.toDouble()
                    radar = branchData.radar.toFloat()
                    checkBranch = true

                } else checkBranch = false
            } else checkBranch = false
        }


    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    private fun enableMyLocation() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            try {
                mMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                Log.e("LocationError", "Security exception: ${e.message}")
            }

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                if (location != null) {
                    val isMock = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        location.isMock
                    } else {
                        location.isFromMockProvider
                    }

                    if (isMock) {
                        CustomToast(this, "Fake Location Detected! Please disable mock location apps.")
                        return@addOnSuccessListener
                    }

                    getLati = location.latitude
                    getLongi = location.longitude
                    Log.e("punchin", "$getLati $getLongi")

                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                } else {
                    CustomToast(this, "Unable to fetch current location. Ensure GPS is on.")
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1001
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }


    private fun calculateDistance(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }


}



