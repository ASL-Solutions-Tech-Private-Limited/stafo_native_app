package com.asl_emp_mng.app.screens.emp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.asl_emp_mng.app.screens.settings.dataClass.BranchItem
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmpLeaveData
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmployeeLeaveHistRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveCount
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.getEmployeeDetails

class EmployeeLeaveHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeLeaveHistoryBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var leaveCount: List<LeaveCount>
    private var list: List<GetEmpLeaveData> = listOf()
    private var filteredList: List<GetEmpLeaveData> = listOf()
    private lateinit var rvAdapter:AdapterEmployeeAllLeaveList

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


        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvEmpLeaveHist.layoutManager = layoutManager
        rvAdapter = AdapterEmployeeAllLeaveList(mutableListOf(), this)
        binding.rvEmpLeaveHist.adapter = rvAdapter

        onClickListener()
        observeViewModel()

    }


    override fun onResume() {
        super.onResume()
        val request = GetEmployeeLeaveHistRequestBody(
            employeeId = getEmployeeDetails()?.id.toString()
        )

        settingsViewModel.getEmployeeLeaveHist(this@EmployeeLeaveHistoryActivity, request)
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mGetEmployeeLeaveHistResponse.observe(this) {

            if (it.data.isNotEmpty()) {

                binding.txtMsg.visibility = View.GONE

                list=it.data
                filteredList=list

                leaveCount=it.leaveCount
                rvAdapter.updateList(filteredList.toMutableList())

                if (leaveCount.size>2){
                    binding.tvPrivileged.text=leaveCount[0].totalDays
                    binding.tvSick.text=leaveCount[1].totalDays
                    binding.tvCasual.text=leaveCount[2].totalDays
                }


            }else{
               binding.txtMsg.visibility=View.VISIBLE
            }



        }


    }

    fun GetEmpLeaveData.getLeaveTypeName(): String {
        return when (this.leaveType) {
            1 -> "Casual Leave"
            2 -> "Sick Leave"
            3 -> "Privillage Leave"
            else -> "All"
        }
    }


    private fun filterList(query: String) {
        val filteredList = if (query.isEmpty()) {
            list
        } else {
            list.filter {
                it.getLeaveTypeName().contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList.toMutableList())
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


            val options = resources.getStringArray(R.array.leave_type_search)
            val adapterSpinner = ArrayAdapter(this@EmployeeLeaveHistoryActivity, R.layout.custom_spinner_item, options)
            binding.spinnerSearchType.setAdapter(adapterSpinner)
            binding.spinnerSearchType.setSelection(0)
            binding.spinnerSearchType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedValue = parent.getItemAtPosition(position).toString()
                    if (selectedValue.equals("All", ignoreCase = true)) {
                        filteredList = list
                    } else {
                        filteredList = list.filter { it.getLeaveTypeName().contains(selectedValue, ignoreCase = true) }
                    }
                    rvAdapter.updateList(filteredList.toMutableList())
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }







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


            imgLeaveRequest.setOnClickListener {
                startActivity(Intent(this@EmployeeLeaveHistoryActivity, EmpLeaveActivity::class.java))
            }


        }
    }
}