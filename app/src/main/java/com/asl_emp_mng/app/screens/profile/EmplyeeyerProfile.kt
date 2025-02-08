package com.asl_emp_mng.app.screens.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.DashboardType
import com.asl_emp_mng.app.base.model.ProfileType
import com.asl_emp_mng.app.base.model.PunchInType
import com.asl_emp_mng.app.databinding.ActivityEmplyeeyerProfileBinding
import com.asl_emp_mng.app.databinding.LayoutAccountSettingsBinding
import com.asl_emp_mng.app.screens.AddBranchActivity
import com.asl_emp_mng.app.screens.AddEmployeeActivity
import com.asl_emp_mng.app.screens.AddShiftActivity
import com.asl_emp_mng.app.screens.EmpLeaveActivity
import com.asl_emp_mng.app.screens.EmpProfileActivity
import com.asl_emp_mng.app.screens.EmployeeAttendance
import com.asl_emp_mng.app.screens.EmployeePunchInActivity
import com.asl_emp_mng.app.screens.LeaveManagementActivity

class EmplyeeyerProfile : AppCompatActivity() {

    private lateinit var secondLayout: LayoutAccountSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_emplyeeyer_profile)

        val binding = ActivityEmplyeeyerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setOnClickEvents(binding)
    }

    private fun setOnClickEvents(binding: ActivityEmplyeeyerProfileBinding) {

        val dashboardType = DashboardType.valueOf(
            intent.getStringExtra("DASHBOARD_TYPE") ?: DashboardType.EMPLOYEE.name
        )

        if (dashboardType == DashboardType.EMPLOYEE) {
            binding.llCompanyProfile.visibility = View.GONE
            binding.llEmployerProfile.visibility = View.VISIBLE

        } else {
            binding.llCompanyProfile.visibility = View.VISIBLE
            binding.llEmployerProfile.visibility = View.GONE
        }



        binding?.expandableAccountSetting?.setOnClickListener {
            binding.expandableAccountSetting.toggleLayout()

        }

        binding?.expandableLeaveManagement?.setOnClickListener {
            binding.expandableLeaveManagement.toggleLayout()
        }

        binding?.expandableAttandancenManagement?.setOnClickListener {
            binding.expandableAttandancenManagement.toggleLayout()
        }

        val branchSettings =
            binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_branch_settings)

        branchSettings?.setOnClickListener {

            startActivity(Intent(this, AddBranchActivity::class.java))

        }

        val shiftSettings =
            binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_shift_settings)

        shiftSettings?.setOnClickListener {

            startActivity(Intent(this, AddShiftActivity::class.java))

        }

        val requestLeave = binding?.expandableLeaveManagement?.findViewById<AppCompatTextView>(R.id.tv_leaves_management)

        requestLeave?.setOnClickListener {
            startActivity(Intent(this, LeaveManagementActivity::class.java))
            }

        // for employee

        binding?.tvLeave?.setOnClickListener {
            startActivity(Intent(this, EmpLeaveActivity::class.java))
        }
        binding?.tvAttendance?.setOnClickListener {
            startActivity(Intent(this, EmployeeAttendance::class.java))
        }
        binding?.expandableProfile?.setOnClickListener {
            binding.expandableProfile.toggleLayout()

        }

        val basicProfile = binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_basic)

        basicProfile?.setOnClickListener {
            val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.BASIC.name)
            startActivity(intent)
        }

        val professionalProfile = binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_professional)
        professionalProfile?.setOnClickListener {
            val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.PROFESSIONAL.name)
            startActivity(intent)
        }
        val educationalProfile = binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_educational)
        educationalProfile?.setOnClickListener {
            val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.EDUCATION.name)
            startActivity(intent)
        }
        val documentProfile = binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_documents)
        documentProfile?.setOnClickListener {
            val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.DOCUMENT.name)
            startActivity(intent)
        }

    }


}