package com.asl_emp_mng.app.screens.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityEmplyeeyerProfileBinding
import com.asl_emp_mng.app.databinding.LayoutAccountSettingsBinding
import com.asl_emp_mng.app.screens.AddBranchActivity
import com.asl_emp_mng.app.screens.AddEmployeeActivity
import com.asl_emp_mng.app.screens.AddShiftActivity
import com.asl_emp_mng.app.screens.LeaveManagementActivity

class EmplyeeyerProfile : AppCompatActivity() {

    private lateinit var secondLayout:LayoutAccountSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_emplyeeyer_profile)

        val binding = ActivityEmplyeeyerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setOnClickEvents(binding)
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

        val branchSettings = binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_branch_settings)

        branchSettings?.setOnClickListener {

            startActivity(Intent(this, AddBranchActivity::class.java))

        }

        val shiftSettings = binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_shift_settings)

        shiftSettings?.setOnClickListener {

            startActivity(Intent(this, AddShiftActivity::class.java))

        }

        val requestLeave = binding?.expandableLeaveManagement?.findViewById<AppCompatTextView>(R.id.tv_leaves_management)
        requestLeave?.setOnClickListener {

            startActivity(Intent(this, LeaveManagementActivity::class.java))

        }

    }


}