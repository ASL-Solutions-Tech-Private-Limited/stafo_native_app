package com.stafo.app.screens.dashboard

import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.ActionsListAdapter
import com.stafo.app.base.adapter.AdapterOnLeave
import com.stafo.app.base.adapter.AdapterWishList
import com.stafo.app.base.adapter.SliderAdapter
import com.stafo.app.base.model.ActionModel
import com.stafo.app.base.model.DashboardType
import com.stafo.app.base.model.DashboardWish
import com.stafo.app.base.service.LocationForegroundService
import com.stafo.app.databinding.ActivityEmployerDashboardBinding
import com.stafo.app.screens.emp.EmplyeeAttendaceListActivity
import com.stafo.app.screens.emp.ViewEmpLocationTrackActivity
import com.stafo.app.screens.performance.PerformanceActivity
import com.stafo.app.screens.profile.CompanyProfileActivity
import com.stafo.app.screens.rank.RankListActivity
import com.stafo.app.screens.settings.AddEmployeeActivity
import com.stafo.app.screens.settings.BranchActivity
import com.stafo.app.screens.settings.LeaveManagementActivity
import com.stafo.app.screens.settings.LeaveRequestHistoryActivity
import com.stafo.app.screens.settings.PolicyActivity
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.VerifyCompanyDetailsActivity
import com.stafo.app.screens.settings.ViewAllEmployeeActivity
import com.stafo.app.screens.settings.ViewDeviceRequestEmpActivity
import com.stafo.app.screens.subscription.SubscriptionActivity
import com.stafo.app.screens.ui.EmplyeeyerProfile
import com.stafo.app.screens.ui.WishListActivity
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.doLogout
import com.stafo.app.utils.getCompanyDetails
import com.stafo.app.utils.getGreetingBasedOnTime
import com.stafo.app.utils.getTodayDate
import com.stafo.app.utils.setEmployeeComId
import com.stafo.app.utils.setIsLock
import com.stafo.app.utils.setIsLockUser
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.stafo.app.screens.chat.ChatWithCompanyActivity
import com.stafo.app.screens.crm.CRMLeadDashboard
import com.stafo.app.screens.notification.NotificationActivity
import com.stafo.app.screens.recharge.RechargeActivity
import com.stafo.app.screens.reports.ReportsActivity
import com.stafo.app.screens.settings.HolidayActivity
import com.stafo.app.screens.settings.SubMenuActivity
import com.tanodxyz.gdownload.isNetworkAvailable

class EmployerDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityEmployerDashboardBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    var wishList = ArrayList<DashboardWish>()

    private val mActionList = ArrayList<ActionModel>()

    private var companyStatus: Boolean = false
    private var maxEmployeeAdd: String = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)


        initViews()
        setOnClickEvents()
        observeViewModel()

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


    private fun initViews() {
        binding.apply {

            tvHeaderGreeting.text = getGreetingBasedOnTime()
            tvHeaderEmpName.text = getCompanyDetails()?.companyName ?: " Guest"
            tvLetsCheck.text = "Today's Report (${getTodayDate()})"
            rvWishes.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)

            rvActions.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)

            val actionsAdapter = ActionsListAdapter(actionList(),
                this@EmployerDashboard,
                object : ActionsListAdapter.ActionClickListener {
                    override fun onActionClick(action: String) {
                        when (action) {
                            "Employee" -> {

                                if (companyStatus) {
                                    startActivity(
                                        Intent(
                                            this@EmployerDashboard,
                                            ViewAllEmployeeActivity::class.java
                                        ).apply {
                                            putExtra("FROM", "View All")
                                        }
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )
                                } else {
                                    showCompanyVerificationDialog()
                                }

                            }

                            "CRM" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        CRMLeadDashboard::class.java
                                    )
                                )
                            }

                            "Reports" -> {
                                startActivity(Intent(this@EmployerDashboard, ReportsActivity::class.java))
                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                            }
                            "Performance Type"-> {
                                startActivity(Intent(this@EmployerDashboard, PerformanceActivity::class.java))
                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                            }
                            "Rank List"-> {
                                startActivity(Intent(this@EmployerDashboard, RankListActivity::class.java))
                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                            }


                            "Leaves" -> {

                                if (companyStatus) {
                                    startActivity(
                                        Intent(
                                            this@EmployerDashboard,
                                            LeaveManagementActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )
                                } else {
                                    showCompanyVerificationDialog()
                                }


                            }

                            "Branches" -> {
                                if (companyStatus) {
                                    startActivity(
                                        Intent(
                                            this@EmployerDashboard,
                                            BranchActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )
                                } else {
                                    showCompanyVerificationDialog()
                                }


                            }

                            "Policy" -> {


                                if (companyStatus) {
                                    startActivity(
                                        Intent(
                                            this@EmployerDashboard,
                                            PolicyActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )
                                } else {
                                    showCompanyVerificationDialog()
                                }

                            }

                            "Location Track" -> {


                                if (companyStatus) {
                                    startActivity(
                                        Intent(
                                            this@EmployerDashboard,
                                            ViewEmpLocationTrackActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )
                                } else {
                                    showCompanyVerificationDialog()
                                }

                            }

                            "Request Device" -> {


                                if (companyStatus) {
                                    startActivity(
                                        Intent(
                                            this@EmployerDashboard,
                                            ViewDeviceRequestEmpActivity::class.java
                                        )
                                    )

                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )
                                } else {
                                    showCompanyVerificationDialog()
                                }

                            }

                            "Holidays" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        HolidayActivity::class.java
                                    )
                                )
                            }

                        }
                    }

                })
            rvActions.adapter = actionsAdapter


            settingsViewModel.getCompanyDashboard(this@EmployerDashboard)
            binding.tvOnLeaveEmp.text = "0"
        }
    }


    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        settingsViewModel.mAttendanceSummaryResponse.observe(this) {
            if (it.status) {
                setEmployeeComId(it.companyId.toString())

                maxEmployeeAdd = it.companyInfo?.maxEmployeeAdd.toString()



                wishList.clear()
                binding.tvPresentEmp.text = it.presentCount.toString()
                binding.tvAllEmp.text = it.employeeCount.toString()

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
                if (it.anniversary != null && it.anniversary.isNotEmpty()) {
                    for (i in it.anniversary.indices) {
                        wishList.add(
                            DashboardWish(
                                it.anniversary[i].id,
                                it.anniversary[i].emp_id,
                                "",
                                it.anniversary[i].name,
                                it.anniversary[i].email,
                                it.anniversary[i].phone,
                                it.anniversary[i].image,
                                "Anniversary",
                                it.anniversary[i].date_of_joining
                            )
                        )
                    }
                }

                if (wishList.isNotEmpty()) {
                    binding.rvWishes.adapter = AdapterWishList(wishList, this@EmployerDashboard)
                } else {
                    binding.llNoWishes.visibility = View.VISIBLE
                    binding.rvWishes.visibility = View.GONE
                }

                if (it.employeesOnLeave != null && it.employeesOnLeave.isNotEmpty()) {
                    binding.rvLeaves.layoutManager =
                        LinearLayoutManager(
                            this@EmployerDashboard,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                    val rvAdapter = AdapterOnLeave(it.employeesOnLeave, this)
                    binding.rvLeaves.adapter = rvAdapter
                    binding.tvOnLeaveEmp.text = it.employeesOnLeave.size.toString()
                } else {
                    binding.llLeaves.visibility = View.VISIBLE
                }


                it.companyInfo?.let { companyInfo ->

                    if (companyInfo.isVerified == "Yes") {
                        companyStatus = true

                    } else {
                        companyStatus = false
                        binding.llNoWishes.visibility = View.GONE
                        binding.llLeaves.visibility = View.GONE

                        showCompanyVerificationDialog()
                    }
                }


            }

        }






        settingsViewModel.mCompanyProfileResponse.observe(this) {
            if (it.status) {

                it.data?.companyLogo?.let { imageUrl ->
                    binding.ivHeaderProfilePic.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.ivHeaderProfilePic)
                } ?: run {
                    binding.ivHeaderProfilePic.visibility = View.GONE
                }


            } else {
                binding.ivHeaderProfilePic.visibility = View.GONE
                CustomToast(this, it.message)
            }

        }



        settingsViewModel.mUpgradePackageResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
            } else {
                CustomToast(this, it.message)
            }
        }


        settingsViewModel.mBannerResponse.observe(this) {

            if (it.status) {

                if (it.data.banner.isNotEmpty()) {
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


    private fun showUpgradeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("You have reached the maximum limit of employees.Upgrade your plan to continue adding employees.")
        builder.setPositiveButton("Upgrade Now") { _, _ ->


            startActivity(Intent(this,SubscriptionActivity::class.java))



           // settingsViewModel.upgradePackage(this@EmployerDashboard)


        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.gray_colour))
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()

        settingsViewModel.getCompanyDashboard(this@EmployerDashboard)
        settingsViewModel.getCompanyDetails(this@EmployerDashboard)
    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun stopLocationServiceIfRunning() {
        if (isServiceRunning(LocationForegroundService::class.java)) {
            val stopIntent = Intent(this, LocationForegroundService::class.java)
            stopIntent.action = "STOP_FOREGROUND_SERVICE"
            ContextCompat.startForegroundService(this, stopIntent)
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


    private fun setOnClickEvents() {

        binding.ivLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.ivNotification.setOnClickListener {
            startActivity(Intent(this@EmployerDashboard, NotificationActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        binding.tvActivitiesViewAll.setOnClickListener {
            startActivity(
                Intent(
                    this@EmployerDashboard,
                    SubMenuActivity::class.java
                )
            )

            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
        
        stopLocationServiceIfRunning()

        settingsViewModel.getCompanyDetails(this@EmployerDashboard)

        settingsViewModel.getBannerImage(this@EmployerDashboard)



        binding.tvHeaderSetting.setOnClickListener {

            if (companyStatus) {
                val intent = Intent(this@EmployerDashboard, EmplyeeyerProfile::class.java)
                intent.putExtra("DASHBOARD_TYPE", DashboardType.EMPLOYEE.name)
                startActivity(intent)
            } else {
                showCompanyVerificationDialog()
            }

        }

        binding.llcCardAllEmp.setOnClickListener {
            if (companyStatus) {
                startActivity(
                    Intent(
                        this@EmployerDashboard,
                        ViewAllEmployeeActivity::class.java
                    ).apply {
                        putExtra("FROM", "View All")
                    }
                )
            } else {
                showCompanyVerificationDialog()
            }
        }

        binding.llcCardOnLeave.setOnClickListener {
            if (companyStatus) {
                startActivity(Intent(this, LeaveRequestHistoryActivity::class.java))
            } else {
                showCompanyVerificationDialog()
            }
        }

        binding.llcCardPresent.setOnClickListener {
            if (companyStatus) {
                startActivity(Intent(this, EmplyeeAttendaceListActivity::class.java))
            } else {
                showCompanyVerificationDialog()
            }
        }


        binding.addEmp.setOnClickListener {


            // startActivity(Intent(this, RechargeActivity::class.java))

            if (companyStatus) {


                val getTotalEmp = binding.tvAllEmp.text.toString().trim().toIntOrNull()

                if (getTotalEmp != null && getTotalEmp >= (maxEmployeeAdd.toIntOrNull() ?: 0)) {

                    showUpgradeDialog()

                } else {
                    startActivity(Intent(this, AddEmployeeActivity::class.java))
                }


            } else {
                showCompanyVerificationDialog()
            }

        }

        binding.tvLetsCheckViewAll.setOnClickListener {

            if (companyStatus) {
                startActivity(Intent(this, EmplyeeAttendaceListActivity::class.java))
            } else {
                showCompanyVerificationDialog()
            }
        }

        binding.tvProfile.setOnClickListener {

            startActivity(Intent(this, CompanyProfileActivity::class.java))

            /* if (companyStatus) {

                // startActivity(Intent(this, VerifyCompanyDetailsActivity::class.java))
             } else {
                 showCompanyVerificationDialog()
             }*/

        }

        binding.tvLetsLeaveViewAll.setOnClickListener {

            if (companyStatus) {
                startActivity(Intent(this, LeaveRequestHistoryActivity::class.java))
            } else {
                showCompanyVerificationDialog()
            }

        }

        binding.tvLetsWishViewAll.setOnClickListener {


            if (companyStatus) {
                startActivity(Intent(this, WishListActivity::class.java).apply {
                    putExtra("WishList", Gson().toJson(wishList))
                })
            } else {
                showCompanyVerificationDialog()
            }

        }


    }

    private fun actionList(): List<ActionModel> {
        mActionList.add(ActionModel("Employee", R.drawable.ic_employee))
        mActionList.add(ActionModel("CRM", R.drawable.ic_crm))
        mActionList.add(ActionModel("Location Track", R.drawable.ic_location))
        mActionList.add(ActionModel("Reports", R.drawable.ic_reports))
        mActionList.add(ActionModel("Leaves", R.drawable.ic_leaves))
        mActionList.add(ActionModel("Performance Type", R.drawable.ic_performace))
        mActionList.add(ActionModel("Rank List", R.drawable.ic_rank))
        mActionList.add(ActionModel("Branches", R.drawable.ic_branches))
        mActionList.add(ActionModel("Holidays", R.drawable.ic_holidays))
        mActionList.add(ActionModel("Policy", R.drawable.ic_policy))
        mActionList.add(ActionModel("Request Device", R.drawable.ic_device_request))
        return mActionList
    }

    /* private fun setupImageSlider() {
         var imageList = ArrayList<Int>()
         imageList.add(R.drawable.banner_one)
         imageList.add(R.drawable.banner_two)
         binding.imageSlider.setSliderAdapter(SliderAdapter(this, imageList))
     }
 */

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


    private fun showCompanyVerificationDialog() {
        val builder = AlertDialog.Builder(this@EmployerDashboard)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Your company is not verified. Please complete verification.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            startActivity(Intent(this, VerifyCompanyDetailsActivity::class.java))
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this@EmployerDashboard)
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
}