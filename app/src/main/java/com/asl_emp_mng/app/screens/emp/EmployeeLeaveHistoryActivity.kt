package com.asl_emp_mng.app.screens.emp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.AdapterEmployeeAllLeaveList
import com.asl_emp_mng.app.databinding.ActivityEmployeeLeaveHistoryBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmployeeLeaveHistRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveCount
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.getEmployeeDetails

class EmployeeLeaveHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeLeaveHistoryBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var leaveCount: List<LeaveCount>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeeLeaveHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)
        onClickListener()
        observeViewModel()

    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mGetEmployeeLeaveHistResponse.observe(this) {

            if (it.data.isNotEmpty()) {
                leaveCount=it.leaveCount
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                binding.rvEmpLeaveHist.setLayoutManager(layoutManager)
                val rvAdapter = AdapterEmployeeAllLeaveList(it.data, this)
                binding.rvEmpLeaveHist.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()

                if (leaveCount.size>2){
                    binding.tvPrivileged.text=leaveCount[0].totalDays
                    binding.tvSick.text=leaveCount[1].totalDays
                    binding.tvCasual.text=leaveCount[2].totalDays
                }



            }


        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    private fun onClickListener() {
        binding?.apply {

            val request = GetEmployeeLeaveHistRequestBody(
                employeeId = getEmployeeDetails()?.id.toString()
            )

            settingsViewModel.getEmployeeLeaveHist(this@EmployeeLeaveHistoryActivity, request)


            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false

                val request = GetEmployeeLeaveHistRequestBody(
                    employeeId = getEmployeeDetails()?.id.toString()
                )

                settingsViewModel.getEmployeeLeaveHist(this@EmployeeLeaveHistoryActivity, request)

            }


            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }
    }
}