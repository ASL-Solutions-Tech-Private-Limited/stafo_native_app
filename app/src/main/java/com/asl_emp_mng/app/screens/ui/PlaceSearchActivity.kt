package com.asl_emp_mng.app.screens.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.PunchInType
import com.asl_emp_mng.app.databinding.ActivityPlaceSearchBinding
import com.asl_emp_mng.app.screens.settings.dataClass.PunchInRequest
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.mmi.MapmyIndiaMapView
import com.mmi.layers.UserLocationOverlay
import com.mmi.layers.location.GpsLocationProvider
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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.Locale

class PlaceSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceSearchBinding
  /*  private lateinit var mapView: MapView
    private val client = OkHttpClient()

    private var currentMarker: Marker? = null*/
    private var getLati: Double? = null
    private var getLongi: Double? = null

    private lateinit var userLocationOverlay: UserLocationOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityPlaceSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

/*
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

        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm_prefs", MODE_PRIVATE))*/


/*

        mapView = binding.mapView

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)


        getPlaceNameFromLatLng(latitude,longitude)

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchLocation(query)
            }
        }



        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    getPlaceNameFromLatLng(it.latitude, it.longitude)
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }
        val overlayEvents = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(overlayEvents)*/


        onClickListener()



    }


    private fun onClickListener() {

        val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
        val mapView = mapmyIndiaMapView.mapView

        // Enable User Location Tracking
        userLocationOverlay = UserLocationOverlay(GpsLocationProvider(this), mapView)
        userLocationOverlay.enableMyLocation()
        mapView.overlays.add(userLocationOverlay)
        mapView.invalidate()

        // Set Marker at User's Location
        userLocationOverlay.runOnFirstFix {
            val userLocation = userLocationOverlay.myLocation
            getLati=userLocation.latitude
            getLongi=userLocation.longitude

            getAddressFromLocation(userLocation.latitude, userLocation.longitude)

            if (userLocation != null) {
                runOnUiThread {
                    val marker = com.mmi.layers.Marker(mapView)
                    marker.position = userLocation
                    marker.setAnchor(com.mmi.layers.Marker.ANCHOR_CENTER, com.mmi.layers.Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)
                    mapView.invalidate()
                    mapView.setCenter(userLocation)
                    mapView.setZoom(13)
                }
            }
        }

        binding.btnAddAddress.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("latitude", getLati)
            returnIntent.putExtra("longitude", getLongi)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }



    }


    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                runOnUiThread {
                    Log.d("Location", "Address: $address")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Location", "Geocoder failed: ${e.message}")
        }
    }





   /* private fun getPlaceNameFromLatLng(latitude: Double, longitude: Double) {
        val url =
            "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude"

        val request = Request.Builder().url(url)
            .header("User-Agent", "YourAppName") // Required for OpenStreetMap API
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
        getLati=latitude
        getLongi=longitude

        val geoPoint = GeoPoint(latitude, longitude)
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


    }*/
}