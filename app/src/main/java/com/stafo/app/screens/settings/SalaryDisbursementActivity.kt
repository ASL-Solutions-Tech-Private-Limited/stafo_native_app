package com.stafo.app.screens.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.stafo.app.R
import com.stafo.app.base.adapter.EmpListAdapter
import com.stafo.app.base.adapter.RadioShiftAdapter
import com.stafo.app.databinding.ActivitySalaryDisbursementBinding
import com.stafo.app.screens.settings.adapter.PayoutEmpListAdapter
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.SetAttendanceTypeRequest
import com.stafo.app.screens.settings.dataClass.Shift
import com.stafo.app.screens.settings.dataClass.ShiftDataList
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class SalaryDisbursementActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalaryDisbursementBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: PayoutEmpListAdapter
    private var empList: List<GetEmployee> = listOf()
    private var filteredList: List<GetEmployee> = listOf()

    //bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var isSearchable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySalaryDisbursementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setOnCLickListener()
        observeViewModel()
        setupSearchListener()


    }


    override fun onResume() {
        super.onResume()
        settingsViewModel.getAllEmployeeList(this@SalaryDisbursementActivity)
    }

    private fun setOnCLickListener() {
        binding.apply {
            settingsViewModel.getAllEmployeeList(this@SalaryDisbursementActivity)


            binding.swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = false
                settingsViewModel.getAllEmployeeList(this@SalaryDisbursementActivity)

            }

            binding.imageBack.setOnClickListener {
                onBackPressed()
            }
        }

    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) {

            if (it.status) {


                if (it.data.isNotEmpty()) {

                    isSearchable = true



                    empList = it.data
                    filteredList = empList

                    val layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvViewEmpList.setLayoutManager(layoutManager)
                    rvAdapter = PayoutEmpListAdapter(
                        empList, this, object : PayoutEmpListAdapter.onGeoClick {
                            override fun onEMPClick(empID: String, type: String, position: Int) {/*if (type == "Request Location") {
                                    settingsViewModel.sendGeoLocationRequest(
                                        this@SalaryDisbursementActivity,
                                        empID, "0"
                                    )
                                }*/
                            }

                        })
                    binding.rvViewEmpList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()


                } else {
                    isSearchable = false
                    binding.txtMsg.visibility = View.VISIBLE
                }


            } else {
                isSearchable = false
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


    private fun setupSearchListener() {
        binding.etDirSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isSearchable) filterList(s.toString()) else binding.txtMsg.visibility =
                    View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            empList
        } else {
            empList.filter {
                it.name.contains(query, ignoreCase = true) || it.phone.contains(
                    query, ignoreCase = true
                )
            }
        }

        rvAdapter.updateList(filteredList)
    }


    @SuppressLint("MissingInflatedId")
    fun showCustomBottomSheet(id: Int, getAttendanceType: String?) {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.payout_employee_salary_bottom_sheet, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)

        }

        bottomSheetDialog.setCancelable(false)

        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)
        val accountNo = view.findViewById<TextInputEditText>(R.id.tie_account_no)
        val paymentType = view.findViewById<TextInputEditText>(R.id.tie_payment_type)
        val amount = view.findViewById<TextInputEditText>(R.id.tie_amount)
        val btnPayNow = view.findViewById<AppCompatButton>(R.id.btn_pay_now)

        accountNo.setText("xxxxxxxxxxxxx915")
        paymentType.setText("IMPS")
        amount.setText("10000")


        btnPayNow.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(
                Intent(
                    this@SalaryDisbursementActivity, SuccessActivity::class.java
                )
            )
            overridePendingTransition(
                R.anim.slide_from_right, R.anim.slide_to_left
            )

        }



        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }





        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()


    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}