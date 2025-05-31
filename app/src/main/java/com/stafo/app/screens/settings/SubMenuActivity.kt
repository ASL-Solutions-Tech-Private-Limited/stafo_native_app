package com.stafo.app.screens.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.SubMneuActionsListAdapter
import com.stafo.app.base.model.ActionModel
import com.stafo.app.databinding.ActivitySubMenuBinding
import com.stafo.app.screens.crm.CRMLeadDashboard
import com.stafo.app.screens.emp.EmpBranchDetailsActivity
import com.stafo.app.screens.emp.EmployeeAttendanceRecordActivity
import com.stafo.app.screens.emp.EmployeeLeaveHistoryActivity
import com.stafo.app.screens.emp.ViewEmpLocationTrackActivity
import com.stafo.app.screens.performance.PerformanceActivity
import com.stafo.app.screens.rank.RankListActivity
import com.stafo.app.screens.reports.ReportsActivity
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin

class SubMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubMenuBinding


    private val mActionList = ArrayList<ActionModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySubMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setOnClickListener()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    private fun setOnClickListener() {
        binding.apply {



            ivBack.setOnClickListener {
                onBackPressed()
            }
            if (getIsCOMPANYLogin(this@SubMenuActivity)==true){
                binding.rvActivities.layoutManager = GridLayoutManager(this@SubMenuActivity, 3)

                val actionsAdapter = SubMneuActionsListAdapter(actionList(),
                    this@SubMenuActivity,
                    object : SubMneuActionsListAdapter.ActionClickListener {
                        override fun onActionClick(action: String) {
                            when (action) {
                                "Employee" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
                                            ViewAllEmployeeActivity::class.java
                                        ).apply {
                                            putExtra("FROM", "View All")
                                        }
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )

                                }
                                "CRM" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
                                            CRMLeadDashboard::class.java
                                        )
                                    )
                                }
                                "Reports" -> {
                                    startActivity(Intent(this@SubMenuActivity, ReportsActivity::class.java))
                                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                                }
                                "Performance"-> {
                                    startActivity(Intent(this@SubMenuActivity, PerformanceActivity::class.java))
                                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                                }
                                "Rank List"-> {
                                    startActivity(Intent(this@SubMenuActivity, RankListActivity::class.java))
                                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                                }
                                "Leaves" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
                                            LeaveManagementActivity::class.java
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
                                            this@SubMenuActivity,
                                            BranchActivity::class.java
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
                                            this@SubMenuActivity,
                                            PolicyActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )

                                }
                                "Location\nTrack" -> {

                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
                                            ViewEmpLocationTrackActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )

                                }
                                "Request\nDevice" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
                                            ViewDeviceRequestEmpActivity::class.java
                                        )
                                    )

                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )

                                }
                                "Holidays" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
                                            HolidayActivity::class.java
                                        )
                                    )
                                }
                            }
                        }

                    })
                rvActivities.adapter = actionsAdapter
            }else{
                binding.rvActivities.layoutManager = GridLayoutManager(this@SubMenuActivity, 3)
                val actionsAdapter = SubMneuActionsListAdapter(empActionList(),
                    this@SubMenuActivity,
                    object : SubMneuActionsListAdapter.ActionClickListener {
                        override fun onActionClick(action: String) {
                            when (action) {
                                "Attendance" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
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

                                "CRM" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
                                            CRMLeadDashboard::class.java
                                        )
                                    )
                                }

                                "Leaves" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
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
                                            this@SubMenuActivity,
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
                                            this@SubMenuActivity,
                                            PolicyActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_from_right,
                                        R.anim.slide_to_left
                                    )
                                }

                                "Holidays" -> {
                                    startActivity(
                                        Intent(
                                            this@SubMenuActivity,
                                            HolidayActivity::class.java
                                        )
                                    )
                                }
                            }
                        }

                    })
                binding.rvActivities.adapter = actionsAdapter
            }

        }
    }


    private fun actionList(): List<ActionModel> {
        mActionList.add(ActionModel("Employee", R.drawable.ic_employee))
        mActionList.add(ActionModel("CRM", R.drawable.ic_crm))
        mActionList.add(ActionModel("Location\nTrack", R.drawable.ic_location))
        mActionList.add(ActionModel("Reports", R.drawable.ic_reports))
        mActionList.add(ActionModel("Performance", R.drawable.ic_performace))
        mActionList.add(ActionModel("Rank List", R.drawable.ic_rank))
        mActionList.add(ActionModel("Leaves", R.drawable.ic_leaves))
        mActionList.add(ActionModel("Branches", R.drawable.ic_branches))
        mActionList.add(ActionModel("Holidays", R.drawable.ic_holidays))
        mActionList.add(ActionModel("Policy", R.drawable.ic_policy))
        mActionList.add(ActionModel("Request\nDevice", R.drawable.ic_device_request))
        return mActionList
    }

    private fun empActionList(): List<ActionModel> {
        mActionList.add(ActionModel("Attendance", R.drawable.ic_attendace))
        mActionList.add(ActionModel("CRM", R.drawable.ic_crm))
        mActionList.add(ActionModel("Leaves", R.drawable.ic_leaves))
        mActionList.add(ActionModel("Branches", R.drawable.ic_branches))
        mActionList.add(ActionModel("Holidays", R.drawable.ic_holidays))
        mActionList.add(ActionModel("Policy", R.drawable.ic_policy))
        return mActionList
    }
}