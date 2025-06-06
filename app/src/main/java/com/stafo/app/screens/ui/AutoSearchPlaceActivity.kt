package com.stafo.app.screens.ui

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.stafo.app.R
import com.stafo.app.databinding.ActivityAutoSearchPlaceBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GeoLocationHistResquest
import com.stafo.app.screens.tripPlan.TripViewModel
import com.stafo.app.screens.tripPlan.dataClass.TripGeoLocationListRequest
import com.stafo.app.utils.CustomLoader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AutoSearchPlaceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityAutoSearchPlaceBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val tripViewModel: TripViewModel by viewModels()

    private lateinit var mEMPID: String
    private var mSelectedDate = ""
    private var mTripID = "22"
    private val calendar = Calendar.getInstance()

    private lateinit var googleMap: GoogleMap

    enum class MarkerType { START, END, HALT }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutoSearchPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        mEMPID = intent.getStringExtra("EMP_ID") ?: ""
        mSelectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initUI()
        fetchLocationData()
        observeViewModel()
    }


    private fun fetchLocationData() {

        Log.e("trip", "TripID: $mTripID, Date: $mSelectedDate")

        if (mTripID.isNullOrBlank()) {
            Log.e("trip", "Fetching employee history")
            // ...

            val request = GeoLocationHistResquest(
                employee_id = mEMPID, date = mSelectedDate
            )
            settingsViewModel.getGeoLocationHist(this, request)
        } else {
            Log.e("trip", "Fetching trip location")
            // ...

            val request = TripGeoLocationListRequest(
                trip_id = mTripID

            )
            tripViewModel.getTripGeoLocation(this, request)
        }




    }
    private fun initUI() {
        binding.apply {
            txtDate.text = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(calendar.time)
            imageBack.setOnClickListener { finish() }
            llCalendar.setOnClickListener { showDatePicker() }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true


        googleMap.setOnMarkerClickListener { marker ->
            val tag = marker.tag
            if (tag is Pair<*, *>) {
                val haltIndex = tag.first as? Int ?: 0
                val duration = tag.second as? Double ?: 0.0
                showHaltTooltip(haltIndex, duration)
                true
            } else false
        }
    }



    private fun observeViewModel() {
        tripViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }


        settingsViewModel.mGeoLocationHistResponse.observe(this) {
            if (it.status && it.data.isNotEmpty()) {
                binding.llMap.visibility = View.VISIBLE
                binding.layoutNotView.visibility = View.GONE
                val geoPoints = it.data.map { point ->
                    TimedGeoPoint(
                        latitude = point.latitude.toDouble(),
                        longitude = point.longitude.toDouble(),
                        timestamp = parseTimestamp(point.createdAt)
                    )
                }
                drawRouteWithHalts(geoPoints)
            } else {
                binding.llMap.visibility = View.GONE
                binding.layoutNotView.visibility = View.VISIBLE
                //CustomToast(this, it.message)
            }
        }
        tripViewModel.mTripGeoLocationListResponse.observe(this) {
            if (it.status && it.data.isNotEmpty()) {
                binding.llMap.visibility = View.VISIBLE
                binding.layoutNotView.visibility = View.GONE
                val geoPoints = it.data.map { point ->
                    TimedGeoPoint(
                        latitude = point.latitude.toDouble(),
                        longitude = point.longitude.toDouble(),
                        timestamp = parseTimestamp(point.created_at)
                    )
                }
                drawRouteWithHalts(geoPoints)
            } else {
                binding.llMap.visibility = View.GONE
                binding.layoutNotView.visibility = View.VISIBLE
                //CustomToast(this, it.message)
            }
        }



    }

    private fun handleLoader(status: String) {
        if (status.equals("load", true)) customLoader.show() else customLoader.dismiss()
    }

    private fun drawRouteWithHalts(points: List<TimedGeoPoint>) {
        googleMap.clear()
        if (points.isEmpty()) return

        val geoPoints = points.map { LatLng(it.latitude, it.longitude) }

        // Start Marker
        val startHalt = checkIfPointIsHalt(points.first(), points)
        val startTitle = if (startHalt != null) "Start Point\n(Halt: ${
            String.format(
                "%.1f",
                startHalt / 60000.0
            )
        } mins)" else "Start Point"
        addCustomMarker(geoPoints.first(), startTitle, MarkerType.START)

        // End Marker
        val endHalt = checkIfPointIsHalt(points.last(), points)
        val endTitle = if (endHalt != null) "End Point\n(Halt: ${
            String.format(
                "%.1f",
                endHalt / 60000.0
            )
        } mins)" else "End Point"
        addCustomMarker(geoPoints.last(), endTitle, MarkerType.END)

        // Draw polyline
        googleMap.addPolyline(
            PolylineOptions().addAll(geoPoints)
                .color(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                .width(10f)
        )

        // Intermediate Halts
        val haltMarkers = detectHalts(points)
        val start = points.first()
        val end = points.last()
        val filteredHalts = haltMarkers.filter {
            calculateDistance(it.latitude, it.longitude, start.latitude, start.longitude) > 50 &&
                    calculateDistance(it.latitude, it.longitude, end.latitude, end.longitude) > 50
        }

        filteredHalts.forEachIndexed { index, halt ->
            val latLng = LatLng(halt.latitude, halt.longitude)
            val duration = halt.duration / 60000.0
            addCustomMarker(
                latLng,
                "Halt #${index + 1}\n${String.format("%.1f", duration)} mins",
                MarkerType.HALT,
                index + 1,
                duration
            )
        }

        // Camera Bounds
        val bounds = LatLngBounds.builder().apply {
            geoPoints.forEach { include(it) }
        }.build()

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun detectHalts(points: List<TimedGeoPoint>): List<HaltInfo> {
        val halts = mutableListOf<HaltInfo>()
        val radius = 200.0
        val minHaltDuration = 10 * 60 * 1000 // 30 mins

        var startIndex = 0
        while (startIndex < points.size - 1) {
            var endIndex = startIndex + 1
            while (endIndex < points.size && isWithinRadius(
                    points[startIndex],
                    points[endIndex],
                    radius
                )
            ) {
                endIndex++
            }
            val duration = points[endIndex - 1].timestamp - points[startIndex].timestamp
            if (duration >= minHaltDuration) {
                val avgLat = points.subList(startIndex, endIndex).map { it.latitude }.average()
                val avgLng = points.subList(startIndex, endIndex).map { it.longitude }.average()
                halts.add(HaltInfo(avgLat, avgLng, duration))
            }
            startIndex = endIndex
        }
        return halts
    }

    private fun checkIfPointIsHalt(point: TimedGeoPoint, all: List<TimedGeoPoint>): Long? {
        val nearby = all.filter { isWithinRadius(point, it, 200.0) }
        if (nearby.size < 2) return null
        val duration = nearby.last().timestamp - nearby.first().timestamp
        return if (duration >= 30 * 60 * 1000) duration else null
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0]
    }

    private fun isWithinRadius(p1: TimedGeoPoint, p2: TimedGeoPoint, radius: Double): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, result)
        return result[0] <= radius
    }

    private fun addCustomMarker(
        position: LatLng,
        title: String,
        markerType: MarkerType,
        haltIndex: Int = 0,
        durationMinutes: Double = 0.0
    ) {
        val markerOptions = MarkerOptions()
            .position(position)
            .title(title)

        // Use default markers
        when (markerType) {
            MarkerType.START -> {
                // Use default start marker with no custom icon (or use any color if desired)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }

            MarkerType.END -> {
                // Use default end marker with no custom icon (or use any color if desired)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }

            MarkerType.HALT -> {
                // Use default halt marker with no custom icon (or use any color if desired)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            }
        }

        // Add the marker to the map
        val marker = googleMap.addMarker(markerOptions)

        // If it's a halt marker, attach additional data (e.g., halt index and duration)
        if (markerType == MarkerType.HALT) {
            marker?.tag = Pair(haltIndex, durationMinutes)
        }

        Log.d("MarkerAdded", "Added ${markerType.name} marker at $position with title: $title")
    }


    private fun getBitmapFromVectorDrawable(@DrawableRes drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(this, drawableId)!!
        val bitmap =
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    private fun showHaltTooltip(haltNumber: Int, durationMinutes: Double) {
        val message =
            "Halt #$haltNumber\nStayed for ${String.format("%.1f", durationMinutes)} minutes"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                binding.txtDate.text =
                    SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(calendar.time)
                mSelectedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                fetchLocationData()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun parseTimestamp(dateTime: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.parse(dateTime)?.time ?: 0L
    }


    data class TimedGeoPoint(val latitude: Double, val longitude: Double, val timestamp: Long)
    data class HaltInfo(val latitude: Double, val longitude: Double, val duration: Long)
}

