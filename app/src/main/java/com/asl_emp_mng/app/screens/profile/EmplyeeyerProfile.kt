package com.asl_emp_mng.app.screens.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.ProfileType
import com.asl_emp_mng.app.databinding.ActivityEmplyeeyerProfileBinding
import com.asl_emp_mng.app.screens.EmpProfileActivity
import com.asl_emp_mng.app.screens.emp.EmpLeaveActivity
import com.asl_emp_mng.app.screens.emp.EmployeeAttendance
import com.asl_emp_mng.app.screens.settings.AddShiftActivity
import com.asl_emp_mng.app.screens.settings.BranchActivity
import com.asl_emp_mng.app.screens.settings.HolidayActivity
import com.asl_emp_mng.app.screens.settings.LeaveManagementActivity
import com.asl_emp_mng.app.screens.settings.PolicyActivity
import com.asl_emp_mng.app.screens.settings.ViewAllEmployeeActivity
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.doLogout
import com.asl_emp_mng.app.utils.getCompanyDetails
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getIsCOMPANYLogin
import com.google.gson.Gson

class EmplyeeyerProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityEmplyeeyerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView(binding)
        setOnClickEvents(binding)
    }

    private fun setupView(binding: ActivityEmplyeeyerProfileBinding) {
        binding?.apply {
            Log.e("TAG", "setupView: ${Gson().toJson(getEmployeeDetails())}")
            if (getIsCOMPANYLogin() == true) {
                tvHeaderEmpName.text = getCompanyDetails()?.companyName ?: "Guest"
                tvHeaderEmpEmail.text = getCompanyDetails()?.email ?: "--"
                binding.llCompanyProfile.visibility = View.VISIBLE
                binding.llEmployerProfile.visibility = View.GONE
            } else {
                tvHeaderEmpName.text = getEmployeeDetails()?.name ?: "Guest"
                tvHeaderEmpEmail.text = getEmployeeDetails()?.email ?: "--"
                binding.llCompanyProfile.visibility = View.GONE
                binding.llEmployerProfile.visibility = View.VISIBLE

            }
        }
    }


    private fun setOnClickEvents(binding: ActivityEmplyeeyerProfileBinding) {

        binding?.expandableAccountSetting?.setOnClickListener {
            binding.expandableAccountSetting.toggleLayout()

        }

        binding?.expandableLeaveManagement?.setOnClickListener {
            binding.expandableLeaveManagement.toggleLayout()
        }

        binding?.expandableAttandancenManagement?.setOnClickListener {
            binding.expandableAttandancenManagement.toggleLayout()
        }

        binding?.expandableOtherManagement?.setOnClickListener {
            binding.expandableOtherManagement.toggleLayout()
        }

        val holidaySettings =
            binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_holiday_settings)

        holidaySettings?.setOnClickListener {
            startActivity(Intent(this, HolidayActivity::class.java))
        }

        val branchSettings =
            binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_branch_settings)

        branchSettings?.setOnClickListener {

            startActivity(Intent(this, BranchActivity::class.java))

        }

        val setAttendanceSetting =
            binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_set_attendance_settings)

        setAttendanceSetting?.setOnClickListener {

            startActivity(
                Intent(
                    this@EmplyeeyerProfile,
                    ViewAllEmployeeActivity::class.java
                ).apply {
                    putExtra("FROM","SetAttendance")
                }
            )

        }


        val shiftSettings =
            binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_shift_settings)

        shiftSettings?.setOnClickListener {

            startActivity(Intent(this, AddShiftActivity::class.java))

        }


        val officePolicy = binding?.expandableLeaveManagement?.findViewById<AppCompatTextView>(R.id.tv_office_policy)

        officePolicy?.setOnClickListener {
            startActivity(Intent(this, PolicyActivity::class.java))
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

        binding.expandableOtherManagement.findViewById<AppCompatTextView>(R.id.tv_logout)
            .setOnClickListener {
            doLogout(this)
        }

        val basicProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_basic)

        basicProfile?.setOnClickListener {
            val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.BASIC.name)
            startActivity(intent)
        }

        val professionalProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_professional)
        professionalProfile?.setOnClickListener {
            val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.PROFESSIONAL.name)
            startActivity(intent)
        }
        val educationalProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_educational)
        educationalProfile?.setOnClickListener {

            CustomToast(this,"Working is progress")
          /*  val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.EDUCATION.name)
            startActivity(intent)*/
        }
        val documentProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_documents)
        documentProfile?.setOnClickListener {
            CustomToast(this,"Working is progress")
          /*  val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.DOCUMENT.name)
            startActivity(intent)*/
        }

        binding.tvEmpLogout.setOnClickListener {
            doLogout(this)
        }
    }


}