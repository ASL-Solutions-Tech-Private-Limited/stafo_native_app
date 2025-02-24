package com.asl_emp_mng.app.screens.dashboard

import android.Manifest
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.ActionsListAdapter
import com.asl_emp_mng.app.base.adapter.AdapterOnLeave
import com.asl_emp_mng.app.base.adapter.AdapterWishList
import com.asl_emp_mng.app.base.adapter.SliderAdapter
import com.asl_emp_mng.app.base.model.ActionModel
import com.asl_emp_mng.app.base.model.DashboardWish
import com.asl_emp_mng.app.base.service.LocationForegroundService
import com.asl_emp_mng.app.databinding.ActivityEmpDashboardBinding
import com.asl_emp_mng.app.databinding.CustomBottomSheetAttendanceLayoutBinding
import com.asl_emp_mng.app.screens.emp.EmpBranchDetailsActivity
import com.asl_emp_mng.app.screens.emp.EmpLeaveActivity
import com.asl_emp_mng.app.screens.emp.EmployeeAttendanceRecordActivity
import com.asl_emp_mng.app.screens.emp.EmployeeLeaveHistoryActivity
import com.asl_emp_mng.app.screens.emp.EmployeeProfileDetails
import com.asl_emp_mng.app.screens.emp.EmployeePunchInActivity
import com.asl_emp_mng.app.screens.settings.BranchActivity
import com.asl_emp_mng.app.screens.settings.LeaveRequestHistoryActivity
import com.asl_emp_mng.app.screens.settings.PolicyActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.ui.EmplyeeyerProfile
import com.asl_emp_mng.app.screens.ui.WishListActivity
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getFormattedDate
import com.asl_emp_mng.app.utils.getFormattedDate2
import com.asl_emp_mng.app.utils.getGreetingBasedOnTime
import com.asl_emp_mng.app.utils.getIsCOMPANYLogin
import com.asl_emp_mng.app.utils.setEmployeeBranchId
import com.asl_emp_mng.app.utils.setEmployeeComId
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EmployeeDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmpDashboardBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val mActionList = ArrayList<ActionModel>()
    var wishList = ArrayList<DashboardWish>()

    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetDialogBinding: CustomBottomSheetAttendanceLayoutBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private val PLACE_SEARCH_REQUEST_CODE = 101

    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        setupViews()
        onClickListener()
        setupImageSlider()
    }

    private fun setupViews() {
        binding?.apply {
            tvHeaderGreeting.text = getGreetingBasedOnTime()
            tvHeaderEmpName.text = getEmployeeDetails()?.name ?: " Guest"
            tvHeaderEmpNo.text = getEmployeeDetails()?.emp_id ?: "--"
            rvLeaves.layoutManager =
                LinearLayoutManager(this@EmployeeDashboard, LinearLayoutManager.HORIZONTAL, false)
            rvWishes.layoutManager = LinearLayoutManager(
                this@EmployeeDashboard,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        }
        setupActive()
        observeViewModel()
    }


    private fun setupActive() {

        binding.rvActivities.layoutManager =
            LinearLayoutManager(this@EmployeeDashboard, LinearLayoutManager.HORIZONTAL, false)
        val actionsAdapter = ActionsListAdapter(actionList(),
            this@EmployeeDashboard,
            object : ActionsListAdapter.ActionClickListener {
                override fun onActionClick(action: String) {
                    when (action) {
                        "Attendance" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    EmployeeAttendanceRecordActivity::class.java
                                ).apply {
                                    putExtra("EMP_ID", "${getEmployeeDetails()?.id.toString()}")
                                }
                            )
                        }

                        "Leaves" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    EmployeeLeaveHistoryActivity::class.java
                                )
                            )
                        }

                        "Branches" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    EmpBranchDetailsActivity::class.java
                                )
                            )
                        }

                        "Policy" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    PolicyActivity::class.java
                                )
                            )
                        }
                    }
                }

            })
        binding.rvActivities.adapter = actionsAdapter
    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.getEmployeDashboard(this)
        startLocationService()
    }


    private fun onClickListener() {
        binding?.apply {


            binding.tvLeaveViewAll.setOnClickListener {
                startActivity(Intent(this@EmployeeDashboard, LeaveRequestHistoryActivity::class.java))
            }

            tvWishViewAll.setOnClickListener {
                startActivity(Intent(this@EmployeeDashboard, WishListActivity::class.java).apply {
                    putExtra("WishList", Gson().toJson(wishList))
                })
            }



            /* tvStopService.setOnClickListener {
                stopLocationService()
            }

              tvStartService.setOnClickListener {
                  if (hasLocationPermission()) {
                      startLocationService()
                  } else {
                      requestLocationPermission()
                  }
              }*/

            Log.d("res", "${getIsCOMPANYLogin()}")




            btnPunchIn.setOnClickListener {

                if (binding.btnPunchIn.text=="Punch Out"){
                    binding.btnPunchIn.isEnabled = true


                    val builder = AlertDialog.Builder(this@EmployeeDashboard)
                    builder.setTitle(R.string.app_name)
                    builder.setMessage("Are you sure? Yuo want to punch out!")

                    builder.setPositiveButton("Yes") { dialog, which ->

                        showCustomBottomSheet()

                        dialog.dismiss()


                    }
                    builder.setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()

                }else{
                    showCustomBottomSheet()
                }



            }

            tvHeaderViewProfile.setOnClickListener {
                val intent = Intent(this@EmployeeDashboard, EmployeeProfileDetails::class.java)
                intent.putExtra("EMP_ID", getEmployeeDetails()?.id.toString())
                startActivity(intent)
            }
            tvHeaderSetting.setOnClickListener {
                startActivity(
                    Intent(
                        this@EmployeeDashboard,
                        EmplyeeyerProfile::class.java
                    )
                )
            }


        }
    }


    private fun observeViewModel() {


        settingsViewModel.mEmployeeDashboardResponse.observe(this) {
            if (it.status) {







                setEmployeeComId(it.employeeInfo.companyId.toString())

                setEmployeeBranchId(it.employeeInfo.branchId.toString())




                if (it.employeeInfo.geoStatus != null && it.employeeInfo.geoStatus == "0") {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.app_name)
                    builder.setMessage("Your admin has requested to track your live location. Do you accept?")
                    builder.setPositiveButton("Accept") { dialog, which ->

                        settingsViewModel.sendGeoLocationRequest(
                            this@EmployeeDashboard,
                            getEmployeeDetails()?.id.toString(), "1"
                        )

                        dialog.dismiss()


                    }
                    builder.setNegativeButton("Reject") { dialog, which ->

                        settingsViewModel.sendGeoLocationRequest(
                            this@EmployeeDashboard,
                            getEmployeeDetails()?.id.toString(), "2"
                        )
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()

                }else if(it.employeeInfo.geoStatus == "1") {

                    if (isLocationEnabled()) {
                        startLocationService()
                    } else {
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }

                }









                /* if (it.employeeInfo.geoStatus != null && it.employeeInfo.geoStatus == "1"){

                val punches = it.employeeInfo.punches

                if (punches != null && punches[0].punchOut != null) {

                    stopLocationService()


                }else{
                    if (isServiceRunning(LocationForegroundService::class.java)) {
                        stopLocationService()
                        if (isLocationEnabled()) {
                            startLocationService()
                        } else {
                            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    } else {
                        if (isLocationEnabled()) {
                            startLocationService()
                        } else {
                            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }


            }*/






                wishList.clear()
             /*   if (it.employeeInfo.punches != null && it.employeeInfo.punches.isNotEmpty()) {


                    if (it.employeeInfo.punches.get(0).punchIn != null) {
                        binding.btnPunchIn.setText("Punch Out")
                        binding.btnPunchIn.isEnabled = true




                        Log.d("res",""+binding.btnPunchIn.text)


                        binding.tvOfficeTiming.text = "Punched In At ${
                            getFormattedDate2(
                                it.employeeInfo.punches.get(0).punchIn ?: "",
                                listOf(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", 
                                    "yyyy-MM-dd HH:mm:ss"         
                                ),
                                "hh:mm a dd-MMM-yyyy"
                            )
                        }"

                    } else if (it.employeeInfo.punches.get(0).punchIn != null &&
                        it.employeeInfo.punches.get(0).punchOut != null
                    ) {
                        binding.btnPunchIn.setText("Already Punched Out")
                        binding.btnPunchIn.isEnabled = false
                        binding.tvOfficeTiming.text ="Punched Out At${
                            getFormattedDate2(
                                it.employeeInfo.punches.get(0).punchOut ?: "",
                                listOf(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                    "yyyy-MM-dd HH:mm:ss"
                                ),
                                "hh:mm a dd-MMM-yyyy"
                            )
                        }"



                    } else {
                        binding.btnPunchIn.setText("Punch In")
                        binding.btnPunchIn.isEnabled = true
                    }
                }*/

                if (!it.employeeInfo.punches.isNullOrEmpty()) {
                    val punchData = it.employeeInfo.punches[0]
                    val punchInTime = punchData.punchIn
                    val punchOutTime = punchData.punchOut

                    val shiftEndTime = it.employeeInfo.shift?.endTime

                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                        Date()
                    )

                    val punchInDate = punchInTime?.let {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it)
                    }?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                    }

                    if (punchInDate == currentDate){

                        if (punchInTime !=null && punchOutTime != null){

                            if (isServiceRunning(LocationForegroundService::class.java)) {
                                stopLocationService()
                            }

                            binding.btnPunchIn.text = "Punched Out"
                            binding.btnPunchIn.isEnabled = false

                            binding.btnPunchIn.setBackgroundResource(R.drawable.disable_btn_punch)


                            binding.tvOfficeTiming.text = "Punched Out At ${
                                getFormattedDate2(
                                    punchOutTime,
                                    listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss"),
                                    "hh:mm a dd-MMM-yyyy"
                                )
                            }"


                        }

                        else if (punchInTime !=null){
                            binding.btnPunchIn.text = "Punch Out"
                            binding.btnPunchIn.isEnabled = true
                            binding.btnPunchIn.setBackgroundResource(R.drawable.button_background)
                            binding.tvOfficeTiming.text = "Punched In At ${
                                getFormattedDate2(
                                    punchInTime,
                                    listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss"),
                                    "hh:mm a dd-MMM-yyyy"
                                )
                            }"
                        }


                        else{
                            binding.btnPunchIn.text = "Punch In"
                            binding.btnPunchIn.isEnabled = true
                            binding.btnPunchIn.setBackgroundResource(R.drawable.button_background)
                        }

                        if (shiftEndTime != null) {
                            val currentDateTime = Calendar.getInstance().time
                            val shiftEndDateTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(shiftEndTime)

                            if (shiftEndDateTime != null && currentDateTime.after(shiftEndDateTime) && punchOutTime == null) {
                                if (isServiceRunning(LocationForegroundService::class.java)) {
                                    stopLocationService()
                                }
                            }
                        }


                    }else{
                        binding.btnPunchIn.text = "Punch In"
                        binding.btnPunchIn.isEnabled = true
                        binding.btnPunchIn.setBackgroundResource(R.drawable.button_background)
                    }



                }










            }

            if (it.birthday != null && it.birthday.isNotEmpty()) {
                for (i in it.birthday.indices) {
                    wishList.add(
                        DashboardWish(
                            it.birthday[i].id,
                            it.birthday[i].emp_id,
                            it.birthday[i].date_of_birth ?: "",
                            it.birthday[i].name,
                            it.birthday[i].email,
                            it.birthday[i].phone,
                            it.birthday[i].image,
                            "Birthday",
                            ""
                        )
                    )
                }

            }
            if (it.annyversary != null && it.annyversary.isNotEmpty()) {
                for (i in it.annyversary.indices) {
                    wishList.add(
                        DashboardWish(
                            it.annyversary[i].id,
                            it.annyversary[i].emp_id,
                            "",
                            it.annyversary[i].name,
                            it.annyversary[i].email,
                            it.annyversary[i].phone,
                            it.annyversary[i].image ?: "",
                            "Anniversary",
                            it.annyversary[i].date_of_joining
                        )
                    )
                }
            }

            if (wishList.isNotEmpty()) {
                binding.rvWishes.adapter = AdapterWishList(wishList, this@EmployeeDashboard)
            } else {
                binding.llNoWishes.visibility = View.VISIBLE
                binding.rvWishes.visibility = View.GONE
            }

            if (it.employeesOnLeave != null && it.employeesOnLeave.isNotEmpty()) {
                binding.rvLeaves.layoutManager =
                    LinearLayoutManager(
                        this@EmployeeDashboard,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                val rvAdapter = AdapterOnLeave(it.employeesOnLeave, this)
                binding.rvLeaves.adapter = rvAdapter
               // binding.tvLeaveViewAll.text = it.employeesOnLeave.size.toString()
            } else {
                binding.llLeaves.visibility = View.VISIBLE
            }
        }


        settingsViewModel.mSendGeoLocationResponse.observe(this) {
            if (it.status) {


                if (isServiceRunning(LocationForegroundService::class.java)) {
                    stopLocationService()
                    if (isLocationEnabled()) {
                        startLocationService()
                    } else {
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }


                } else {
                    if (isLocationEnabled()) {
                        startLocationService()
                    } else {
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }

                CustomToast(this, it.message)
            } else {
                CustomToast(this, it.message)
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
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

    private fun stopLocationService() {
        val intent = Intent(this, LocationForegroundService::class.java)
        stopService(intent)
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun showCustomBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialogBinding = CustomBottomSheetAttendanceLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)
        bottomSheetDialogBinding.bottomSheetCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialogBinding.llGeoAttendance.setOnClickListener {
            if (isLocationEnabled()) {
                bottomSheetDialog.dismiss()
                startActivity(Intent(this, EmployeePunchInActivity::class.java))
            } else {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        bottomSheetDialogBinding.llSelfieAttendance.setOnClickListener {
            // checkLocationPermissionAndFind()
        }

        bottomSheetDialogBinding.llQrAttendance.setOnClickListener {

            Toast.makeText(this@EmployeeDashboard, "work in progress", Toast.LENGTH_SHORT).show()
        }

        bottomSheetDialog.setContentView(bottomSheetDialogBinding.root)
        bottomSheetDialog.show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private val locationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isLocationEnabled()) {
                checkLocationPermissionAndFind()
            }
        }

    private fun showLocationServicesDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Location Services")
            .setMessage("This app requires location services to be enabled. Please turn on location services.")
            .setPositiveButton("OK") { _, _ ->
                locationSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkLocationPermissionAndFind() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }

            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
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

                val intent = Intent(this@EmployeeDashboard, EmployeePunchInActivity::class.java)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                startActivityForResult(intent, PLACE_SEARCH_REQUEST_CODE)
            }

        } else {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
            latitude = location.latitude
            longitude = location.longitude
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private val networkLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
            latitude = location.latitude
            longitude = location.longitude

        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }



    private fun actionList(): List<ActionModel> {
        mActionList.add(ActionModel("Attendance", R.drawable.ic_user))
        mActionList.add(ActionModel("Leaves", R.drawable.ic_leaves))
        mActionList.add(ActionModel("Branches", R.drawable.ic_calendar_month))
        mActionList.add(ActionModel("Policy", R.drawable.ic_policy))
        return mActionList
    }

    private fun setupImageSlider() {
        var imageList = ArrayList<Int>()
        imageList.add(R.drawable.banner_one)
        imageList.add(R.drawable.banner_two)
        binding.imageSlider.setSliderAdapter(SliderAdapter(this, imageList))
    }
}