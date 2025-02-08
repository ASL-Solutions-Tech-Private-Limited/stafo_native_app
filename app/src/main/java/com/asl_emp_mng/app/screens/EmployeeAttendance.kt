package com.asl_emp_mng.app.screens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.EmployeeAttendanceAdapter
import com.asl_emp_mng.app.base.model.EmployeeAttendanceModel
import com.asl_emp_mng.app.databinding.ActivityEmployeeAttendanceBinding
import java.util.Collections
import java.util.Random

class EmployeeAttendance : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeAttendanceBinding
    private lateinit var rvAdapter: EmployeeAttendanceAdapter
    private lateinit var attendList: List<EmployeeAttendanceModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeeAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)

        onClickListener()

        loadAttendList()

    }

    private fun loadAttendList() {
        attendList = listOf(
            EmployeeAttendanceModel("Hamid", "09:32 AM", "confirm_check_in", "04/02/2025",true),
            EmployeeAttendanceModel("Hamid", "09:10 AM", "confirm_check_in", "04/02/2025",true),
            EmployeeAttendanceModel("Hamid", "09:12 AM", "confirm_check_in", "04/02/2025",false),
            EmployeeAttendanceModel("Hamid", "09:45 AM", "confirm_check_in", "04/02/2025",false),
            EmployeeAttendanceModel("Hamid", "09:22 AM", "confirm_check_in", "04/02/2025",true),
            EmployeeAttendanceModel("Hamid", "10:22 AM", "confirm_check_in", "04/02/2025",true)


        )
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvEmpAttendList.setLayoutManager(layoutManager)
        rvAdapter = EmployeeAttendanceAdapter(attendList, this)
        binding.rvEmpAttendList.adapter = rvAdapter
        rvAdapter.notifyDataSetChanged()
    }

    private fun onClickListener() {
        binding?.apply {

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                Collections.shuffle(attendList, Random(System.currentTimeMillis()))
                rvAdapter.notifyDataSetChanged()

            }

            //progressBar.updateProgress(50.0F)
            progressBar.updateProgress(Random().nextInt(100).toFloat())


        }
    }
}