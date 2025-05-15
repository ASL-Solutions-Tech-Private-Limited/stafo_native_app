package com.stafo.app.screens.payroll

import android.app.DatePickerDialog
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
import com.stafo.app.databinding.ActivityGenerateSalaryBinding
import com.stafo.app.screens.payroll.dataClass.SalaryRequest
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.SalaryComponent
import com.stafo.app.screens.settings.dataClass.SalaryGeneratedRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.showCustomMonthYearPicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GenerateSalaryActivity : AppCompatActivity() {
    private lateinit var binding:ActivityGenerateSalaryBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var mMonthOfSalary: String = ""
    private var mEMpId: Int = 0
    private val calendar = Calendar.getInstance()

    private lateinit var adapter: DynamicSalaryAdapter
    private lateinit var deductionAdapter: DynamicDeductionAdapter
    private val dynamicFields = mutableListOf<SalaryComponent>()
    private val deductionDynamicFields = mutableListOf<SalaryComponent>()

    private var mEmpList: List<GetEmployee>? = ArrayList()

    private lateinit var employeeListDialog: SearchableDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityGenerateSalaryBinding.inflate(layoutInflater)
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

    private fun observeViewModel() {

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mSalaryGeneratedResponse.observe(this) {

            if (it.success) {

                if (!it.data.basic_salary.isNullOrBlank()) {
                    val salary = it.data.basic_salary.replace(".00", "")
                    binding.tieSalary.setText(salary)
                }

                binding.tvGrossSalary.text = it.data.gross_salary.toString()

                if (it.data.earning.isNotEmpty()) {
                    binding.llcEarning.visibility = View.VISIBLE

                    dynamicFields.clear()
                    dynamicFields.addAll(it.data.earning)

                    adapter = DynamicSalaryAdapter(dynamicFields) {
                        calculateGrossSalary()
                    }

                    binding.recyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.recyclerView.adapter = adapter

                } else {
                    binding.llcEarning.visibility = View.GONE
                }

                if (it.data.deduction.isNotEmpty()) {
                    binding.llcDeduction.visibility = View.VISIBLE

                    deductionDynamicFields.clear()
                    deductionDynamicFields.addAll(it.data.deduction)

                    deductionAdapter = DynamicDeductionAdapter(deductionDynamicFields) {
                        calculateGrossSalary()
                    }

                    binding.rvDeductionList.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvDeductionList.adapter = deductionAdapter

                } else {
                    binding.llcDeduction.visibility = View.GONE
                }
            } else {
                binding.llcDeduction.visibility = View.GONE
                binding.llcEarning.visibility = View.GONE
            }
        }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) {
            if (it.status && it.data.isNotEmpty()) {
                mEmpList = it.data
                setupSearchableDialog(mEmpList ?: emptyList(), "Employee List", binding.tieEmployee)
            }
        }

        settingsViewModel.mSaveSalaryResponse.observe(this) {
            CustomToast(this, it.message)
            if (it.success) {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun calculateGrossSalary() {
        val totalEarning = dynamicFields.sumOf { it.amount?.toString()?.toDoubleOrNull() ?: 0.0 }
        val totalDeduction = deductionDynamicFields.sumOf { it.amount?.toString()?.toDoubleOrNull() ?: 0.0 }
        val grossSalary = totalEarning - totalDeduction

        val formattedGross = if (grossSalary % 1 == 0.0)
            grossSalary.toInt().toString()
        else
            String.format("%.2f", grossSalary)

        binding.tvGrossSalary.text = formattedGross
    }





    private fun onClickListener() {


        binding.apply {



            settingsViewModel.getAllEmployeeList(this@GenerateSalaryActivity)

            tieEmployee.setOnClickListener {
                if (mMonthOfSalary.isBlank()) {
                    CustomToast(this@GenerateSalaryActivity,"Please select a month first")
                } else {
                    employeeListDialog.show()
                }
            }

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }




           tieMonth.setOnClickListener {
                showCustomMonthYearPicker(this@GenerateSalaryActivity) { formattedDate, displayDate ->
                    mMonthOfSalary = formattedDate
                    binding.tieMonth.setText(displayDate)
                    Log.d("date","$mMonthOfSalary")
                }
            }
            btnSubmit.setOnClickListener {
                val basicSalaryStr = binding.tieSalary.text.toString().trim()
                val grossSalaryStr = binding.tvGrossSalary.text.toString().trim()

                val basicSalary = basicSalaryStr.toDoubleOrNull()
                val grossSalary = grossSalaryStr.toDoubleOrNull()

                if (basicSalary == null || grossSalary == null) {
                    CustomToast(this@GenerateSalaryActivity, "Invalid salary values. Please check the inputs.")
                    return@setOnClickListener
                }

                val format = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                val date = format.parse(mMonthOfSalary)

                val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
                val month = monthFormat.format(date).toIntOrNull()

                if (month == null) {
                    CustomToast(this@GenerateSalaryActivity, "Invalid month format.")
                    return@setOnClickListener
                }

                val allComponents = mutableListOf<SalaryComponent>().apply {
                    addAll(dynamicFields)
                    addAll(deductionDynamicFields)
                }

                getEmployeeComId()?.let { companyIdStr ->
                    val companyId = companyIdStr.toIntOrNull()
                    if (companyId == null) {
                        CustomToast(this@GenerateSalaryActivity, "Invalid company ID.")
                        return@setOnClickListener
                    }

                    val request = SalaryRequest(
                        company_id = companyId,
                        employee_id = mEMpId,
                        month = month,
                        basic_salary = basicSalary,
                        gross_salary = grossSalary,
                        components = allComponents
                    )

                    settingsViewModel.saveSalary(this@GenerateSalaryActivity, request)
                } ?: run {
                    CustomToast(this@GenerateSalaryActivity, "Company ID is missing.")
                }
            }


          /*  btnSubmit.setOnClickListener {

                val basicSalary=tieSalary.text.toString().trim()
                val grossSalary=tvGrossSalary.text.toString().trim()

                val format = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                val date = format.parse(mMonthOfSalary)

                val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
                val month = monthFormat.format(date)



                val allComponents = mutableListOf<SalaryComponent>()

                if (dynamicFields.isNotEmpty()) {
                    allComponents.addAll(dynamicFields)
                }

                if (deductionDynamicFields.isNotEmpty()) {
                    allComponents.addAll(deductionDynamicFields)
                }
                getEmployeeComId()?.let {
                    val request = SalaryRequest(
                        company_id = it.toInt(),
                        employee_id = mEMpId,
                        month = month.toInt(),
                        basic_salary = basicSalary.toInt(),
                        gross_salary = grossSalary.toInt(),
                        components = allComponents
                    )

                    settingsViewModel.saveSalary(this@GenerateSalaryActivity, request)
                }

            }*/


        }
    }

    private fun isValidated(): Boolean {
        binding.apply {
            if (tieEmployee.text.isNullOrEmpty()) {
                CustomToast(this@GenerateSalaryActivity,"Please select employee")
                return false
            }  else if (tieMonth.text.isNullOrEmpty()) {
                CustomToast(this@GenerateSalaryActivity,"Please select month")
                return false
            }
        }
        return true
    }



    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }
    private fun setupSearchableDialog(
        dataList: List<Any>?,
        title: String,
        field: TextInputEditText
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

                    mEMpId=searchListItem.id

                    getEmployeeComId()?.let {
                        val request = SalaryGeneratedRequest(
                            employee_id = mEMpId,
                            company_id = it.toInt()
                        )
                        settingsViewModel.generateSalary(this@GenerateSalaryActivity, request)
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