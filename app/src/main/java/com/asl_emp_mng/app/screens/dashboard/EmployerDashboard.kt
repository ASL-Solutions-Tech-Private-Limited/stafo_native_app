package com.asl_emp_mng.app.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.asl_emp_mng.app.base.model.DashboardType
import com.asl_emp_mng.app.base.model.DashboardWish
import com.asl_emp_mng.app.base.service.LocationForegroundService
import com.asl_emp_mng.app.databinding.ActivityEmployerDashboardBinding
import com.asl_emp_mng.app.screens.emp.EmplyeeAttendaceListActivity
import com.asl_emp_mng.app.screens.profile.CompanyProfileActivity
import com.asl_emp_mng.app.screens.settings.AddEmployeeActivity
import com.asl_emp_mng.app.screens.settings.BranchActivity
import com.asl_emp_mng.app.screens.settings.LeaveManagementActivity
import com.asl_emp_mng.app.screens.settings.LeaveRequestHistoryActivity
import com.asl_emp_mng.app.screens.settings.PolicyActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.VerifyCompanyDetailsActivity
import com.asl_emp_mng.app.screens.settings.ViewAllEmployeeActivity
import com.asl_emp_mng.app.screens.ui.EmplyeeyerProfile
import com.asl_emp_mng.app.screens.ui.WishListActivity
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getCompanyDetails
import com.asl_emp_mng.app.utils.getGreetingBasedOnTime
import com.asl_emp_mng.app.utils.getTodayDate
import com.asl_emp_mng.app.utils.setEmployeeComId
import com.bumptech.glide.Glide
import com.google.gson.Gson

class EmployerDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerDashboardBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    var wishList = ArrayList<DashboardWish>()

    private val mActionList = ArrayList<ActionModel>()

    private var companyStatus: Boolean = false
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)

        initViews()
        setOnClickEvents()
        observeViewModel()
        setupImageSlider()
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
                                } else {
                                    showCompanyVerificationDialog()
                                }

                            }

                            "Leaves" -> {

                                if (companyStatus) {
                                    startActivity(
                                        Intent(
                                            this@EmployerDashboard,
                                            LeaveManagementActivity::class.java
                                        )
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
                                } else {
                                    showCompanyVerificationDialog()
                                }

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
                wishList.clear()
                binding.tvPresentEmp.text = it.presentCount.toString()
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
                    Log.d("CompanyVerification", "isVerified: ${companyInfo.isVerified}")
                    if (companyInfo.isVerified == "Yes") {
                        companyStatus = true
                        Log.d("CompanyVerification", "companyStatus set to TRUE")
                    } else {
                        companyStatus = false
                        Log.d("CompanyVerification", "companyStatus set to FALSE")
                        showCompanyVerificationDialog()
                    }
                }


            }

        }



        settingsViewModel.mCompanyProfileResponse.observe(this) {
            if (it.status) {

                it.data?.companyLogo?.let { imageUrl ->
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.demo_avatar)
                        .error(R.drawable.demo_avatar)
                        .into(binding.ivHeaderProfilePic)
                } ?: run {
                    CustomToast(this, "No image available")
                }


            } else {
                CustomToast(this, it.message)
            }

        }

    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.getCompanyDetails(this@EmployerDashboard)
    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun stopLocationService() {
        val intent = Intent(this, LocationForegroundService::class.java)
        stopService(intent)
    }

    private fun setOnClickEvents() {

        stopLocationService()

        settingsViewModel.getCompanyDetails(this@EmployerDashboard)

        binding.tvHeaderSetting.setOnClickListener {

            if (companyStatus) {
                val intent = Intent(this@EmployerDashboard, EmplyeeyerProfile::class.java)
                intent.putExtra("DASHBOARD_TYPE", DashboardType.EMPLOYEE.name)
                startActivity(intent)
            } else {
                showCompanyVerificationDialog()
            }

        }


        binding.addEmp.setOnClickListener {

            if (companyStatus) {
                startActivity(Intent(this, AddEmployeeActivity::class.java))
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

            if (companyStatus) {
                startActivity(Intent(this, CompanyProfileActivity::class.java))
               // startActivity(Intent(this, VerifyCompanyDetailsActivity::class.java))
            } else {
                showCompanyVerificationDialog()
            }

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
        mActionList.add(ActionModel("Employee", R.drawable.ic_user))
        mActionList.add(ActionModel("Leaves", R.drawable.ic_leaves))
        mActionList.add(ActionModel("Branches", R.drawable.ic_calendar_month))
        mActionList.add(ActionModel("Policy", R.drawable.ic_policy))
        mActionList.add(ActionModel("Location Track", R.drawable.ic_location_pin))
        return mActionList
    }

    private fun setupImageSlider() {
        var imageList = ArrayList<Int>()
        imageList.add(R.drawable.banner_one)
        imageList.add(R.drawable.banner_two)
        binding.imageSlider.setSliderAdapter(SliderAdapter(this, imageList))
    }


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
}