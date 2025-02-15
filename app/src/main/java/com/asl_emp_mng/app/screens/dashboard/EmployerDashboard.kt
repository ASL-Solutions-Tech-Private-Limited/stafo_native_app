package com.asl_emp_mng.app.screens.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.ActionsListAdapter
import com.asl_emp_mng.app.base.adapter.AdapterOnLeave
import com.asl_emp_mng.app.base.adapter.EmpListAdapter
import com.asl_emp_mng.app.base.adapter.SliderAdapter
import com.asl_emp_mng.app.base.model.ActionModel
import com.asl_emp_mng.app.base.model.DashboardType
import com.asl_emp_mng.app.databinding.ActivityEmployerDashboardBinding
import com.asl_emp_mng.app.screens.settings.AddEmployeeActivity
import com.asl_emp_mng.app.screens.settings.BranchActivity
import com.asl_emp_mng.app.screens.settings.CompanyProfileActivity
import com.asl_emp_mng.app.screens.settings.EmployeeProfileDetails
import com.asl_emp_mng.app.screens.settings.LeaveManagementActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.ViewAllEmployeeActivity
import com.asl_emp_mng.app.screens.ui.EmployeeAttendance
import com.asl_emp_mng.app.screens.ui.EmployeePunchInActivity
import com.asl_emp_mng.app.screens.ui.EmplyeeyerProfile
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.getCompanyDetails
import com.asl_emp_mng.app.utils.getGreetingBasedOnTime
import com.asl_emp_mng.app.utils.getTodayDate
import com.asl_emp_mng.app.utils.getUserAccessToken

class EmployerDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerDashboardBinding
    private lateinit var name: String

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var rvAdapter: EmpListAdapter
    private val mActionList = ArrayList<ActionModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setOnClickEvents()
        observeViewModel()
        setupImageSlider()
    }

    private fun initViews() {
        binding.apply {
            name = intent.extras?.getString("name") ?: ""
            tvHeaderGreeting.text = getGreetingBasedOnTime()
            tvHeaderEmpName.text = getCompanyDetails()?.companyName ?: " Guest"

            tvLetsCheck.text = "Today's Report (${getTodayDate()})"
            rvWishes.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
            llNoWishes.visibility = View.VISIBLE


            rvActions.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
            val actionsAdapter = ActionsListAdapter(actionList(),
                this@EmployerDashboard,
                object : ActionsListAdapter.ActionClickListener {
                    override fun onActionClick(action: String) {
                        when (action) {
                            "Employee" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        ViewAllEmployeeActivity::class.java
                                    )
                                )
                            }

                            "Leaves" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        LeaveManagementActivity::class.java
                                    )
                                )
                            }

                            "Branchs" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        BranchActivity::class.java
                                    )
                                )
                            }

                            "Policy" -> {}
                        }
                    }

                })
            rvActions.adapter = actionsAdapter

            //settingsViewModel.getEmpList(this@EmployerDashboard,"2025-02-07")

            settingsViewModel.getCompanyDashboard(this@EmployerDashboard)
            settingsViewModel.getOnLeaveList(this@EmployerDashboard)

        }


    }


    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        settingsViewModel.mAttendanceSummaryResponse.observe(this) {
            binding.tvPresentEmp.text = it.presentCount.toString()
            binding.tvOnLeaveEmp.text = it.employeeCount.toString()

        }

        settingsViewModel.mOnLeaveResponse.observe(this) {

            if (it.leave.isNotEmpty()){
                binding.rvLeaves.layoutManager =
                    LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
                val rvAdapter = AdapterOnLeave(it.leave, this)
                binding.rvLeaves.adapter = rvAdapter
            }else{
                binding.llLeaves.visibility = View.VISIBLE
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

    private fun setOnClickEvents() {
        binding.tvHeaderSetting.setOnClickListener {
            val intent = Intent(this@EmployerDashboard, EmplyeeyerProfile::class.java)
            intent.putExtra("DASHBOARD_TYPE", DashboardType.EMPLOYEE.name)
            startActivity(intent)
        }

        binding.addEmp.setOnClickListener {
            startActivity(Intent(this, AddEmployeeActivity::class.java))
        }

        binding.tvLetsCheckViewAll.setOnClickListener {
            startActivity(Intent(this, EmployeeAttendance::class.java))
        }

        binding.tvHeaderEmpName.text = name

        binding.tvProfile.setOnClickListener {
            startActivity(Intent(this, CompanyProfileActivity::class.java))
            //startActivity(Intent(this, EmployeeProfileDetails::class.java))
           //startActivity(Intent(this, EmployeePunchInActivity::class.java))
        }
    }

    private fun actionList(): List<ActionModel> {
        mActionList.add(ActionModel("Employee", R.drawable.ic_user))
        mActionList.add(ActionModel("Leaves", R.drawable.ic_leaves))
        mActionList.add(ActionModel("Branchs", R.drawable.ic_calendar_month))
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