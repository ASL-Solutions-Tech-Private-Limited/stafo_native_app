package com.stafo.app.screens.tripPlan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.databinding.BottomSheetTripActionBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class TripActionBottomSheet(
    private val context: Activity,
    private val tripType: String,
    private val tripID: String,
    private val viewModel: TripViewModel,
    private val onAssignSuccess: () -> Unit,
    private val onCameraRequest: () -> Unit
) {

    private val dialog: BottomSheetDialog = BottomSheetDialog(context)
    private lateinit var binding: BottomSheetTripActionBinding
    private var imagePath: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    fun show() {
        binding = BottomSheetTripActionBinding.inflate(LayoutInflater.from(context))
        val view = binding.root

        dialog.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        getLocation()
        dialog.setCancelable(false)
        binding.apply {
            bottomSheetCancel.setOnClickListener {
                dialog.dismiss()
            }

            tieOdometerImage.setOnClickListener {
                onCameraRequest() // Delegate to activity
            }

            btnNext.setOnClickListener {
                if (!tieOdometer.text.isNullOrEmpty()) {
                    if (!imagePath.isNullOrEmpty()) {
                        val file = File(imagePath!!)
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData(
                            "image", file.name, requestFile
                        )

                        val odometer = binding.tieOdometer.text.toString()
                        val map = hashMapOf(
                            "odometer" to odometer.toRequestBody(),
                            "type" to tripType.toRequestBody(),
                            "lat" to latitude.toString().toRequestBody(),
                            "long" to longitude.toString().toRequestBody(),
                            "trip_id" to tripID.toRequestBody()
                        )

                        viewModel.sendTripAction(context, map, body)
                    } else {
                        tieOdometerImage.error = "Please upload image"
                    }
                } else {
                    tieOdometer.error = "Please enter odometer"
                }
            }
        }

        viewModel.mTripActionResponse.observe(context as TripDetailsActivity) {
            if (it.status == true) {
                onAssignSuccess()
                dialog.dismiss()
            } else {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun String.toPlainText(): RequestBody =
        this.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun Double.toRequestBody(): RequestBody =
        this.toString().toRequestBody("text/plain".toMediaTypeOrNull())

    fun setCapturedImagePath(path: String) {
        imagePath = path
        binding.tieOdometerImage.setText(File(path).name)
    }


    private val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private val networkLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location

        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    private fun getLocation() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 0F, gpsLocationListener
                )
            }

            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5000, 0F, networkLocationListener
                )
            }

            val lastKnownGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastKnownNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (lastKnownGps != null && lastKnownNetwork != null) {
                currentLocation = if (lastKnownGps.accuracy <= lastKnownNetwork.accuracy) {
                    lastKnownGps
                } else {
                    lastKnownNetwork
                }
            } else if (lastKnownGps != null) {
                currentLocation = lastKnownGps
            } else if (lastKnownNetwork != null) {
                currentLocation = lastKnownNetwork
            }

            currentLocation?.let {
                latitude = it.latitude
                longitude = it.longitude
            }

        } else {
            Toast.makeText(context, "Please enable location services", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }
}

