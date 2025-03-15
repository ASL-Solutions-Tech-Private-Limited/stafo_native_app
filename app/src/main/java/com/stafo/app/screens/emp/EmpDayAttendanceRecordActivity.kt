package com.stafo.app.screens.emp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterEmpDayAttendance
import com.stafo.app.databinding.ActivityEmpDayAttendanceRecordBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.DayPunchINRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmpDayAttendanceRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmpDayAttendanceRecordBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var mEMPID = ""
    private val calendar = Calendar.getInstance()
    private var mSelectedDate = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpDayAttendanceRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        val curren = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        mSelectedDate = curren

        mEMPID = intent.getStringExtra("EMP_ID") ?: ""
        onClickListener()
        observeViewModel()
    }

    private fun onClickListener() {
        binding?.apply {

            val currentDate = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(calendar.time)
            binding.txtDate.setText(currentDate)

            fetchAttendanceData(mSelectedDate)

            llCalendar.setOnClickListener {
                showDatePicker()
            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }



            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                fetchAttendanceData(mSelectedDate)

            }


        }
    }
    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }


        settingsViewModel.mDayPunchINEmpResponse.observe(this) { response ->
            if (response.status && response.data != null) {
                binding.txtMsg.visibility = View.GONE
                binding.rvEmpAttendList.layoutManager = LinearLayoutManager(this)
                val newAdapter = AdapterEmpDayAttendance(response.data,this)
                binding.rvEmpAttendList.adapter = newAdapter
                newAdapter.notifyDataSetChanged()
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

    private fun fetchAttendanceData(date:String) {
        if (getIsCOMPANYLogin() == true) {

            val request = DayPunchINRequest(
                employee_id = mEMPID,
                date = date
            )

            settingsViewModel.getDayAttendanceRecordEmp(
                this@EmpDayAttendanceRecordActivity,
                request
            )
        } else {

            val request = DayPunchINRequest(
                employee_id = getEmployeeDetails()?.id.toString(),
                date = date
            )
            settingsViewModel.getDayAttendanceRecordEmp(
                this@EmpDayAttendanceRecordActivity,
                request
            )
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.txtDate.setText("$formattedDate")
                mSelectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                fetchAttendanceData(mSelectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}