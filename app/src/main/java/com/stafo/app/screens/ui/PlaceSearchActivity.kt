package com.stafo.app.screens.ui

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.mmi.services.api.PlaceResponse
import com.mmi.util.GeoPoint
import com.stafo.app.screens.auth.AuthViewModel
import com.stafo.app.screens.auth.dataClass.DataBusinessType
import com.stafo.app.screens.auth.dataClass.DataCity
import com.stafo.app.screens.auth.dataClass.DataCompanyType
import com.stafo.app.screens.auth.dataClass.DataCountry
import com.stafo.app.screens.auth.dataClass.DataStates
import com.stafo.app.screens.settings.SettingsViewModel
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
    private var getLati: Double? = null
    private var getLongi: Double? = null

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
                            val returnIntent = Intent()
                            returnIntent.putExtra("type", "custom")
                            returnIntent.putExtra("fullAddress", fullAddress)
                            setResult(Activity.RESULT_OK, returnIntent)
                            finish()
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
