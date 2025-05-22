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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mappls.sdk.maps.MapplsMap
import com.mappls.sdk.maps.annotations.Icon
import com.mappls.sdk.maps.annotations.IconFactory
import com.mappls.sdk.maps.annotations.MarkerOptions
import com.mappls.sdk.maps.annotations.PolylineOptions
import com.mappls.sdk.maps.geometry.LatLng
import com.mmi.MapView
import com.mmi.layers.Marker
import com.mmi.layers.PathOverlay
import com.mmi.util.GeoPoint
import com.stafo.app.R
import com.stafo.app.databinding.ActivityAutoSearchPlaceBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GeoLocationHistResquest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AutoSearchPlaceActivity : AppCompatActivity() {
    private val TAG = "AutoSearchPlaceActivity"
    private lateinit var binding: ActivityAutoSearchPlaceBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var mEMPID: String
    private var mSelectedDate = ""
    private val calendar = Calendar.getInstance()

    // Marker types enum for better organization
    enum class MarkerType {
        START, END, HALT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutoSearchPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        mEMPID = intent.getStringExtra("EMP_ID") ?: ""
        mSelectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        initUI()
        observeViewModel()



    }

    private fun initUI() {
        binding.apply {
            txtDate.text = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(calendar.time)

            imageBack.setOnClickListener { finish() }

            llCalendar.setOnClickListener { showDatePicker() }

            fetchLocationData()
        }
    }

    private fun fetchLocationData() {
        val request = GeoLocationHistResquest(
            employee_id = mEMPID, date = mSelectedDate
        )
        settingsViewModel.getGeoLocationHist(this, request)
    }

    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGeoLocationHistResponse.observe(this) {
            if (it.status && it.data.isNotEmpty()) {
                binding.idMapView.visibility = View.VISIBLE
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
                binding.idMapView.visibility = View.GONE
                binding.layoutNotView.visibility = View.VISIBLE
                CustomToast(this, it.message)
            }
        }
    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun drawRouteWithHalts(points: List<TimedGeoPoint>) {
        val mapView = binding.idMapView.mapView
        mapView.overlays.clear()
        if (points.isEmpty()) return

        val geoPoints = points.map { GeoPoint(it.latitude, it.longitude) }

        // Check if start point is also a halt
        val startHalt = checkIfPointIsHalt(points.first(), points)
        val startTitle = if (startHalt != null) {
            "Start Point\n(Halt: ${String.format("%.1f", startHalt / (60.0 * 1000.0))} mins)"
        } else {
            "Start Point"
        }
        addCustomMarker(mapView, geoPoints.first(), startTitle, MarkerType.START)

        // Check if end point is also a halt
        val endHalt = checkIfPointIsHalt(points.last(), points)
        val endTitle = if (endHalt != null) {
            "End Point\n(Halt: ${String.format("%.1f", endHalt / (60.0 * 1000.0))} mins)"
        } else {
            "End Point"
        }
        addCustomMarker(mapView, geoPoints.last(), endTitle, MarkerType.END)

        // Route Polyline
        val pathOverlay = PathOverlay(this).apply {
            color =
                ContextCompat.getColor(this@AutoSearchPlaceActivity, android.R.color.holo_red_dark)
            width = 10f
            this.points = geoPoints
        }
        mapView.overlays.add(pathOverlay)

        // Halt Detection (excluding start and end points to avoid duplication)
        val haltMarkers = detectHalts(points)
        val startPoint = points.first()
        val endPoint = points.last()

        // Filter out halts that are too close to start or end points (to avoid duplicate markers)
        val filteredHalts = haltMarkers.filter { halt ->
            val distanceFromStart = calculateDistance(
                halt.latitude,
                halt.longitude,
                startPoint.latitude,
                startPoint.longitude
            )
            val distanceFromEnd = calculateDistance(
                halt.latitude,
                halt.longitude,
                endPoint.latitude,
                endPoint.longitude
            )
            distanceFromStart > 50 && distanceFromEnd > 50 // 50 meters threshold to avoid overlap
        }

        Log.d("TotalHalts", "Found ${filteredHalts.size} intermediate halts (excluding start/end)")

        for ((index, halt) in filteredHalts.withIndex()) {
            val haltPoint = GeoPoint(halt.latitude, halt.longitude)
            val durationMinutes = halt.duration / (60.0 * 1000.0) // Convert milliseconds to minutes
            addCustomMarker(
                mapView,
                haltPoint,
                "Halt #${index + 1}\n${String.format("%.1f", durationMinutes)} mins",
                MarkerType.HALT,
                haltIndex = index + 1,
                durationMinutes = durationMinutes
            )
        }

        mapView.setBounds(geoPoints as ArrayList<GeoPoint>)
        mapView.invalidate()
    }

    private fun detectHalts(points: List<TimedGeoPoint>): List<HaltInfo> {
        val halts = mutableListOf<HaltInfo>()
        val radius = 200.0 // meters (increased from 100m)
        val minHaltDuration = 10 * 60 * 1000 // 30 minutes

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
                Log.d("HaltDetected", "Halt at ($avgLat, $avgLng), duration: $duration ms")
                halts.add(HaltInfo(avgLat, avgLng, duration))
            }

            startIndex = endIndex
        }

        return halts
    }

    private fun checkIfPointIsHalt(
        targetPoint: TimedGeoPoint,
        allPoints: List<TimedGeoPoint>
    ): Long? {
        val radius = 200.0 // Same radius as halt detection
        val minHaltDuration = 30 * 60 * 1000 // 30 minutes

        // Find all points within radius of the target point
        val nearbyPoints = allPoints.filter { point ->
            isWithinRadius(targetPoint, point, radius)
        }

        if (nearbyPoints.size < 2) return null

        // Calculate total duration spent in this area
        val sortedPoints = nearbyPoints.sortedBy { it.timestamp }
        val duration = sortedPoints.last().timestamp - sortedPoints.first().timestamp

        return if (duration >= minHaltDuration) duration else null
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
        mapView: MapView,
        point: GeoPoint,
        title: String,
        markerType: MarkerType,
        haltIndex: Int = 0,
        durationMinutes: Double = 0.0
    ) {
        val marker = Marker(mapView).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            this.title = title

            // Set custom drawable based on marker type with fixed size
            val drawableRes = when (markerType) {
                MarkerType.START -> R.drawable.ic_start_marker // Replace with your start marker drawable
                MarkerType.END -> R.drawable.ic_end_marker     // Replace with your end marker drawable
                MarkerType.HALT -> R.drawable.ic_halt_marker   // Replace with your halt marker drawable
            }

            try {
                val drawable = ContextCompat.getDrawable(this@AutoSearchPlaceActivity, drawableRes)
                drawable?.let { originalDrawable ->
                    // Set fixed size for markers to prevent scaling with zoom
                    val fixedSize = when (markerType) {
                        MarkerType.START, MarkerType.END -> 48 // dp size for start/end markers
                        MarkerType.HALT -> 36 // dp size for halt markers
                    }

                    // Convert dp to pixels
                    val sizeInPixels = (fixedSize * resources.displayMetrics.density).toInt()

                    // Create a new drawable with fixed bounds
                    val scaledDrawable = originalDrawable.mutate()
                    scaledDrawable.setBounds(0, 0, sizeInPixels, sizeInPixels)

                    this.icon = scaledDrawable
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading custom drawable for $markerType: ${e.message}")
                // Fallback to default marker if custom drawable fails to load
            }

            // Add click listener for halt markers to show tooltip
            if (markerType == MarkerType.HALT) {
                setOnMarkerClickListener { marker, mapView ->
                    showHaltTooltip(haltIndex, durationMinutes)
                    true // Return true to consume the click event
                }
            }
        }

        Log.d(
            "MarkerAdded",
            "Custom Marker ($markerType): $title at (${point.latitude}, ${point.longitude})"
        )
        mapView.overlays.add(marker)
    }

    // Deprecated method - kept for backward compatibility if needed
    @Deprecated("Use addCustomMarker instead")
    private fun addMarker(mapView: MapView, point: GeoPoint, title: String) {
        val marker = Marker(mapView).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            this.title = title
        }
        Log.d("MarkerAdded", "Marker: $title at (${point.latitude}, ${point.longitude})")
        mapView.overlays.add(marker)
    }

    private fun showHaltTooltip(haltNumber: Int, durationMinutes: Double) {
        val message =
            "Halt #$haltNumber\nStayed for ${String.format("%.1f", durationMinutes)} minutes"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        // Alternative: You can also show a custom dialog or popup
        // showCustomHaltDialog(haltNumber, durationMinutes)
    }

    // Optional: Custom dialog method for more detailed halt information
    private fun showCustomHaltDialog(haltNumber: Int, durationMinutes: Double) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Halt Information")
        builder.setMessage(
            "Halt #$haltNumber\n\nDuration: ${
                String.format(
                    "%.1f",
                    durationMinutes
                )
            } minutes"
        )
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun parseTimestamp(dateTime: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.parse(dateTime)?.time ?: 0L
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, day ->
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

    data class TimedGeoPoint(val latitude: Double, val longitude: Double, val timestamp: Long)
    data class HaltInfo(val latitude: Double, val longitude: Double, val duration: Long)
}