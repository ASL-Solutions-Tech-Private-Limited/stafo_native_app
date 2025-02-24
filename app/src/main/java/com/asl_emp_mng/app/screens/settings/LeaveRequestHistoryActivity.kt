package com.asl_emp_mng.app.screens.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveData
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveRequestBody
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeComId
import java.util.Collections
import java.util.Random

class LeaveRequestHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaveRequestHistoryBinding
    private lateinit var rvAdapter: AdapterRequestLeaveHistory


    private var leaveList: List<LeaveData> = listOf()
    private var filteredList: List<LeaveData> = listOf()

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLeaveRequestHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)

        onClickListener()
        observeViewModel()
        setupSearchListener()
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mLeaveResponse.observe(this) {


            if (it.data.isNotEmpty()) {

                binding.txtMsg.visibility = View.GONE
                val approvedLeaves = it.data.filter { leave -> leave.status == "approved" }
                val rejectedLeaves = it.data.filter { leave -> leave.status == "rejected" }
                leaveList = approvedLeaves + rejectedLeaves
                filteredList = leaveList

                if (leaveList.isNotEmpty()) {
                    binding.etDirSearch.isFocusable = true
                    binding.etDirSearch.isFocusableInTouchMode = true
                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
                        this@LeaveRequestHistoryActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    binding.rvLeaveList.setLayoutManager(layoutManager)
                    rvAdapter =
                        AdapterRequestLeaveHistory(leaveList, this@LeaveRequestHistoryActivity)
                    binding.rvLeaveList.adapter = rvAdapter
                } else {
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE
                }


            } else {
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
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

            getEmployeeComId()?.let {
                val request = LeaveRequestBody(
                    companyId = it,
                    employeeId = ""
                )
                settingsViewModel.getAllLeaveList(this@LeaveRequestHistoryActivity, request)
            }





            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                getEmployeeComId()?.let {
                    val request = LeaveRequestBody(
                        companyId = it,
                        employeeId = ""
                    )
                    settingsViewModel.getAllLeaveList(this@LeaveRequestHistoryActivity, request)
                }
            }

            imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }


        }
    }


    private fun setupSearchListener() {
        binding.etDirSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            leaveList
        } else {
            leaveList.filter {
                it.employeeBasicInfo.name.contains(query, ignoreCase = true) ||
                        it.employeeBasicInfo.email.contains(query, ignoreCase = true) ||
                        it.employeeBasicInfo.phone.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }
}