package com.asl_emp_mng.app.screens.settings

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.AdapterEmployeeRecord
import com.asl_emp_mng.app.base.adapter.LeavesManagementAdapter
import com.asl_emp_mng.app.databinding.ActivityEmployeeAttendanceRecordBinding
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Random

class EmployeeAttendanceRecordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEmployeeAttendanceRecordBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityEmployeeAttendanceRecordBinding.inflate(layoutInflater)
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



        settingsViewModel.mGetEmployeeRecordResponse.observe(this) {

            if (it.data.isNotEmpty()) {
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                binding.rvEmpAttendList.setLayoutManager(layoutManager)
                val rvAdapter = AdapterEmployeeRecord(it.data[0].attendances, this)
                binding.rvEmpAttendList.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()
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

            settingsViewModel.getEmployeeAttendRecord(this@EmployeeAttendanceRecordActivity,"11","2025-02")

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false

                settingsViewModel.getEmployeeAttendRecord(this@EmployeeAttendanceRecordActivity,"11","2025-02")
            }
            //progressBar.updateProgress(50.0F)
            binding.cpb.updateProgress(Random().nextInt(100).toFloat())


        }
    }

    fun getDayNameOld(dateString: String, format: String = "yyyy-MM-dd"): String {
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        val date = sdf.parse(dateString)
        val sdfDay = SimpleDateFormat("EEE", Locale.ENGLISH)
        return sdfDay.format(date!!)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val localDate = LocalDate.parse(dateString, formatter)
        val dayOnly = localDate.dayOfMonth

        return dayOnly.toString()
    }
}