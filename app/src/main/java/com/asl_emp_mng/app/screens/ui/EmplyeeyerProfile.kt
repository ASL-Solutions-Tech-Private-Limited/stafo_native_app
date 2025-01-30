package com.asl_emp_mng.app.screens.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asl_emp_mng.app.databinding.ActivityEmplyeeyerProfileBinding

class EmplyeeyerProfile : AppCompatActivity() {
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

    }


}