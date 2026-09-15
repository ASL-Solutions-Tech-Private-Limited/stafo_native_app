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
import com.google.maps.android.PolyUtil
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


    private lateinit var mEMPID: String
    private var mSelectedDate = ""
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

        val request = GeoLocationHistResquest(
            employee_id = mEMPID, date = mSelectedDate
        )
        settingsViewModel.getGeoLocationHist(this, request)


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
            if (tag is String) {
                showHaltTooltip(tag)
                marker.showInfoWindow()
                true
            } else false
        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }


        settingsViewModel.mGeoLocationHistResponse.observe(this) {
            if (it.status && it.data.isNotEmpty()) {
                binding.llMap.visibility = View.VISIBLE
                binding.layoutNotView.visibility = View.GONE
                val geoPoints = it.data.map { point ->
                    TimedGeoPoint(
                        latitude = point.latitude.toDouble(),
                        longitude = point.longitude.toDouble(),
                        timestamp = parseTimestamp(point.createdAt),
                        batteryPercentage = point.batteryPercentage
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

        val simplifiedGeoPoints = PolyUtil.simplify(geoPoints, 5.0)
        googleMap.addPolyline(
            PolylineOptions().addAll(simplifiedGeoPoints)
                .color(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
                .width(10f)
        )

        val halts = detectHalts(points)

        val startPoint = points.first()
        val endPoint = points.last()

        val startHalt = halts.find { calculateDistance(it.latitude, it.longitude, startPoint.latitude, startPoint.longitude) <= 100.0 }
        var startTitle = "Start Point"
        if (startHalt != null) startTitle += "\n(Halt: ${String.format("%.1f", startHalt.duration / 60000.0)} mins)"
        if (startPoint.batteryPercentage != null) startTitle += "\nBattery: ${startPoint.batteryPercentage}%"
        addCustomMarker(geoPoints.first(), startTitle, MarkerType.START, 0, startPoint)

        val endHalt = halts.find { calculateDistance(it.latitude, it.longitude, endPoint.latitude, endPoint.longitude) <= 100.0 && it != startHalt }
        var endTitle = "End Point"
        if (endHalt != null) endTitle += "\n(Halt: ${String.format("%.1f", endHalt.duration / 60000.0)} mins)"
        if (endPoint.batteryPercentage != null) endTitle += "\nBattery: ${endPoint.batteryPercentage}%"
        addCustomMarker(geoPoints.last(), endTitle, MarkerType.END, 0, endPoint)

        val intermediateHalts = halts.filter { it != startHalt && it != endHalt }
        intermediateHalts.forEachIndexed { index, halt ->
            val latLng = LatLng(halt.latitude, halt.longitude)
            val duration = halt.duration / 60000.0
            var title = "Halt #${index + 1}\nDuration: ${String.format("%.1f", duration)} mins"
            if (halt.batteryPercentage != null) title += "\nBattery: ${halt.batteryPercentage}%"
            addCustomMarker(latLng, title, MarkerType.HALT, index + 1, TimedGeoPoint(halt.latitude, halt.longitude, halt.startTime, halt.batteryPercentage))
        }

        val bounds = LatLngBounds.builder().apply {
            simplifiedGeoPoints.forEach { include(it) }
        }.build()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun detectHalts(points: List<TimedGeoPoint>): List<HaltInfo> {
        val halts = mutableListOf<HaltInfo>()
        val radius = 200.0
        val minHaltDuration = 10 * 60 * 1000 // 10 mins

        var startIndex = 0
        while (startIndex < points.size - 1) {
            var endIndex = startIndex + 1
            while (endIndex < points.size && isWithinRadius(points[startIndex], points[endIndex], radius)) {
                endIndex++
            }
            val duration = points[endIndex - 1].timestamp - points[startIndex].timestamp
            if (duration >= minHaltDuration) {
                val avgLat = points.subList(startIndex, endIndex).map { it.latitude }.average()
                val avgLng = points.subList(startIndex, endIndex).map { it.longitude }.average()
                halts.add(HaltInfo(avgLat, avgLng, duration, points[startIndex].timestamp, points[endIndex - 1].timestamp, points[startIndex].batteryPercentage))
            }
            startIndex = endIndex
        }
        return halts
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
        pointData: TimedGeoPoint? = null
    ) {
        val markerOptions = MarkerOptions().position(position).title(title)

        when (markerType) {
            MarkerType.START -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            MarkerType.END -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            MarkerType.HALT -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        }

        val marker = googleMap.addMarker(markerOptions)
        if (marker != null && pointData != null) {
            marker.tag = title
        }
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


    private fun showHaltTooltip(title: String) {
        Toast.makeText(this, title, Toast.LENGTH_LONG).show()
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
        val formats = listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss")
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                return sdf.parse(dateTime)?.time ?: continue
            } catch (e: Exception) {}
        }
        return 0L
    }


    data class TimedGeoPoint(val latitude: Double, val longitude: Double, val timestamp: Long, val batteryPercentage: String? = null)
    data class HaltInfo(val latitude: Double, val longitude: Double, val duration: Long, val startTime: Long, val endTime: Long, val batteryPercentage: String? = null)
}

