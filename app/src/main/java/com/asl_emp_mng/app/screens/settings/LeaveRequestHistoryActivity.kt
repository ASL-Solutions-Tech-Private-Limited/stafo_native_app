package com.asl_emp_mng.app.screens.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.AdapterRequestLeaveHistory
import com.asl_emp_mng.app.base.adapter.LeavesManagementAdapter
import com.asl_emp_mng.app.databinding.ActivityLeaveManagementBinding
import com.asl_emp_mng.app.databinding.ActivityLeaveRequestHistoryBinding
import java.util.Collections
import java.util.Random

class LeaveRequestHistoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLeaveRequestHistoryBinding
    private lateinit var rvAdapter: AdapterRequestLeaveHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLeaveRequestHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onClickListener()
    }

    private fun onClickListener() {
        binding?.apply {

            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@LeaveRequestHistoryActivity,LinearLayoutManager.VERTICAL,false)
            binding.rvLeaveList.setLayoutManager(layoutManager)
            rvAdapter = AdapterRequestLeaveHistory(this@LeaveRequestHistoryActivity)
            binding.rvLeaveList.adapter = rvAdapter



            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false

            }

           imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }





        }
    }
}