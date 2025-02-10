package com.asl_emp_mng.app.screens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.EmpItemAdapter
import com.asl_emp_mng.app.base.adapter.LeavesManagementAdapter
import com.asl_emp_mng.app.base.model.EmpConstants
import com.asl_emp_mng.app.base.model.Employee
import com.asl_emp_mng.app.base.model.LeavesManagementModel
import com.asl_emp_mng.app.databinding.ActivitySetAttendanceBinding
import java.util.Collections
import java.util.Random

class SetAttendanceActivity : AppCompatActivity() {

    private lateinit var binding :ActivitySetAttendanceBinding

    private lateinit var rvAdapter: EmpItemAdapter
    private lateinit var employeeList : ArrayList<Employee>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySetAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadEmpList()
        onClickListener()


    }


    private fun loadEmpList() {
        employeeList= EmpConstants.getEmployeeData()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvEmpList.setLayoutManager(layoutManager)
        rvAdapter = EmpItemAdapter(employeeList)
        binding.rvEmpList.adapter = rvAdapter
        rvAdapter.notifyDataSetChanged()
    }
    private fun onClickListener() {
        binding?.apply {


            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                Collections.shuffle(employeeList, Random(System.currentTimeMillis()))

                rvAdapter.notifyDataSetChanged()

            }






        }
    }
}