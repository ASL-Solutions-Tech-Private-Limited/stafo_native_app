package com.stafo.app.screens.tripPlan

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.stafo.app.R
import com.stafo.app.base.service.LocationForegroundService
import com.stafo.app.databinding.ActivityTripDetailsBinding
import com.stafo.app.databinding.ItemTripStepsBinding
import com.stafo.app.screens.tripPlan.dataClass.TripDetailsResponse
import com.stafo.app.screens.tripPlan.dataClass.TripGeoLocationListRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.checkExactAlarmPermission
import com.stafo.app.utils.formatDate
import com.stafo.app.utils.requestIgnoreBatteryOptimization
import com.stafo.app.utils.setTripId
import com.stafo.app.utils.setTripServiceAction
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class TripDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTripDetailsBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val mCustomLoader: CustomLoader by lazy { CustomLoader(this) }
    private var mTripID: String? = null
    private lateinit var googleMap_: GoogleMap
    private var mTripDetails: TripDetailsResponse.Trip? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val markerMap = mutableMapOf<LatLng, Marker>()

    private lateinit var imageUri: Uri
    private lateinit var photoFile: File
    private lateinit var tripActionBottomSheet: TripActionBottomSheet
    private var haltList: List<HaltInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_trip_details)
        binding = ActivityTripDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mTripID = intent.getStringExtra("tripId") ?: ""
        setTripId(mTripID!!)

        mTripViewModel.getTripDetails(this, mTripID ?: "")
        observeTripDetails()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun observeTripDetails() {
        mTripViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") mCustomLoader.show()
            else mCustomLoader.dismiss()
        }
        mTripViewModel.mTripDetailsResponse.observe(this) {
            if (it.status) {
                binding?.apply {
                    mTripDetails = it.trip
                    tripIdDate.text = "${it.trip.title} | ${formatDate(it.trip.start_time)}"
                    tripStatus.text = it.trip.status
                    tvDriverName.text = "Driver Name:\n${it.trip?.driver?.name}"
                    tvDriverId.text = "Mobile:\n${it.trip?.driver?.emp_id}"

                    tvVehicleNumber.text = "Vehicle Number:\n${it.trip?.vehicle?.vehicle_no}"
                    tvVehicleType.text = "Vehicle Type:\n${it.trip?.vehicle?.vehicle_type}"

                    tvCustomerName.text = "Client Name:\n${it.trip?.customer_info?.customer_name}"
                    tvCustomerPhone.text = "Client Mobile:\n${it.trip?.customer_info?.phone}"

                    tvDuration.text = "00h 00m"
                    tvDistance.text = "${it.trip.distance}kms"
                    notesText.text = it.trip?.notes


                    // val (steps, lastStatus, totalHaltDurationMillis, haltCount) = processTripLogs(it.trip.trip_logs)
                    val result = processTripLogs(it.trip.trip_logs)
                    addTripSteps(result.steps)
                    val mtripAction = if (result.tripStatusCode == 1) "end" else "start"
                    val mhaltAction = if (result.lastStatus == "pause") "resume" else "pause"

                    when (it.trip.status) {
                        "pending" -> {
                            stopLocationServiceIfRunning()
                        }
                        "ongoing" -> {
                            startLocationServiceIfNotRunning()
                        }
                        "pause" -> {
                            stopLocationServiceIfRunning()
                        }
                        "resume" -> startLocationServiceIfNotRunning()
                        else -> {
                            stopLocationServiceIfRunning()
                        }
                    }



                    updateMapWithTripStatus(result)

                    val tripDuration = formatMillisToReadableTime(result.totalTripDurationMillis)
                    val haltDuration = formatMillisToReadableTime(result.totalHaltDurationMillis)
                    val runDuration = formatMillisToReadableTime(result.totalRunningDurationMillis)

                    binding.tvDuration.text = "Trip Duration: $tripDuration"

                    when (result.tripStatusCode) {
                        1 -> {
                            binding.pauseTripBtn.visibility = View.VISIBLE
                            binding.startTripBtn.text = "End Trip"
                            binding.btnAddExpenses.visibility = View.VISIBLE
                        }

                        4 -> {
                            binding.pauseTripBtn.visibility = View.GONE
                            binding.startTripBtn.visibility = View.GONE
                            binding.startTripBtn.text = "Trip Completed"
                            binding.btnAddExpenses.visibility = View.VISIBLE
                            binding.btnAddExpenses.text = "Expenses"
                        }

                        else -> {
                            binding.pauseTripBtn.visibility = View.GONE
                            binding.btnAddExpenses.visibility = View.GONE
                            binding.startTripBtn.text = "Start Trip"
                        }
                    }

                    binding.startTripBtn.setOnClickListener {
                    /*    if (mtripAction == "end") {
                            //Stop Location Service
                            stopLocationServiceIfRunning()

                        } else startLocationServiceIfNotRunning()*/
                        showTripActionBottomSheet(mTripID ?: "", mTripViewModel, mtripAction)
                    }

                    binding.pauseTripBtn.setOnClickListener {
                      /*  if (mhaltAction == "resume") {
                            startLocationServiceIfNotRunning()
                        } else stopLocationServiceIfRunning()*/

                        showTripActionBottomSheet(mTripID ?: "", mTripViewModel, mhaltAction)
                    }



                    if (result.lastStatus == "pause") {
                        binding.pauseTripBtn.text = "Resume"

                    } else {
                        binding.pauseTripBtn.text = "Pause"
                    }




                    binding.btnAddExpenses.setOnClickListener {
                        startActivity(
                            Intent(
                                this@TripDetailsActivity,
                                TripExpensesActivity::class.java
                            ).putExtra("tripId", mTripID)
                                .putExtra("isEnd", result.tripStatusCode)
                        )
                    }
                }
            }
        }

        mTripViewModel.mTripActionResponse.observe(this) {
            if (it.status == true) {
                mTripViewModel.getTripDetails(this, mTripID ?: "")
            } else CustomToast(this, it.message ?: "")
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap_ = map

        /* val startLatLng = LatLng(
             mTripDetails?.start_latitude?.toDouble() ?: 00.00,
             mTripDetails?.start_longitude?.toDouble() ?: 00.00
         )
         val endLatLng = LatLng(
             mTripDetails?.end_latitude?.toDouble() ?: 00.00,
             mTripDetails?.end_longitude?.toDouble() ?: 00.00
         )

         map.addMarker(MarkerOptions().position(startLatLng).title("Start"))
         map.addMarker(MarkerOptions().position(endLatLng).title("End"))
         map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 5f))

         val polylineOptions = PolylineOptions().add(startLatLng, endLatLng)
             .color(ContextCompat.getColor(this, R.color.pending_colour)).width(8f)
         map.addPolyline(polylineOptions)*/
    }

    data class StepData(
        val location: String, val timeRange: String, val flag: String, val latitude: Double,
        val longitude: Double
    )

    private fun addTripSteps(steps: List<StepData>) {
        binding.stepContainer.removeAllViews()

        val inflater = LayoutInflater.from(this)

        steps.forEachIndexed { index, step ->
            val stepBinding = ItemTripStepsBinding.inflate(inflater, binding.stepContainer, false)
            stepBinding.stepLocation.text = step.location
            stepBinding.stepTime.text = step.timeRange

            if (index == steps.size - 1) {
                stepBinding.root.findViewById<View>(
                    stepBinding.root.context.resources.getIdentifier(
                        "verticalLine", "id", packageName
                    )
                )?.visibility = View.GONE
            }
            binding.stepContainer.addView(stepBinding.root)
            stepBinding.stepItem.setOnClickListener {

                val stepLatLng = LatLng(step.latitude, step.longitude)
                googleMap_.animateCamera(CameraUpdateFactory.newLatLngZoom(stepLatLng, 16f))
              //  markerMap[stepLatLng]?.showInfoWindow()
            }
        }
    }


    private fun showTripActionBottomSheet(
        tripId: String, tripViewModel: TripViewModel, tripStatus: String
    ) {
        tripActionBottomSheet = TripActionBottomSheet(context = this,
            tripType = tripStatus,
            tripID = tripId,
            viewModel = tripViewModel,
            onAssignSuccess = {

            },
            onCameraRequest = { openPicker(1101) })
        tripActionBottomSheet.show()
    }


    private fun openPicker(req: Int) {
        ImagePicker.with(this).crop().cameraOnly().compress(1024).maxResultSize(
            1080, 1080
        ).start(req)
    }


    private fun getFileFromUri(uri: Uri): File? {
        val fileName = getFileName(uri) ?: return null
        val file = File(cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null

        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex)
                    }
                }
            }
        }

        if (name.isNullOrEmpty()) {
            name = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) {
                    path.substring(cut + 1)
                } else {
                    path
                }
            }
        }

        return name ?: "unknown_file"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri: Uri = data.data!!

            val file = getFileFromUri(uri)

            if (file != null) {
                when (requestCode) {
                    1101 -> {
                        tripActionBottomSheet.setCapturedImagePath(file.path)
                    }
                }
            } else {
                CustomToast(this, "File selection failed")

            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            CustomToast(this, ImagePicker.getError(data))

        } else {
            // CustomToast(this, "Task Cancelled")

        }
    }

    fun processTripLogs(
        tripLogs: List<TripDetailsResponse.Trip.TripLog>
    ): TripProcessingResult {
        val steps = mutableListOf<StepData>()
        var lastStatus = ""
        var haltStartTime: String? = null
        var haltLocation: String? = null

        var startLocation = ""
        var startTime = ""
        var endLocation = ""
        var endTime = ""

        var startMillis: Long? = null
        var endMillis: Long? = null
        var totalHaltDurationMillis = 0L
        var haltCount = 0

        var hasStart = false
        var hasEnd = false

        val halts = mutableListOf<HaltInfo>()

        val sortedLogs = tripLogs.sortedBy { it.timestamp }

        for (log in sortedLogs) {
            when (log.action_type) {
                "start" -> {
                    hasStart = true
                    startLocation = getAddressFromLatLng(this, log.latitude.toDouble(), log.longitude.toDouble()) ?: "${log.latitude},${log.longitude}"
                    startTime = log.timestamp
                    startMillis = parseTimestampFlexible(log.timestamp)
                    lastStatus = "start"
                }
                "pause" -> {
                    // Begin a halt
                    haltStartTime = log.timestamp
                    haltLocation = getAddressFromLatLng(this, log.latitude.toDouble(), log.longitude.toDouble()) ?: "${log.latitude},${log.longitude}"
                    lastStatus = "pause"
                }
                "resume" -> {
                    // End a halt
                    if (haltStartTime != null && haltLocation != null) {
                        val haltStart = parseTimestampFlexible(haltStartTime)
                        val haltEnd = parseTimestampFlexible(log.timestamp)
                        val haltDuration = haltEnd - haltStart
                        totalHaltDurationMillis += haltDuration
                        haltCount++
                        lastStatus = "resume"
                        val timeRange = "${formatTime(haltStartTime)} - ${formatTime(log.timestamp)}"
                        steps.add(
                            StepData(
                                location = haltLocation,
                                timeRange = "Halt ${haltCount}: ${timeRange}",
                                flag = "Halt ${haltCount}",
                                latitude = log.latitude.toDouble(),
                                longitude = log.longitude.toDouble()
                            )
                        )

                        halts.add(
                            HaltInfo(
                                lat = log.latitude.toDouble(),
                                lng = log.longitude.toDouble(),
                                haltNumber = haltCount,
                                durationMillis = haltDuration
                            )
                        )

                        // Reset halt info
                        haltStartTime = null
                        haltLocation = null
                    }
                }
                "end" -> {
                    // If halt is ongoing, finalize it
                    if (haltStartTime != null && haltLocation != null) {
                        val haltStart = parseTimestampFlexible(haltStartTime)
                        val haltEnd = parseTimestampFlexible(log.timestamp)
                        val haltDuration = haltEnd - haltStart
                        totalHaltDurationMillis += haltDuration
                        haltCount++
                        lastStatus = "end"
                        val timeRange = "${formatTime(haltStartTime)} - ${formatTime(log.timestamp)}"
                        steps.add(
                            StepData(
                                location = haltLocation,
                                timeRange = "Halt ${haltCount}: ${timeRange}",
                                flag = "Halt ${haltCount}",
                                latitude = log.latitude.toDouble(),
                                longitude = log.longitude.toDouble()
                            )
                        )

                        halts.add(
                            HaltInfo(
                                lat = log.latitude.toDouble(),
                                lng = log.longitude.toDouble(),
                                haltNumber = haltCount,
                                durationMillis = haltDuration
                            )
                        )
                        // Reset halt info
                        haltStartTime = null
                        haltLocation = null
                    }
                    // Set trip end info
                    hasEnd = true
                    endLocation = getAddressFromLatLng(this, log.latitude.toDouble(), log.longitude.toDouble()) ?: "${log.latitude},${log.longitude}"
                    endTime = log.timestamp
                    endMillis = parseTimestampFlexible(log.timestamp)
                }
            }
        }

        // Add starting point to steps
        if (startLocation.isNotEmpty() && startTime.isNotEmpty()) {
            steps.add(0, StepData(
                location = startLocation,
                timeRange = "Started at ${formatTime(startTime)}",
                flag = "Start",
                latitude = tripLogs[0].latitude.toDouble(),
                longitude = tripLogs[0].longitude.toDouble()
            ))
        }
        // Add end point
        if (endLocation.isNotEmpty() && endTime.isNotEmpty()) {
            steps.add(
                StepData(
                    location = endLocation,
                    timeRange = "Ended at ${formatTime(endTime)}",
                    flag = "End", latitude = tripLogs[tripLogs.size - 1].latitude.toDouble(),
                    longitude = tripLogs[tripLogs.size - 1].longitude.toDouble()
                )
            )
        }

        // Determine trip status
        val tripStatusCode = when {
            hasStart && hasEnd -> 4
            hasStart -> 1
            hasEnd -> 3
            else -> 0
        }

        // Calculate total trip duration
        val totalTripDurationMillis = if (startMillis != null && endMillis != null) {
            endMillis - startMillis
        } else {
            0L
        }

        val runningDurationMillis = totalTripDurationMillis - totalHaltDurationMillis

        return TripProcessingResult(
            steps = steps,
            lastStatus = lastStatus,
            tripStatusCode = tripStatusCode,
            haltCount = haltCount,
            totalHaltDurationMillis = totalHaltDurationMillis,
            totalTripDurationMillis = totalTripDurationMillis,
            totalRunningDurationMillis = runningDurationMillis,
            halts = halts
        )
    }


    data class TripProcessingResult(
        val steps: List<StepData>,
        val lastStatus: String,
        val tripStatusCode: Int,
        val haltCount: Int,
        val totalHaltDurationMillis: Long,
        val totalTripDurationMillis: Long,
        val totalRunningDurationMillis: Long,
        val halts: List<HaltInfo> = emptyList()
    )

    fun parseTimestampFlexible(timestamp: String): Long {
        val formats = listOf(
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        )

        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                sdf.timeZone = TimeZone.getDefault()
                return sdf.parse(timestamp)?.time ?: continue
            } catch (_: Exception) {
            }
        }
        throw IllegalArgumentException("Unparseable date: $timestamp")
    }


    fun formatTime(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        return if (date != null) outputFormat.format(date) else timestamp
    }

    fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                return address.getAddressLine(0)  // Full address
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun formatMillisToReadableTime(millis: Long): String {
        val totalMinutes = millis / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "${hours} hr ${minutes} min"
    }


    private fun startLocationServiceIfNotRunning(fromTripDetails: Boolean = true) {
        checkExactAlarmPermission(this) { exactAlarmGranted ->
            if (exactAlarmGranted) {
                requestIgnoreBatteryOptimization(this) { batteryOptGranted ->
                    if (batteryOptGranted) {

                        if (!isServiceRunning(LocationForegroundService::class.java)) {
                            setTripServiceAction(this, true)
                            val intent = Intent(this, LocationForegroundService::class.java)
                            intent.putExtra("FROM_TRIP_DETAILS", fromTripDetails)
                            ContextCompat.startForegroundService(this, intent)
                        }

                    }
                }
            }
        }


    }

    private fun stopLocationServiceIfRunning() {
        setTripServiceAction(this, false)

        if (isServiceRunning(LocationForegroundService::class.java)) {
            val stopIntent = Intent(this, LocationForegroundService::class.java)
            stopIntent.action = "STOP_FOREGROUND_SERVICE"
            stopService(stopIntent)

            /*val stopIntent = Intent(this, LocationForegroundService::class.java)
            stopIntent.action = "STOP_FOREGROUND_SERVICE"
            ContextCompat.startForegroundService(this, stopIntent)*/
        }
    }

    private fun isServiceRunning(serviceClass: Class<out Service>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    private fun updateMapWithTripStatus(result: TripProcessingResult) {
        googleMap_.clear()

        val startLatLng = LatLng(
            mTripDetails?.start_latitude?.toDoubleOrNull() ?: 0.0,
            mTripDetails?.start_longitude?.toDoubleOrNull() ?: 0.0
        )

        if (result.tripStatusCode == 0) {
            // Trip not started, show only start marker
            googleMap_.addMarker(MarkerOptions().position(startLatLng).title("Start Location"))
            googleMap_.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15f))
            return
        }

        if (result.tripStatusCode == 1 || result.tripStatusCode == 4) {
            mTripViewModel.getTripGeoLocation(this, TripGeoLocationListRequest(mTripID ?: ""))
            haltList = result.halts
            mTripViewModel.mTripGeoLocationListResponse.observe(this) { geoResponse ->
                if (geoResponse.status && geoResponse.data.isNotEmpty()) {
                    val latLngList = geoResponse.data.map {
                        LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                    }

                    googleMap_.clear()

                    val polylineOptions = PolylineOptions()
                        .addAll(latLngList)
                        .color(ContextCompat.getColor(this, R.color.app_theme_colour))
                        .width(8f)

                    googleMap_.addPolyline(polylineOptions)

                    // Set start marker at first point
                    googleMap_.addMarker(
                        MarkerOptions()
                            .position(latLngList.first())
                            .title("Start")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )

                    // Set end marker at last point
                    googleMap_.addMarker(
                        MarkerOptions()
                            .position(latLngList.last())
                            .title("End")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )

                    Log.e("TAG", "updateMapWithTripStatus: ${Gson().toJson(result.halts)}", )
                    // Add halts from haltList if available
                    haltList.forEach { halt ->
                        val durationReadable = formatMillisToReadableTime(halt.durationMillis)
                        googleMap_.addMarker(
                            MarkerOptions()
                                .position(LatLng(halt.lat, halt.lng))
                                .title("Halt ${halt.haltNumber}")
                                .snippet("Duration: $durationReadable")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        )
                    }

                    // Optionally: If haltList is empty or incomplete, detect halts from geoResponse.data similarly as your second function

                    // Adjust camera to show entire route with padding
                    val boundsBuilder = LatLngBounds.builder()
                    latLngList.forEach { boundsBuilder.include(it) }
                    haltList.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
                    val bounds = boundsBuilder.build()

                    googleMap_.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                    googleMap_.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoContents(marker: Marker): View? = null
                        override fun getInfoWindow(marker: Marker): View? = null
                    })
                }
            }
        }
    }

    data class HaltInfo(
        val lat: Double,
        val lng: Double,
        val haltNumber: Int,
        val durationMillis: Long
    )

}