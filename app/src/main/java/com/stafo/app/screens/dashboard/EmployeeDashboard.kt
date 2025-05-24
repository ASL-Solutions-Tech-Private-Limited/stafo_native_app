package com.stafo.app.screens.dashboard

import android.Manifest
import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
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
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.gson.Gson
import com.stafo.app.R
import com.stafo.app.base.adapter.ActionsListAdapter
import com.stafo.app.base.adapter.AdapterOnLeave
import com.stafo.app.base.adapter.AdapterWishList
import com.stafo.app.base.adapter.SliderAdapter
import com.stafo.app.base.model.ActionModel
import com.stafo.app.base.model.DashboardWish
import com.stafo.app.base.model.EmployeeInfo
import com.stafo.app.base.model.FullScreenDialog
import com.stafo.app.base.service.LocationForegroundService
import com.stafo.app.databinding.ActivityEmpDashboardBinding
import com.stafo.app.databinding.CustomBottomSheetAttendanceLayoutBinding
import com.stafo.app.screens.crm.CRMLeadDashboard
import com.stafo.app.screens.emp.EmpBranchDetailsActivity
import com.stafo.app.screens.emp.EmpSelfieAttendanceActivity
import com.stafo.app.screens.emp.EmployeeAttendanceRecordActivity
import com.stafo.app.screens.emp.EmployeeLeaveHistoryActivity
import com.stafo.app.screens.emp.EmployeeProfileDetails
import com.stafo.app.screens.emp.EmployeePunchInActivity
import com.stafo.app.screens.emp.QRCodeAttendanceEmpActivity
import com.stafo.app.screens.notification.NotificationActivity
import com.stafo.app.screens.settings.HolidayActivity
import com.stafo.app.screens.settings.LeaveRequestHistoryActivity
import com.stafo.app.screens.settings.PolicyActivity
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.SubMenuActivity
import com.stafo.app.screens.ui.EmplyeeyerProfile
import com.stafo.app.screens.ui.WishListActivity
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.convertTo12HourFormat
import com.stafo.app.utils.doLogout
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getFormattedDate2
import com.stafo.app.utils.getGreetingBasedOnTime
import com.stafo.app.utils.setEmployeeBranchId
import com.stafo.app.utils.setEmployeeComId
import com.stafo.app.utils.setIsLock
import com.stafo.app.utils.setIsLockUser
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
    private lateinit var reviewManager: FakeReviewManager
    private var mEmplyeeInfo: EmployeeInfo? = null

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // reviewManager = ReviewManagerFactory.create(this)
        reviewManager = FakeReviewManager(this)


        /* if (getIsLockUser() ==true){
             if (getIsLock() ==true){
                 val delayMillis = 100L
                 Handler(Looper.getMainLooper()).postDelayed({
                     showLockScreen()
                 }, delayMillis)

             }
         }else{
             showScreenLockDialog()
         }*/








        setupViews()
        onClickListener()
        //setupImageSlider()
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
                            overridePendingTransition(
                                R.anim.slide_from_right,
                                R.anim.slide_to_left
                            )
                        }

                        "Leaves" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    EmployeeLeaveHistoryActivity::class.java
                                )
                            )
                            overridePendingTransition(
                                R.anim.slide_from_right,
                                R.anim.slide_to_left
                            )
                        }

                        "Branches" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    EmpBranchDetailsActivity::class.java
                                )
                            )
                            overridePendingTransition(
                                R.anim.slide_from_right,
                                R.anim.slide_to_left
                            )
                        }

                        "Policy" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    PolicyActivity::class.java
                                )
                            )
                            overridePendingTransition(
                                R.anim.slide_from_right,
                                R.anim.slide_to_left
                            )
                        }

                        "CRM" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    CRMLeadDashboard::class.java
                                )
                            )
                        }

                        "Holidays" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    HolidayActivity::class.java
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
        settingsViewModel.fetchEmployeeDetails(
            this@EmployeeDashboard,
            getEmployeeDetails()?.id.toString()
        )

    }


    private fun onClickListener() {
        binding.apply {

            ivLogout.setOnClickListener {
                showLogoutDialog()
                // showRateDialog()
            }

            ivNotification.setOnClickListener {
                startActivity(Intent(this@EmployeeDashboard,NotificationActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }


            tvActivitiesViewAll.setOnClickListener {
                startActivity(
                    Intent(
                        this@EmployeeDashboard,
                        SubMenuActivity::class.java
                    )
                )

                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }

            settingsViewModel.fetchEmployeeDetails(
                this@EmployeeDashboard,
                getEmployeeDetails()?.id.toString()
            )

            settingsViewModel.getBannerImage(this@EmployeeDashboard)

            binding.tvLeaveViewAll.setOnClickListener {
                startActivity(
                    Intent(
                        this@EmployeeDashboard,
                        LeaveRequestHistoryActivity::class.java
                    )
                )
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






            btnPunchIn.setOnClickListener {

                if (binding.btnPunchIn.text == "Punch Out") {
                    binding.btnPunchIn.isEnabled = true


                    val builder = AlertDialog.Builder(this@EmployeeDashboard)
                    builder.setTitle(R.string.app_name)
                    builder.setMessage("Are you sure? You want to punch out!")

                    builder.setPositiveButton("Yes") { dialog, which ->

                        showCustomBottomSheet()

                        dialog.dismiss()


                    }
                    builder.setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()

                } else {
                    if (isLocationPermissionGranted()) {
                        showCustomBottomSheet()
                    } else {
                        showPermissionDialog()
                    }
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


    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showPermissionDialog() {
        val dialog = FullScreenDialog(this) {
            requestLocationPermission()
        }
        dialog.show()
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    showCustomBottomSheet()
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                CustomToast(this, "Background Location Permission Granted")
                showCustomBottomSheet()
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    showSettingsDialog()
                } else {
                    showSettingsDialog()
                }
            }
        }


    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Background location access is required. Please enable it in settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun observeViewModel() {


        settingsViewModel.mEmployeeDashboardResponse.observe(this) {
            if (it.status) {
                mEmplyeeInfo = it.employeeInfo
                setEmployeeComId(it.employeeInfo.companyId.toString())
                setEmployeeBranchId(it.employeeInfo.branchId.toString())


                it.employeeInfo.shifts.firstOrNull()?.let { shift ->
                    val startTime12Hr = convertTo12HourFormat(shift.startTime)
                    val endTime12Hr = convertTo12HourFormat(shift.endTime)
                    binding.tvOfficeTiming.text =
                        "Your Office timing is $startTime12Hr to $endTime12Hr"

                }?: run {
                    binding.tvOfficeTiming.text = "Your Office timing is 10 AM to 8 PM"
                }

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

                }



                wishList.clear()




                if (!it.employeeInfo.punches.isNullOrEmpty()) {
                    Log.d("res", "true ")

                    val punchesToday = it.employeeInfo.punches.filter { punch ->
                        val punchDate = punch.punchIn?.let {
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it)
                        }?.let {
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                        }
                        punchDate == SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(Date())
                    }

                    if (punchesToday.isNotEmpty()) {
                        val lastPunch = punchesToday.first()
                        val punchInTime = lastPunch.punchIn
                        val punchOutTime = lastPunch.punchOut
                        val geoStatus = it.employeeInfo.geoStatus
                        val shiftEndTime = it.employeeInfo.shifts.firstOrNull()?.endTime

                        if (punchInTime != null && geoStatus == "1") {

                            if (!isServiceRunning(LocationForegroundService::class.java)) {
                                startService(Intent(this, LocationForegroundService::class.java))
                            }






                        }

                        if (punchInTime != null && punchOutTime != null) {

                            if (isServiceRunning(LocationForegroundService::class.java)) {

                                val stopIntent = Intent(this, LocationForegroundService::class.java)
                                stopIntent.action = "STOP_FOREGROUND_SERVICE"
                                startService(stopIntent)

                                /*val serviceIntent = Intent(this, LocationForegroundService::class.java)
                                stopService(serviceIntent)*/
                                Log.d("res", "Stopped service after punch out.")
                            }

                            // If last punch has both In and Out, allow another Punch In
                            binding.btnPunchIn.text = "Punch In"
                            binding.btnPunchIn.isEnabled = true
                            binding.btnPunchIn.setBackgroundResource(R.drawable.button_background)
                            binding.tvOfficeTiming.text = "Punched Out At ${
                                getFormattedDate2(
                                    punchOutTime,
                                    listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss"),
                                    "hh:mm a dd MMM yyyy"
                                )
                            }"

                        } else if (punchInTime != null) {
                            // If last punch only has In, allow Punch Out
                            binding.btnPunchIn.text = "Punch Out"
                            binding.btnPunchIn.isEnabled = true
                            binding.btnPunchIn.setBackgroundResource(R.drawable.button_background)
                            binding.tvOfficeTiming.text = "Punched In At ${
                                getFormattedDate2(
                                    punchInTime,
                                    listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss"),
                                    "hh:mm a dd MMM yyyy"
                                )
                            }"
                        }
                       /* val currentDateTime = Calendar.getInstance()
                        if (shiftEndTime != null) {


                            val shiftEndCalendar = Calendar.getInstance()
                            val shiftEndTimeDate =
                                SimpleDateFormat("HH:mm", Locale.getDefault()).parse(shiftEndTime)

                            shiftEndTimeDate?.let {
                                shiftEndCalendar.set(Calendar.HOUR_OF_DAY, it.hours)
                                shiftEndCalendar.set(Calendar.MINUTE, it.minutes)
                                shiftEndCalendar.set(Calendar.SECOND, 0)
                            }

                            if (shiftEndTime != null && currentDateTime.after(shiftEndCalendar)) {

                                Log.d("res", "end time service")


                                if (isServiceRunning(LocationForegroundService::class.java)) {

                                    val stopIntent = Intent(this, LocationForegroundService::class.java)
                                    stopIntent.action = "STOP_FOREGROUND_SERVICE"
                                    startService(stopIntent)
                                    *//*val serviceIntent = Intent(this, LocationForegroundService::class.java)
                                    stopService(serviceIntent)*//*
                                    Log.d("res", "Stopped service after shift end.")
                                }
                            }
                        }else{
                            if (isServiceRunning(LocationForegroundService::class.java)) {

                                val stopIntent = Intent(this, LocationForegroundService::class.java)
                                stopIntent.action = "STOP_FOREGROUND_SERVICE"
                                startService(stopIntent)
                                Log.d("res", "Stopped service not find shift time.")
                            }

                        }*/

                        val currentDateTime = Calendar.getInstance()

                        if (!shiftEndTime.isNullOrEmpty()) {
                            val shiftEndCalendar = Calendar.getInstance()
                            val shiftEndTimeDate =
                                SimpleDateFormat("HH:mm", Locale.getDefault()).parse(shiftEndTime)

                            shiftEndTimeDate?.let {
                                shiftEndCalendar.set(Calendar.HOUR_OF_DAY, it.hours)
                                shiftEndCalendar.set(Calendar.MINUTE, it.minutes)
                                shiftEndCalendar.set(Calendar.SECOND, 0)
                            }

                            if (currentDateTime.after(shiftEndCalendar)) {
                                Log.d("res", "End time reached, stopping service.")

                                if (isServiceRunning(LocationForegroundService::class.java)) {
                                    val stopIntent = Intent(this, LocationForegroundService::class.java)
                                    stopIntent.action = "STOP_FOREGROUND_SERVICE"
                                    startService(stopIntent)
                                    Log.d("res", "Stopped service after shift end.")
                                }
                            }
                        } else {

                            val currentHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
                            val currentMinute = currentDateTime.get(Calendar.MINUTE)

                            if (currentHour == 20 && currentMinute == 0) {

                                Log.d("res", "Shift time is empty, checking 8:00 PM condition.")

                                if (isServiceRunning(LocationForegroundService::class.java)) {
                                    val stopIntent = Intent(this, LocationForegroundService::class.java)
                                    stopIntent.action = "STOP_FOREGROUND_SERVICE"
                                    startService(stopIntent)
                                    Log.d("res", "Stopped service at 8:00 PM as shift time was empty.")
                                }
                            }
                        }



                    } else {
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
                binding.llNoWishes.visibility = View.GONE
                binding.rvWishes.visibility = View.VISIBLE
                binding.rvWishes.adapter = AdapterWishList(wishList, this@EmployeeDashboard)
            } else {
                binding.llNoWishes.visibility = View.VISIBLE
                binding.rvWishes.visibility = View.GONE
            }

            if (it.employeesOnLeave != null && it.employeesOnLeave.isNotEmpty()) {

                binding.rvLeaves.visibility = View.VISIBLE
                binding.llLeaves.visibility = View.GONE

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
                binding.rvLeaves.visibility = View.GONE
                binding.llLeaves.visibility = View.VISIBLE
            }
        }


        settingsViewModel.mSendGeoLocationResponse.observe(this) {
            if (it.status) {
                settingsViewModel.getEmployeDashboard(this)
            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mFetchEmployeeDetailsResponse.observe(this) {
            if (it.status) {
                if (!it.imageUrl.isNullOrEmpty()) {

                    binding.ivHeaderProfilePic.visibility = View.VISIBLE
                    val imageUrl = it.imageUrl

                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.ivHeaderProfilePic)


                } else {
                    binding.ivHeaderProfilePic.visibility = View.GONE
                }

            } else {
                binding.ivHeaderProfilePic.visibility = View.GONE
                CustomToast(this, it.message)
            }
        }


        settingsViewModel.mBannerResponse.observe(this) {

            if (it.status) {

                if (it.data.banner.isNotEmpty()){
                    binding.imageSlider.setSliderAdapter(
                        SliderAdapter(
                            it.data.path,
                            it.data.banner
                        )
                    )

                    binding.imageSlider.setScrollTimeInSec(5)
                    binding.imageSlider.startAutoCycle()
                }
            }
        }


    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun showRateDialog() {
        /*val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                Log.i("CheckReview","IsSuccess")
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(this@EmployeeDashboard, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                // There was some problem, continue regardless of the result.
                // you can show your own rate dialog alert and redirect user to your app page
                // on play store.
            }
        }*/
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("CheckReview", "IsSuccess")
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(this@EmployeeDashboard, reviewInfo)

                flow.addOnCompleteListener { _ ->
                    Log.i("CheckReview", "Review flow completed")
                }
            } else {
                openPlayStoreForReview(this)
                Log.e("CheckReview", "Review flow request failed", task.exception)

                // Handle error (optional: show a custom review dialog)
                task.exception?.let { exception ->
                    when (exception) {
                        is com.google.android.play.core.review.ReviewException -> {
                            Log.e("CheckReview", "Review API error: ${exception.message}")
                        }

                        else -> {
                            Log.e("CheckReview", "Unknown error: ${exception.message}")
                        }
                    }
                }

                // Alternative action: Show custom rating dialog or redirect to Play Store
            }
        }
    }


    private fun showScreenLockDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage("Are you using a screen lock for better security?")
            .setPositiveButton("Yes") { dialog, _ ->
                setIsLockUser(true)
                setIsLock(true)
                showLockScreen()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                setIsLockUser(true)
                setIsLock(false)
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun showLockScreen() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (keyguardManager.isDeviceSecure) {
            val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                "Unlock Your Phone",
                "Please confirm your identity"
            )
            if (intent != null) {
                lockScreenLauncher.launch(intent)
            }
        } else {
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
            startActivity(intent)
        }
    }

    private val lockScreenLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

            } else {
                finish()
            }
        }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this@EmployeeDashboard)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? You want to logout from device!")

        builder.setPositiveButton("Yes") { dialog, _ ->
            doLogout(this)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
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
            if (mEmplyeeInfo != null && mEmplyeeInfo?.attendance_type == "geo") {
                if (isLocationEnabled()) {
                    bottomSheetDialog.dismiss()
                    startActivity(Intent(this, EmployeePunchInActivity::class.java))
                } else {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else CustomToast(this, "Geo Attendance is not enabled for you")
        }

        bottomSheetDialogBinding.llSelfieAttendance.setOnClickListener {
            if (mEmplyeeInfo != null && mEmplyeeInfo?.attendance_type == "selfie" || mEmplyeeInfo?.attendance_type == null) {
                if (isLocationEnabled()) {
                    bottomSheetDialog.dismiss()
                    startActivity(Intent(this, EmpSelfieAttendanceActivity::class.java))
                } else {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else CustomToast(this, "Selfie Attendance is not enabled for you")


            /*   startActivity(Intent(this, EmpSelfieAttendanceActivity::class.java))
               bottomSheetDialog.dismiss()*/
        }

        bottomSheetDialogBinding.llQrAttendance.setOnClickListener {

            if (mEmplyeeInfo != null && mEmplyeeInfo?.attendance_type == "qr code") {
                if (isLocationEnabled()) {
                    bottomSheetDialog.dismiss()
                    startActivity(Intent(this, QRCodeAttendanceEmpActivity::class.java))
                } else {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else CustomToast(this, "QR Attendance is not enabled for you")
            /* startActivity(Intent(this, QRCodeAttendanceEmpActivity::class.java))
             bottomSheetDialog.dismiss()*/

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
        mActionList.add(ActionModel("Attendance", R.drawable.ic_employee))
        mActionList.add(ActionModel("CRM", R.drawable.ic_crm))
        mActionList.add(ActionModel("Leaves", R.drawable.ic_leaves))
        mActionList.add(ActionModel("Branches", R.drawable.ic_branches))
        mActionList.add(ActionModel("Holidays", R.drawable.ic_holidays))
        mActionList.add(ActionModel("Policy", R.drawable.ic_policy))
        return mActionList
    }

    /*  private fun setupImageSlider() {
          val imageList = ArrayList<Int>()
          imageList.add(R.drawable.banner_one)
          imageList.add(R.drawable.banner_two)
          binding.imageSlider.setSliderAdapter(SliderAdapter(this, imageList))
      }*/

    private fun openPlayStoreForReview(context: Context) {
        val appPackageName = context.packageName
        val uri = Uri.parse("market://details?id=$appPackageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}