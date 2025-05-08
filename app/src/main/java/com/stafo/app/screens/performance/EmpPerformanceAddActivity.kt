package com.stafo.app.screens.performance

import android.os.Bundle
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
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.textfield.TextInputEditText
import com.stafo.app.R
import com.stafo.app.base.adapter.DynamicDeductionAdapter
import com.stafo.app.base.adapter.DynamicSalaryAdapter
import com.stafo.app.databinding.ActivityEmpPerformanceAddBinding
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.chat.dataClass.ChatRequest
import com.stafo.app.screens.payroll.dataClass.SalaryRequest
import com.stafo.app.screens.performance.adapter.AdapterPerformanceType
import com.stafo.app.screens.performance.adapter.DynamicPerformanceAdapter
import com.stafo.app.screens.performance.dataClass.PerformanceAddRequest
import com.stafo.app.screens.performance.dataClass.PerformanceInput
import com.stafo.app.screens.performance.dataClass.PerformanceTypeList
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.SalaryComponent
import com.stafo.app.screens.settings.dataClass.SalaryGeneratedRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.showCustomMonthYearPicker
import java.text.SimpleDateFormat
import java.util.Locale

class EmpPerformanceAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmpPerformanceAddBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()

    private var mEmpList: List<GetEmployee>? = ArrayList()
    private lateinit var employeeListDialog: SearchableDialog

    private var mSelectMonth: String = ""
    private var mEMpId: Int = 0

    private lateinit var adapterDynamic: DynamicPerformanceAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpPerformanceAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()

    }

    private fun onClickListener() {


        binding.apply {

            getEmployeeComId()?.let {

                val request = ChatRequest(
                    company_id = it
                )

                billPaymentsViewModel.getPerformanceTypeList(
                    this@EmpPerformanceAddActivity, request
                )
            }

            settingsViewModel.getAllEmployeeList(this@EmpPerformanceAddActivity)

            tieEmployee.setOnClickListener {
                if (mSelectMonth.isBlank()) {
                    CustomToast(this@EmpPerformanceAddActivity, "Please select a month first")
                } else {
                    employeeListDialog.show()
                }
            }

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }




            tieMonth.setOnClickListener {
                showCustomMonthYearPicker(this@EmpPerformanceAddActivity) { formattedDate, displayDate ->
                    mSelectMonth = formattedDate
                    binding.tieMonth.setText(displayDate)
                    Log.d("date", "$mSelectMonth")
                }
            }


            btnSubmit.setOnClickListener {

                if (mSelectMonth.isNotBlank() && mEMpId != 0) {
                    val userInputList = adapterDynamic.getUserInput()

                    userInputList.forEach {
                        Log.d("INPUT", "type_id = ${it.type_id}, points = ${it.points}")
                    }
                    val (year, month) = mSelectMonth.split("-")
                    val request = PerformanceAddRequest(
                        emp_id = mEMpId, month = month, year = year, perform = userInputList
                    )

                    billPaymentsViewModel.savePerformance(
                        this@EmpPerformanceAddActivity, request
                    )
                } else CustomToast(
                    this@EmpPerformanceAddActivity, "Please select both employee and month"
                )


            }


        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) {

            if (it.status) {


                if (it.data.isNotEmpty()) {

                    mEmpList = it.data
                    binding.let { it1 ->
                        setupSearchableDialog(
                            mEmpList, "Employee List", it1.tieEmployee
                        )
                    }


                }


            }
        }


        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mPerformanceTypeResponse.observe(this) {

            if (it.success) {

                if (!it.data.isNullOrEmpty()) {

                    val performanceInputList = it.data.map { type ->
                        PerformanceInput(performanceType = type)
                    }.toMutableList()

                    adapterDynamic = DynamicPerformanceAdapter(performanceInputList)

                    val layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvDynamicType.layoutManager = layoutManager
                    binding.rvDynamicType.adapter = adapterDynamic


                }

            }


        }

        billPaymentsViewModel.mPerformanceAddResponse.observe(this) {

            if (it.success) {
                CustomToast(this, it.message)
                val intent = intent
                finish()
                startActivity(intent)
            } else CustomToast(this, it.message)


        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun setupSearchableDialog(
        dataList: List<Any>?, title: String, field: TextInputEditText
    ) {
        val items = dataList?.map {
            val name = when (it) {
                is GetEmployee -> it.name
                else -> "Unknown"
            }

            val id = when (it) {
                is GetEmployee -> it.id
                else -> -1
            }

            SearchListItem(id, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)
                if (title == "Employee List") {

                    mEMpId = searchListItem.id

                    getEmployeeComId()?.let {
                        val request = SalaryGeneratedRequest(
                            employee_id = mEMpId, company_id = it.toInt()
                        )
                        settingsViewModel.generateSalary(this@EmpPerformanceAddActivity, request)
                    }

                }

                dialog.dismiss()
            }
        })

        when (title) {
            "Employee List" -> employeeListDialog = dialog
        }
    }
}