package com.asl_emp_mng.app.screens.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.asl_emp_mng.app.base.model.PunchInType
import com.asl_emp_mng.app.databinding.ActivityEmpDashboardBinding
import com.asl_emp_mng.app.databinding.CustomBottomSheetAttendanceLayoutBinding
import com.asl_emp_mng.app.screens.emp.EmpLeaveActivity
import com.asl_emp_mng.app.screens.emp.EmployeeAttendanceRecordActivity
import com.asl_emp_mng.app.screens.emp.EmployeeProfileDetails
import com.asl_emp_mng.app.screens.emp.EmployeePunchInActivity
import com.asl_emp_mng.app.screens.settings.BranchActivity
import com.asl_emp_mng.app.screens.settings.PolicyActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.ui.EmplyeeyerProfile
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getFormattedDate
import com.asl_emp_mng.app.utils.getGreetingBasedOnTime
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog

class EmployeeDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmpDashboardBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val mActionList = ArrayList<ActionModel>()
    var wishList = ArrayList<DashboardWish>()
    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetDialogBinding: CustomBottomSheetAttendanceLayoutBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
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
            tvHeaderEmpNo.text = getEmployeeDetails()?.empId ?: "--"
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
                                    EmpLeaveActivity::class.java
                                )
                            )
                        }

                        "Branches" -> {
                            startActivity(
                                Intent(
                                    this@EmployeeDashboard,
                                    BranchActivity::class.java
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
    }
    private fun onClickListener() {
        binding?.apply {

            btnPunchIn.setOnClickListener {
                showCustomBottomSheet()
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
                if (it.employeeInfo.geoStatus != null && it.employeeInfo.geoStatus == "0") {

                }
                if (it.employeeInfo.punches != null && it.employeeInfo.punches.isNotEmpty()) {
                    if (it.employeeInfo.punches.get(0).punchIn != null) {
                        binding.btnPunchIn.setText("Punch Out")
                        binding.btnPunchIn.isEnabled = false
                        binding.tvOfficeTiming.text = "Punched In At ${
                            getFormattedDate(
                                it.employeeInfo.punches.get(0).punchIn ?: "",
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                "hh:mm a dd-MMM-yyyy"
                            )
                        }"

                    } else if (it.employeeInfo.punches.get(0).punchIn != null &&
                        it.employeeInfo.punches.get(0).punchOut != null
                    ) {
                        binding.btnPunchIn.setText("Already Punched Out")
                        binding.btnPunchIn.isEnabled = false
                        binding.tvOfficeTiming.text = "Punched Out At ${
                            getFormattedDate(
                                it.employeeInfo.punches.get(0).punchOut ?: "",
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                "hh:mm a dd-MMM-yyyy"
                            )
                        }"
                    } else {
                        binding.btnPunchIn.setText("Punch In")
                        binding.btnPunchIn.isEnabled = true
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
                            it.annyversary[i].image,
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
                binding.tvLeaveViewAll.text = it.employeesOnLeave.size.toString()
            } else {
                binding.llLeaves.visibility = View.VISIBLE
            }
        }

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
            if (!isLocationEnabled()) {
                showLocationServicesDialog()
            } else {
                checkLocationPermissionAndFind()
            }
        }

        bottomSheetDialogBinding.llSelfieAttendance.setOnClickListener {
            val intent = Intent(this@EmployeeDashboard, EmployeePunchInActivity::class.java)
            intent.putExtra("Punch_TYPE", PunchInType.SELFIE.name)
            startActivity(intent)
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
                val intent = Intent(this@EmployeeDashboard, EmployeePunchInActivity::class.java)
                intent.putExtra("Punch_TYPE", PunchInType.GEO.name)
                startActivity(intent)
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
            val intent = Intent(this@EmployeeDashboard, EmployeePunchInActivity::class.java)
            intent.putExtra("Punch_TYPE", PunchInType.GEO.name)
            startActivity(intent)
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
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