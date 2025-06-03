package com.stafo.app.screens.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.stafo.app.R
import com.stafo.app.databinding.ActivityPlaceSearchBinding
import com.stafo.app.screens.ui.adapter.PlacesAdapter


class PlaceSearchActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPlaceSearchBinding

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: PlacesAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var getLat:Double = 0.0
    private var getLon:Double = 0.0
    private var getAddress:String=""

    private var isSettingQueryProgrammatically = false
    private var sessionToken: AutocompleteSessionToken? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlaceSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBfO34QF_dUDQdeVW7wMTU-qsIPLBEU7Lw")
        }
        placesClient = Places.createClient(this)
        warmUpPlacesClient()
        onClickListener()


    }




    private fun onClickListener(){
        binding.apply {


            adapter = PlacesAdapter { placeId -> fetchPlaceDetails(placeId) }
            rvSearchResults.layoutManager = LinearLayoutManager(this@PlaceSearchActivity)
            rvSearchResults.adapter = adapter

            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this@PlaceSearchActivity)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@PlaceSearchActivity)

            idSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (sessionToken == null) {
                        sessionToken = AutocompleteSessionToken.newInstance()
                    }


                    if (isSettingQueryProgrammatically) {
                        isSettingQueryProgrammatically = false
                        return true
                    }

                    searchRunnable?.let { handler.removeCallbacks(it) }
                    searchRunnable = Runnable {
                        if (!newText.isNullOrEmpty() && newText.length >= 3) {
                            searchPlace(newText)
                        } else {
                            binding.rvSearchResults.visibility = View.GONE
                        }
                    }
                    handler.postDelayed(searchRunnable!!, 300)
                    return true
                }
            })

            btnAddAddress.setOnClickListener {
                val intent = Intent()
                intent.putExtra("type", "map")
                intent.putExtra("latitude", getLat)
                intent.putExtra("longitude", getLon)
                intent.putExtra("fullAddress", getAddress)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }


    private fun warmUpPlacesClient() {
        val warmupToken = AutocompleteSessionToken.newInstance()
        val warmupRequest = FindAutocompletePredictionsRequest.builder()
            .setQuery("del")
            .setCountries("IN")
            .setSessionToken(warmupToken)
            .build()

        placesClient.findAutocompletePredictions(warmupRequest)

            .addOnSuccessListener {
                Log.d("PlacesSearch", "Places SDK warmup complete")
            }
            .addOnFailureListener {
                Log.e("PlacesSearch", "Places SDK warmup failed: ${it.localizedMessage}")
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableLocation()
    }

    private fun enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    getLat=it.latitude
                    getLon=it.longitude
                    val latLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    map.addMarker(MarkerOptions().position(latLng).title("You are here"))

                    val geocoder = Geocoder(this)
                    try {
                        val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val addressLine = address.getAddressLine(0) // Full address
                            getAddress=addressLine

                            Log.d("LocationAddress", "Current address: $addressLine")
                            // You can show this address in UI, e.g.:
                            // binding.addressTextView.text = addressLine
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }
    }

    private fun searchPlace(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .setCountries("IN")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                if (predictions.isNotEmpty()) {
                    adapter.submitList(predictions)
                    binding.rvSearchResults.visibility = View.VISIBLE
                } else {
                    binding.rvSearchResults.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Log.e("PlacesSearch", "Search failed: ${e.localizedMessage}")
            }
    }

    private fun fetchPlaceDetails(placeId: String) {
        val request = FetchPlaceRequest.builder(
            placeId,
            listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS) // Add ADDRESS here
        ).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                Log.e("PlacesSearch", "get lat long :${place.latLng}")
                place.latLng?.let {
                    getLat = it.latitude
                    getLon = it.longitude
                    map.clear()
                    map.addMarker(MarkerOptions().position(it).title(place.name))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
                    binding.rvSearchResults.visibility = View.GONE

                    val placeLabel = place.address ?: place.name ?: ""
                    getAddress = placeLabel

                    isSettingQueryProgrammatically = true
                    binding.idSearchView.setQuery(placeLabel, false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PlacesSearch", "Place details failed: ${e.localizedMessage}")
            }
    }


    /* private fun fetchPlaceDetails(placeId: String) {
         val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG, Place.Field.NAME)).build()

         placesClient.fetchPlace(request)
             .addOnSuccessListener { response ->
                 val place = response.place
                 Log.e("PlacesSearch", "get lat long :${place.latLng}")
                 place.latLng?.let {
                     getLat=it.latitude
                     getLon=it.longitude
                     map.clear()
                     map.addMarker(MarkerOptions().position(it).title(place.name))
                     map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
                     binding.rvSearchResults.visibility = View.GONE

                     val placeLabel = place.address ?: place.name ?: ""
                     getAddress=placeLabel

                     isSettingQueryProgrammatically = true
                     binding.idSearchView.setQuery(placeLabel, false)
                 }
             }
             .addOnFailureListener { e ->
                 Log.e("PlacesSearch", "Place details failed: ${e.localizedMessage}")
             }
     }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocation()
        }
    }


}

