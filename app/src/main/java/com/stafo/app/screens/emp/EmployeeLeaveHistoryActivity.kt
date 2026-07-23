package com.stafo.app.screens.emp

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
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterEmployeeAllLeaveList
import com.stafo.app.databinding.ActivityEmployeeLeaveHistoryBinding
import com.stafo.app.screens.emp.adapter.AdapterDynamicLeaveCount
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmpLeaveData
import com.stafo.app.screens.settings.dataClass.GetEmployeeLeaveHistRequestBody
import com.stafo.app.screens.settings.dataClass.LeaveCount
import com.stafo.app.screens.settings.dataClass.LeaveItem
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails

class EmployeeLeaveHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeLeaveHistoryBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var leaveCount: List<LeaveCount>
    private var list: List<GetEmpLeaveData> = listOf()
    private var getLeaveTypeList:List<LeaveItem> = listOf()
    private var filteredList: List<GetEmpLeaveData> = listOf()
    private lateinit var rvAdapter:AdapterEmployeeAllLeaveList
    private lateinit var rvLeaveCountAdapter:AdapterDynamicLeaveCount

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)


        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvEmpLeaveHist.layoutManager = layoutManager
        rvAdapter = AdapterEmployeeAllLeaveList(mutableListOf(), this)
        binding.rvEmpLeaveHist.adapter = rvAdapter


        getEmployeeComId()?.let {
            settingsViewModel.getLeaveTypeList(
                this@EmployeeLeaveHistoryActivity,
                it.toInt()
            )
        }

        onClickListener()
        observeViewModel()

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

                if (!leaveCount.isNullOrEmpty() && getLeaveTypeList.isNotEmpty()) {


                    val enrichedLeaveCount = leaveCount.mapNotNull { count ->
                        val matchingLeaveItem = getLeaveTypeList.find { it.id == count.leaveType }
                        matchingLeaveItem?.let {
                            count.copy(leaveTypeName = it.name)
                        }
                    }

                    if (enrichedLeaveCount.isNotEmpty()) {
                        binding.llcTopParent.visibility = View.VISIBLE
                        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        binding.rvDynamicLeaveCount.layoutManager = layoutManager
                        rvLeaveCountAdapter = AdapterDynamicLeaveCount(enrichedLeaveCount, this)
                        binding.rvDynamicLeaveCount.adapter = rvLeaveCountAdapter
                    } else {
                        binding.llcTopParent.visibility = View.GONE
                    }

                } else {
                    Log.e("LeaveCount", "Waiting for getLeaveTypeList to load")
                    binding.llcTopParent.visibility = View.GONE
                }



               /* if (!leaveCount.isNullOrEmpty() && getLeaveTypeList.isNotEmpty()) {

                    Log.e("LeaveCount", "leaveCount: $leaveCount")
                    Log.e("LeaveCount", "getLeaveTypeList: $getLeaveTypeList")


                    val enrichedLeaveCount = leaveCount.map { count ->
                        val matchingLeaveItem = getLeaveTypeList.find { it.id == count.leaveType }
                        count.copy(leaveTypeName = matchingLeaveItem?.name)
                    }

                    binding.llcTopParent.visibility = View.VISIBLE
                    val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.rvDynamicLeaveCount.layoutManager = layoutManager
                    rvLeaveCountAdapter = AdapterDynamicLeaveCount(enrichedLeaveCount, this)
                    binding.rvDynamicLeaveCount.adapter = rvLeaveCountAdapter
                } else {
                    Log.e("LeaveCount", "Waiting for getLeaveTypeList to load")
                    binding.llcTopParent.visibility = View.GONE
                }*/


            }else{
                binding.txtMsg.visibility=View.VISIBLE
            }



        }

        settingsViewModel.mLeaveTypeListResponse.observe(this) {

            if (it.data.isNotEmpty()) {


                if (!it.data.isNullOrEmpty()) {

                    getLeaveTypeList = it.data

                    val options = mutableListOf<String>()
                    options.add("All")
                    options.addAll(getLeaveTypeList.map { it.name })

                    val adapterSpinner = ArrayAdapter(
                        this@EmployeeLeaveHistoryActivity,
                        R.layout.custom_spinner_item,
                        options
                    )
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSearchType.adapter = adapterSpinner
                    binding.spinnerSearchType.setSelection(0)

                    binding.spinnerSearchType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedValue = parent.getItemAtPosition(position).toString()

                            filteredList = if (selectedValue.equals("All", ignoreCase = true)) {
                                list
                            } else {
                                list.filter {
                                    it.leaveTypeObj.name?.contains(selectedValue, ignoreCase = true) == true
                                }
                            }

                            rvAdapter.updateList(filteredList.toMutableList())
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                } else{
                    val options = mutableListOf<String>()
                    options.add("All")
                    val adapterSpinner = ArrayAdapter(
                        this@EmployeeLeaveHistoryActivity,
                        R.layout.custom_spinner_item,
                        options
                    )
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSearchType.adapter = adapterSpinner
                    binding.spinnerSearchType.setSelection(0)

                    binding.spinnerSearchType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedValue = parent.getItemAtPosition(position).toString()

                            filteredList = if (selectedValue.equals("All", ignoreCase = true)) {
                                list
                            } else {
                                list.filter {
                                    it.leaveTypeObj.name?.contains(selectedValue, ignoreCase = true) == true
                                }
                            }

                            rvAdapter.updateList(filteredList.toMutableList())
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                }




            }
            else{
                val options = mutableListOf<String>()
                options.add("All")
                val adapterSpinner = ArrayAdapter(
                    this@EmployeeLeaveHistoryActivity,
                    R.layout.custom_spinner_item,
                    options
                )
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerSearchType.adapter = adapterSpinner
                binding.spinnerSearchType.setSelection(0)

                binding.spinnerSearchType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedValue = parent.getItemAtPosition(position).toString()

                        filteredList = if (selectedValue.equals("All", ignoreCase = true)) {
                            list
                        } else {
                            list.filter {
                                it.leaveTypeObj.name?.contains(selectedValue, ignoreCase = true) == true
                            }
                        }

                        rvAdapter.updateList(filteredList.toMutableList())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }
            }


        }

    }


    override fun onResume() {
        super.onResume()

        getEmployeeComId()?.let {
            settingsViewModel.getLeaveTypeList(
                this@EmployeeLeaveHistoryActivity,
                it.toInt()
            )
        }

        val request = GetEmployeeLeaveHistRequestBody(
            employeeId = getEmployeeDetails()?.id.toString()
        )

        settingsViewModel.getEmployeeLeaveHist(this@EmployeeLeaveHistoryActivity, request)





    }



  /*  fun GetEmpLeaveData.getLeaveTypeName(): String {
        return when (this.leaveType) {
            1 -> "Casual Leave"
            2 -> "Sick Leave"
            3 -> "Privilege Leave"
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
    }*/

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        finish()
    }
    private fun onClickListener() {
        binding.apply {

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
                onBackPressed()
            }


            imgLeaveRequest.setOnClickListener {
                startActivity(Intent(this@EmployeeLeaveHistoryActivity, EmpLeaveActivity::class.java))
            }


        }
    }
}