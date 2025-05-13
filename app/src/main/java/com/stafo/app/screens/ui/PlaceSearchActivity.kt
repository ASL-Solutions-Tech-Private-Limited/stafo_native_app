package com.stafo.app.screens.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.textfield.TextInputEditText
import com.stafo.app.R
import com.stafo.app.databinding.ActivityPlaceSearchBinding
import java.util.Locale
import com.mmi.MapView
import com.mmi.MapmyIndiaMapView
import com.mmi.layers.MapEventsOverlay
import com.mmi.layers.MapEventsReceiver
import com.mmi.layers.Marker
import com.mmi.layers.UserLocationOverlay
import com.mmi.layers.location.GpsLocationProvider
import com.mmi.util.GeoPoint
import com.stafo.app.screens.auth.AuthViewModel
import com.stafo.app.screens.auth.dataClass.DataCity
import com.stafo.app.screens.auth.dataClass.DataCountry
import com.stafo.app.screens.auth.dataClass.DataStates
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.Place
import com.stafo.app.screens.settings.dataClass.PlacesAdapter
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class PlaceSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceSearchBinding
    private var getLati: Double = 0.0
    private var getLongi: Double = 0.0

    private lateinit var userLocationOverlay: UserLocationOverlay
    private lateinit var mapView: MapView
    private lateinit var marker: Marker


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    // for update company type , business type ,country,state, city,

    private val viewModel: AuthViewModel by viewModels()


    private var mCountryList: ArrayList<DataCountry>? = ArrayList()
    private var mStateList: ArrayList<DataStates>? = ArrayList()
    private var mCityList: ArrayList<DataCity>? = ArrayList()

    private lateinit var countryDialog: SearchableDialog
    private lateinit var stateDialog: SearchableDialog
    private lateinit var cityDialog: SearchableDialog
    private var selectedCountry: String = ""
    private var selectedState: String = ""
    private var selectedCity: String = ""

    private var addressType: String = "map"
   // private var accessToken: String = ""

   // val placesList = mutableListOf<Place>()

   // private lateinit var adapter:PlacesAdapter

   // val adapter = PlacesAdapter(placesList,getLati!!,getLongi!!)

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

        onClickListener()
        observeAuthViewModel()




    }


    private fun onClickListener() {

        binding.apply {

         /*   etDirSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) {
                        if (s.length >= 5) {

                            callMapMyIndiaPlaceSearch(s.toString(), accessToken)
                        }
                    } else {

                        placesList.clear()
                        adapter=PlacesAdapter(placesList,getLati,getLongi)
                        binding.recyclerView.layoutManager = LinearLayoutManager(this@PlaceSearchActivity)
                        binding.recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })*/









            val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
            mapView = mapmyIndiaMapView.mapView
            userLocationOverlay =
                UserLocationOverlay(GpsLocationProvider(this@PlaceSearchActivity), mapView)
            userLocationOverlay.enableMyLocation()
            mapView.overlays.add(userLocationOverlay)
            mapView.invalidate()
            marker = Marker(mapView)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)

            userLocationOverlay.runOnFirstFix {
                val userLocation = userLocationOverlay.myLocation
                getLati = userLocation.latitude
                getLongi = userLocation.longitude
                if (userLocation != null) {
                    runOnUiThread {
                        moveMarker(userLocation)
                    }
                }
            }
            setUpMapClickListener()


            binding.rdgpProfile.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_map -> {
                        addressType = "map"
                        binding.idMapView.visibility = View.VISIBLE
                        binding.llcCustom.visibility = View.GONE
                    }

                    R.id.radio_custom -> {
                        addressType = "custom"
                        binding.idMapView.visibility = View.GONE
                        binding.llcCustom.visibility = View.VISIBLE
                    }

                }


            }




            tieSelectCountry.setOnClickListener { countryDialog.show() }
            tieSelectState.setOnClickListener { validateAndShowStateDialog() }
            tieSelectCity.setOnClickListener { validateAndShowCityDialog() }







            binding.btnAddAddress.setOnClickListener {


                when (addressType) {
                    "map" -> {
                        val returnIntent = Intent()
                        returnIntent.putExtra("type", "map")
                        returnIntent.putExtra("latitude", getLati)
                        returnIntent.putExtra("longitude", getLongi)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    "custom" -> {
                        val address = binding.tieCompanyAddress.text.toString()
                        val pin = binding.tieZipcode.text.toString()
                        val fullAddress =
                            "$address,$selectedCity,$selectedState,$pin,$selectedCountry"

                        if (isValidation()) {

                            getLatLngFromAddress(this@PlaceSearchActivity, fullAddress)

                        }


                    }
                }

            }
        }

    }





    private fun setUpMapClickListener() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {


                    getLati = it.latitude
                    getLongi = it.longitude

                    Log.d("MapTap", "Tapped Location: Lat=${getLati}, Lng=${getLongi}")
                    moveMarker(it)
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(this, mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)
    }


    private fun moveMarker(location: GeoPoint) {
        marker.position = location
        mapView.invalidate()
        mapView.setCenter(location)
        mapView.setZoom(13)
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


    private fun observeAuthViewModel() {
        viewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        viewModel.getCountryList(this)
        viewModel.mCountryResponse.observe(this) {
            if (it.success) {
                mCountryList = it.data
                binding.let { it1 ->
                    setupSearchableDialog(
                        mCountryList, "Country", it1.tieSelectCountry
                    )
                }
            }
        }

        viewModel.mStateResponse.observe(this) {
            if (it.success) {
                mStateList = it.data
                binding.let { it1 ->
                    setupSearchableDialog(
                        mStateList, "State", it1.tieSelectState
                    )
                }
            }
        }

        viewModel.mCityResponse.observe(this) {
            if (it.success) {
                mCityList = it.data
                binding.let { it1 ->
                    setupSearchableDialog(
                        mCityList, "City", it1.tieSelectCity
                    )
                }
            }
        }
    }


   /* fun getMapMyIndiaAccessToken() {
        val clientId="96dHZVzsAuveHJyb4fsrVuXD0YNPrFaochM2cB-f7hG7DijsK6wuIGwWgAo7ksFFxTVpPm2mORP_XLz9OkWc1Q=="
        val clientSecret="lrFxI-iSEg9UFw9ZECaYSUPOvunPyH3qtIQyBP0lo-8yMBn9fNnEUP8xU44RbKPf-yq4d7x-H1T6fo1qyZRdt7x6r4gib2ys"


        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url("https://outpost.mapmyindia.com/api/security/oauth/token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        println("Failed: ${it.code} ${it.message}")
                        return
                    }

                    val responseBody = it.body?.string()
                    if (responseBody != null) {
                        val json = JSONObject(responseBody)
                        accessToken = json.optString("access_token")

                        Log.d("res","Access Token: $accessToken")

                    }
                }
            }
        })
    }





    fun callMapMyIndiaPlaceSearch(query: String, accessToken: String) {


        val client = OkHttpClient()

        val url = "https://atlas.mapmyindia.com/api/places/search/json?query=$query"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        println("Request failed: ${it.code}")
                        Log.d("res","Request failed: ${it.code}")
                        return
                    }

                    val responseBody = it.body?.string()
                    if (responseBody != null) {
                        val json = JSONObject(responseBody)
                        val suggestions = json.optJSONArray("suggestedLocations")

                        placesList.clear()

                        for (i in 0 until suggestions.length()) {
                            val place = suggestions.getJSONObject(i)
                            val placeName = place.optString("placeName")
                            val placeAddress = place.optString("placeAddress")

                            var lat: Double? = null
                            var lng: Double? = null

                            try {
                                val geocoder = Geocoder(this@PlaceSearchActivity, Locale.getDefault())
                                val addressList = geocoder.getFromLocationName(placeAddress, 1)
                                if (!addressList.isNullOrEmpty()) {
                                    lat = addressList[0].latitude
                                    lng = addressList[0].longitude
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            placesList.add(Place(placeName, placeAddress, lat, lng))

                        }

                        runOnUiThread {
                            adapter=PlacesAdapter(placesList,getLati,getLongi)
                            binding.recyclerView.layoutManager = LinearLayoutManager(this@PlaceSearchActivity)
                            binding.recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }


            *//*override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        println("Request failed: ${it.code}")
                        return
                    }

                    val responseBody = it.body?.string()
                    if (responseBody != null) {

                        if (responseBody != null) {
                            val json = JSONObject(responseBody)
                            val suggestions = json.optJSONArray("suggestedLocations")

                            placesList.clear()

                            for (i in 0 until suggestions.length()) {
                                val place = suggestions.getJSONObject(i)
                                val placeName = place.optString("placeName")
                                val placeAddress = place.optString("placeAddress")

                                placesList.add(Place(placeName, placeAddress))
                            }


                            runOnUiThread {
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }*//*
        })
    }*/



    private fun getLatLngFromAddress(context: Context, mAddress: String) {
        val coder = Geocoder(context)
        try {
            val addressList: List<Address>? = coder.getFromLocationName(mAddress, 5)
            if (addressList.isNullOrEmpty()) {
                Log.d("res", "Fail to find Lat,Lng")
                Log.d("MapTap", "Fail to find Lat,Lng")
                return
            }

            val location = addressList[0]
            getLati = location.latitude
            getLongi = location.longitude

            val returnIntent = Intent().apply {
                putExtra("type", "custom")
                putExtra("fullAddress", mAddress)
                putExtra("latitude", getLati)
                putExtra("longitude", getLongi)
            }

            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            Log.d("MapTap", "Custom Location: Lat=${getLati}, Lng=${getLongi}")

        } catch (e: Exception) {
            Log.d("res", "Fail to find Lat,Lng: ${e.localizedMessage}")
        }
    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun validateAndShowStateDialog() {
        if (binding.tieSelectCountry.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please select country first", Toast.LENGTH_SHORT).show()
        } else {
            stateDialog.show()
        }
    }

    private fun validateAndShowCityDialog() {
        if (binding.tieSelectState.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please select state first", Toast.LENGTH_SHORT).show()
        } else {
            cityDialog.show()
        }
    }

    private fun setupSearchableDialog(
        dataList: List<Any>?, title: String, field: TextInputEditText
    ) {
        val items = dataList?.map {
            val name = when (it) {
                is DataCountry -> it.name
                is DataStates -> it.name
                is DataCity -> it.name
                else -> "Unknown"
            }

            val id = when (it) {
                is DataCountry -> it.id
                is DataStates -> it.id
                is DataCity -> it.id
                else -> -1
            }

            SearchListItem(id, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)

                when (title) {
                    "Country" -> {
                        selectedCountry = searchListItem.title
                        Log.d("res", "get : $selectedCountry $searchListItem.title")
                    }

                    "State" -> {
                        selectedState = searchListItem.title
                        Log.d("res", "get : $selectedState $searchListItem.title")

                    }

                    "City" -> {
                        selectedCity = searchListItem.title
                        Log.d("res", "get : $selectedCity $searchListItem.title")

                    }
                }


                dialog.dismiss()


                when (field) {

                    binding.tieSelectCountry -> viewModel.getStateList(
                        this@PlaceSearchActivity, searchListItem.id.toString()
                    )

                    binding.tieSelectState -> viewModel.getCityList(
                        this@PlaceSearchActivity, searchListItem.id.toString()


                    )
                }
            }
        })
        when (title) {
            "Country" -> countryDialog = dialog
            "State" -> stateDialog = dialog
            "City" -> cityDialog = dialog
        }
    }


    private fun isValidation(): Boolean {
        return listOf(
            binding.tieSelectCountry to "Please select country",
            binding.tieSelectState to "Please select state",
            binding.tieSelectCity to "Please select city",
            binding.tieZipcode to "Please enter zip code",
            binding.tieCompanyAddress to "Please enter address"
        ).all { validateField(it.first, it.second) }
    }

    private fun validateField(view: TextInputEditText?, errorMsg: String): Boolean {
        return if (view?.text.isNullOrEmpty()) {
            CustomToast(this, errorMsg)
            view?.requestFocus()
            false
        } else {
            true
        }
    }


}
