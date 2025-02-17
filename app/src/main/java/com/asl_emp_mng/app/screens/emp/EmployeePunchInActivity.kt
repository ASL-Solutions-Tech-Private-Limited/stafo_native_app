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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.IOException


class EmployeePunchInActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEmployeePunchInBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var mapView: org.osmdroid.views.MapView
    private val client = OkHttpClient()
    private var currentMarker: Marker? = null
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

        Configuration.getInstance()
            .load(applicationContext, getSharedPreferences("osm_prefs", MODE_PRIVATE))




        mapView = binding.mapView

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        getPlaceNameFromLatLng(latitude, longitude)



        onClickListener()
        observeViewModel()
        startLocationService()
    }

    private fun onClickListener() {


        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchLocation(query)
            }
        }


        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: org.osmdroid.util.GeoPoint?): Boolean {
                p?.let {
                    getPlaceNameFromLatLng(it.latitude, it.longitude)
                }
                return true
            }

            override fun longPressHelper(p: org.osmdroid.util.GeoPoint?): Boolean {
                return false
            }
        }
        val overlayEvents = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(overlayEvents)


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

            settingsViewModel.punchInRequest(this, request)


        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mPunchInResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
            } else {
                CustomToast(this, it.message)
            }


        }


    }


    private fun getPlaceNameFromLatLng(latitude: Double, longitude: Double) {
        getLati = latitude
        getLongi = longitude

        val url =
            "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude"

        val request = Request.Builder().url(url)
            .header("User-Agent", "YourAppName")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val responseData = responseBody.string()
                    val jsonObject = JSONObject(responseData)
                    val displayName = jsonObject.optString("display_name", "Unknown Location")

                    runOnUiThread {
                        updateMap(latitude, longitude, displayName)
                    }
                }
            }
        })
    }

    private fun searchLocation(query: String) {
        val url = "https://nominatim.openstreetmap.org/search?format=json&q=$query"

        val request = Request.Builder().url(url)
            .header("User-Agent", "YourAppName")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val responseData = responseBody.string()
                    val jsonArray = JSONArray(responseData)

                    if (jsonArray.length() > 0) {
                        val firstResult: JSONObject = jsonArray.getJSONObject(0)
                        val lat = firstResult.getDouble("lat")
                        val lon = firstResult.getDouble("lon")
                        val displayName = firstResult.getString("display_name")


                        runOnUiThread {
                            updateMap(lat, lon, displayName)
                        }
                    }
                }
            }
        })
    }

    private fun updateMap(latitude: Double, longitude: Double, placeName: String) {
        getLati = latitude
        getLongi = longitude

        val geoPoint = org.osmdroid.util.GeoPoint(latitude, longitude)
        mapView.controller.animateTo(geoPoint)
        mapView.controller.setZoom(15.0)

        // Remove the previous marker
        currentMarker?.let {
            mapView.overlays.remove(it)
        }

        val displayName = if (!placeName.isNullOrEmpty()) placeName else "Unknown Location"
        // Create a new marker
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = displayName
        //marker.snippet = "Lat: $latitude, Lon: $longitude"
        marker.setOnMarkerClickListener { m, _ ->
            m.showInfoWindow() // Show place name when clicked
            true
        }

        // Add the marker to the map
        mapView.overlays.add(marker)
        marker.showInfoWindow() // Show place name immediately
        mapView.invalidate()

        // Update current marker reference
        currentMarker = marker


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
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start(req)
    }


    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }


}