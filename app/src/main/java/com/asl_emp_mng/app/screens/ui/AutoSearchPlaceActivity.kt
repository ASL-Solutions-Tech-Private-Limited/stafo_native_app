package com.asl_emp_mng.app.screens.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.LeavesManagementAdapter
import com.asl_emp_mng.app.databinding.ActivityAutoSearchPlaceBinding
import com.asl_emp_mng.app.screens.emp.EmplyeeAttendaceListActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.GeoLocationHistResquest
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveRequestBody
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getIsCOMPANYLogin
import com.mmi.MapmyIndiaMapView
import com.mmi.layers.Marker
import com.mmi.layers.PathOverlay
import com.mmi.layers.UserLocationOverlay
import com.mmi.layers.location.GpsLocationProvider
import com.mmi.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AutoSearchPlaceActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAutoSearchPlaceBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var mEMPID:String
    private val calendar = Calendar.getInstance()
    private var mSelectedDate = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityAutoSearchPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        mEMPID = intent.getStringExtra("EMP_ID") ?: ""
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        mSelectedDate = currentDate


        onClickListener()
        observeViewModel()

    }



    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mGeoLocationHistResponse.observe(this) {

            if (it.status) {

                if (it.data.isNotEmpty()){
                    binding.idMapView.visibility=View.VISIBLE
                    binding.layoutNotView.visibility=View.GONE
                    val geoPoints = it.data.map {
                        GeoPoint(it.latitude.toDouble(), it.longitude.toDouble())
                    }

                    addMarkersAndPath(geoPoints)
                }else{
                    binding.idMapView.visibility=View.GONE
                    binding.layoutNotView.visibility=View.VISIBLE
                }



            } else {
               CustomToast(this,it.message)
            }


        }





    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun onClickListener() {
        binding?.apply {

            val showCurrentDate = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(calendar.time)
            binding.txtDate.setText(showCurrentDate)

            val request = GeoLocationHistResquest(
               employee_id =mEMPID,
                date = mSelectedDate
            )
            Log.d("res","emp get :$request")

            settingsViewModel.getGeoLocationHist(this@AutoSearchPlaceActivity, request)


            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            llCalendar.setOnClickListener {
                showDatePicker()
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
    private fun addMarkersAndPath(geoPoints: List<GeoPoint>) {
        val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
        val mapView = mapmyIndiaMapView.mapView

        if (geoPoints.isEmpty()) return // Prevent errors if the list is empty

        val geoPointsArrayList = ArrayList(geoPoints)

        // Add Start Marker (First Point)
        val startMarker = Marker(mapView).apply {
            position = geoPointsArrayList.first()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Default anchor
            title = "Start Point"
        }
        mapView.overlays.add(startMarker)

        // Add End Marker (Last Point)
        val endMarker = Marker(mapView).apply {
            position = geoPointsArrayList.last()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Default anchor
            title = "End Point"
        }
        mapView.overlays.add(endMarker)

        // Draw polyline between points
        val pathOverlay = PathOverlay(this).apply {
            color = ContextCompat.getColor(this@AutoSearchPlaceActivity,android.R.color.holo_red_dark)
            width = 10f
            points = geoPointsArrayList
        }

        mapView.overlays.add(pathOverlay)
        mapView.setBounds(geoPointsArrayList) // Adjust camera to show full path
        mapView.invalidate()
    }



    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { _, year, monthOfYear,dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.txtDate.setText(formattedDate)

                mSelectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)

                val request = GeoLocationHistResquest(
                    employee_id =mEMPID,
                    date = mSelectedDate
                )
                Log.d("res","emp get :$request")

                settingsViewModel.getGeoLocationHist(this@AutoSearchPlaceActivity, request)



            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        try {
            val datePicker = datePickerDialog.datePicker
            val daySpinner = datePicker.findViewById<View>(
                resources.getIdentifier("day", "id", "android")
            )
            daySpinner?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        datePickerDialog.show()
    }




}
