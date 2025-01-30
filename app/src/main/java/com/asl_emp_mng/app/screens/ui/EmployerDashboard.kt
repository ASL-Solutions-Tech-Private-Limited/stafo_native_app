package com.asl_emp_mng.app.screens.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asl_emp_mng.app.databinding.ActivityEmployerDashboardBinding
import com.asl_emp_mng.app.utils.getGreetingBasedOnTime
import com.asl_emp_mng.app.utils.getTodayDate

class EmployerDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setOnClickEvents()
    }

    private fun initViews() {
        binding.apply {

            tvHeaderGreeting.text = getGreetingBasedOnTime()
            tvHeaderEmpName.text = "John Doe"

            tvLetsCheck.text = "Today's Report (${getTodayDate()})"
            rvWishes.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
            val emplyeeListAdapter = EmplyeeListAdapter(this@EmployerDashboard)
            rvWishes.adapter = emplyeeListAdapter

            rvLeaves.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
            val emplyeeListWishAdapter = EmplyeeListAdapter(this@EmployerDashboard)
            rvLeaves.adapter = emplyeeListWishAdapter

        }


    }

    private fun setOnClickEvents() {
        binding.tvHeaderSetting.setOnClickListener {
            startActivity(Intent(this, EmplyeeyerProfile::class.java))
        }
    }
}