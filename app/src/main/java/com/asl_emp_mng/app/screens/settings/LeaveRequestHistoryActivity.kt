package com.asl_emp_mng.app.screens.settings

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.AdapterRequestLeaveHistory
import com.asl_emp_mng.app.base.adapter.LeavesManagementAdapter
import com.asl_emp_mng.app.databinding.ActivityLeaveManagementBinding
import com.asl_emp_mng.app.databinding.ActivityLeaveRequestHistoryBinding
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveRequestBody
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import java.util.Collections
import java.util.Random

class LeaveRequestHistoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLeaveRequestHistoryBinding
    private lateinit var rvAdapter: AdapterRequestLeaveHistory

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)

        onClickListener()
        observeViewModel()
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mLeaveResponse.observe(this) {

            if (it.data.isNotEmpty()) {
                val approvedLeaves = it.data.filter { leave -> leave.status == "approved" }
                val rejectedLeaves = it.data.filter { leave -> leave.status == "rejected" }
                val approvedRejectedLeaves = approvedLeaves + rejectedLeaves


                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@LeaveRequestHistoryActivity,LinearLayoutManager.VERTICAL,false)
                binding.rvLeaveList.setLayoutManager(layoutManager)
                rvAdapter = AdapterRequestLeaveHistory(approvedRejectedLeaves,this@LeaveRequestHistoryActivity)
                binding.rvLeaveList.adapter = rvAdapter
            } else {
                binding.txtMsg.visibility = View.VISIBLE
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

            val request = LeaveRequestBody(
                companyId = "1",
                employeeId = ""
            )

            settingsViewModel.getAllLeaveList(this@LeaveRequestHistoryActivity, request)



            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                val request = LeaveRequestBody(
                    companyId = "1",
                    employeeId = ""
                )

                settingsViewModel.getAllLeaveList(this@LeaveRequestHistoryActivity, request)

            }

           imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }





        }
    }
}