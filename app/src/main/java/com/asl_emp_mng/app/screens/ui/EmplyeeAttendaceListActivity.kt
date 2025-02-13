package com.asl_emp_mng.app.screens.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asl_emp_mng.app.databinding.ActivityEmployeeAttendanceBinding

class EmplyeeAttendaceListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeAttendanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}