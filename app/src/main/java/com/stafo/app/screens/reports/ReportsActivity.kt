package com.stafo.app.screens.reports

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterDownloadReports
import com.stafo.app.databinding.ActivityReportsBinding
import com.stafo.app.screens.emp.EmployeeAttendanceRecordActivity
import com.stafo.app.screens.payroll.CreateSalaryTypeActivity
import com.stafo.app.screens.payroll.GenerateSalaryActivity
import com.stafo.app.screens.payroll.SalarySlipActivity
import com.stafo.app.screens.payroll.SalaryTypeActivity
import com.stafo.app.screens.payroll.SalaryTypeListAdapter
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.OwnerInfo
import com.stafo.app.screens.settings.dataClass.SalaryTypeListRequest
import com.stafo.app.screens.settings.dataClass.UpdateCompanyProfile
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class ReportsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportsBinding
    private lateinit var rvAdapter: AdapterDownloadReports

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReportsBinding.inflate(layoutInflater)
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


            imageBack.setOnClickListener {
                onBackPressed()
            }

            binding.rdgpCompanyReports.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radio_report -> {
                        binding.llcReports.visibility = View.VISIBLE
                        binding.llcDownload.visibility = View.GONE

                    }

                    R.id.radio_download -> {
                        binding.llcReports.visibility = View.GONE
                        binding.llcDownload.visibility = View.VISIBLE
                        getEmployeeComId()?.let {
                            settingsViewModel.viewAllReportsList(this@ReportsActivity, it)
                        }
                    }

                }


            }






            rtlAttendanceReport.setOnClickListener {
              /*  startActivity(Intent(this@ReportsActivity,AttendanceReportActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)*/

                val intent = Intent(this@ReportsActivity, AttendanceReportActivity::class.java).apply {
                    putExtra("reports_type", "attendance_reports")
                }
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                startActivity(intent)
            }

            rtlEmployeeReports.setOnClickListener {
                val intent = Intent(this@ReportsActivity, AttendanceReportActivity::class.java).apply {
                    putExtra("reports_type", "emp_reports")
                }
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                startActivity(intent)
            }

            binding.expandablePayrollSetting.setOnClickListener {
                binding.expandablePayrollSetting.toggleLayout()

            }
            val rtlSalaryType = binding.expandablePayrollSetting.findViewById<RelativeLayout>(R.id.rtl_salary_type)
            val rtlGenerateSalary = binding.expandablePayrollSetting.findViewById<RelativeLayout>(R.id.rtl_generate_salary)
            val rtlSalarySlip = binding.expandablePayrollSetting.findViewById<RelativeLayout>(R.id.rtl_salary_slip)

            rtlSalaryType.setOnClickListener {
                startActivity(Intent(this@ReportsActivity, SalaryTypeActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            }
            rtlGenerateSalary.setOnClickListener {
                startActivity(Intent(this@ReportsActivity, GenerateSalaryActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            }

            rtlSalarySlip.setOnClickListener {
                startActivity(Intent(this@ReportsActivity, SalarySlipActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            }


        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
        finish()
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mAllReportsListResponse.observe(this) {



            if (it.success){
                if (it.data.isNotEmpty()) {
                    binding.txtMsg.visibility = View.GONE
                    binding.rvDownloadList.visibility = View.VISIBLE


                    binding.rvDownloadList.layoutManager = LinearLayoutManager(this@ReportsActivity, LinearLayoutManager.VERTICAL, false)
                    rvAdapter=AdapterDownloadReports(it.data,this@ReportsActivity)
                    binding.rvDownloadList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()

                } else {
                    binding.rvDownloadList.visibility = View.GONE
                    binding.txtMsg.visibility = View.VISIBLE
                }

            } else {
                binding.rvDownloadList.visibility = View.GONE
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

}