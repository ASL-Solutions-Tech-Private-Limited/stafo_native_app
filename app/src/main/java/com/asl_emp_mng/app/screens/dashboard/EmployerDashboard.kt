package com.asl_emp_mng.app.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.ActionsListAdapter
import com.asl_emp_mng.app.base.adapter.AdapterOnLeave
import com.asl_emp_mng.app.base.adapter.AdapterWishList
import com.asl_emp_mng.app.base.adapter.SliderAdapter
import com.asl_emp_mng.app.base.model.ActionModel
import com.asl_emp_mng.app.base.model.DashboardType
import com.asl_emp_mng.app.base.model.DashboardWish
import com.asl_emp_mng.app.databinding.ActivityEmployerDashboardBinding
import com.asl_emp_mng.app.screens.settings.AddEmployeeActivity
import com.asl_emp_mng.app.screens.settings.BranchActivity
import com.asl_emp_mng.app.screens.profile.CompanyProfileActivity
import com.asl_emp_mng.app.screens.settings.LeaveManagementActivity
import com.asl_emp_mng.app.screens.settings.LeaveRequestHistoryActivity
import com.asl_emp_mng.app.screens.settings.PolicyActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.ViewAllEmployeeActivity
import com.asl_emp_mng.app.screens.emp.EmplyeeAttendaceListActivity
import com.asl_emp_mng.app.screens.ui.EmplyeeyerProfile
import com.asl_emp_mng.app.screens.ui.WishListActivity
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.getCompanyDetails
import com.asl_emp_mng.app.utils.getGreetingBasedOnTime
import com.asl_emp_mng.app.utils.getTodayDate
import com.google.gson.Gson

class EmployerDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerDashboardBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    var wishList = ArrayList<DashboardWish>()

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
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        ViewAllEmployeeActivity::class.java
                                    ).apply {
                                        putExtra("FROM", "View All")
                                    }
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

                            "Branches" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        BranchActivity::class.java
                                    )
                                )
                            }

                            "Policy" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        PolicyActivity::class.java
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
            startActivity(Intent(this, EmplyeeAttendaceListActivity::class.java))
        }


        binding.tvProfile.setOnClickListener {
            startActivity(Intent(this, CompanyProfileActivity::class.java))
        }

        binding.tvLetsLeaveViewAll.setOnClickListener {
            startActivity(Intent(this, LeaveRequestHistoryActivity::class.java))
        }

        binding.tvLetsWishViewAll.setOnClickListener {
            startActivity(Intent(this, WishListActivity::class.java).apply {
                putExtra("WishList", Gson().toJson(wishList))
            })
        }
    }

    private fun actionList(): List<ActionModel> {
        mActionList.add(ActionModel("Employee", R.drawable.ic_user))
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