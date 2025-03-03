package com.asl_emp_mng.app.screens.emp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.asl_emp_mng.app.base.model.DateItem
import com.asl_emp_mng.app.databinding.ActivityEmployeeAttendanceRecordBinding
import com.asl_emp_mng.app.screens.dashboard.EmployeeDashboard
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.calculateMinutes
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getIsCOMPANYLogin
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.Random

class EmployeeAttendanceRecordActivity : AppCompatActivity() {
    private val TAG = "EmployeeAttendanceRecor"
    private lateinit var binding:ActivityEmployeeAttendanceRecordBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var mEMPID = ""
    private val calendar = Calendar.getInstance()
    private var mSelectedDate = ""
    private var avgWork: Float? = null
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        val curren = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
        mSelectedDate = curren

        mEMPID = intent.getStringExtra("EMP_ID") ?: ""

        Log.d("res","get :$mEMPID")

        onClickListener()
        observeViewModel()




    }

    private fun fetchAttendanceData() {
        if (getIsCOMPANYLogin()==true){

            settingsViewModel.getMonthlyAttendance(
                this@EmployeeAttendanceRecordActivity,
                mSelectedDate,
                mEMPID
            )
        }else{
            settingsViewModel.getMonthlyAttendance(
                this@EmployeeAttendanceRecordActivity,
                mSelectedDate, getEmployeeDetails()?.id.toString(),
            )
        }
    }

    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
      /*  settingsViewModel.mAttendanceHistoryResponse.observe(this) {
            if (it.status) {
                if (it.data != null) {
                    binding.txtMsg.visibility = View.GONE
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    var mPresentCount = 0
                    var mTotalWorkingHour = 0
                    val mMonth = getAllDatesFromMonth(mSelectedDate)
                    for (month in mMonth.indices) {
                        for (item in it.data.indices) {
                            if (mMonth[month].date == it.data[item].date) {
                                mMonth[month].isPresent = it.data[item].attendance
                                mMonth[month].punchIn = it.data[item].in_time.toString()
                                mMonth[month].punchOut = it.data[item].out_time.toString()
                                if (it.data[item].attendance == "Present") {
                                    mPresentCount++
                                }
                                if (!it.data[item].in_time.isNullOrEmpty()) {
                                    mTotalWorkingHour += calculateMinutes(
                                        it.data[item].in_time.toString(),
                                        it.data[item].out_time.toString()
                                    ).toInt()
                                }
                            }
                        }
                    }
                    Log.e(TAG, "observeViewModel: $mPresentCount")
                    val totalHours = mTotalWorkingHour / 60
                    val totalMinutes = mTotalWorkingHour % 60

// Display the total time in "hh:mm" format
                    val totalWorkingTime = String.format("%02d:%02d", totalHours, totalMinutes)

                   // Calculate the average working hours per day

                    val officeHoursPerDay = 8.0
                    val totalWorkingHours = mTotalWorkingHour / 60.0
                    avgWork = calculateAverageHours(totalWorkingHours, mPresentCount)
                    Log.d("res", "get avg: $avgWork")
                    avgWork?.let {
                        val progress = ((it / officeHoursPerDay) * 100).toFloat()
                        binding.cpb.updateProgress(progress.coerceIn(0f, 100f))
                    }

                    binding.txtTotalPresent.text = mPresentCount.toString() ?: "0"
                    binding.txtTotalWorking.text = totalWorkingTime ?: "00:00"
                binding.rvEmpAttendList.setLayoutManager(layoutManager)
                    val rvAdapter = AdapterEmployeeRecord(mMonth, this)
                binding.rvEmpAttendList.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()
            } else {
                binding.txtMsg.visibility = View.VISIBLE
            }
            }
        }*/

        settingsViewModel.mAttendanceHistoryResponse.observe(this) { response ->
            if (response.status && response.data != null) {
                binding.txtMsg.visibility = View.GONE
                val mMonth = getAllDatesFromMonth(mSelectedDate)
                var mPresentCount = 0
                var mTotalWorkingHour = 0

                for (month in mMonth.indices) {
                    for (item in response.data.indices) {
                        if (mMonth[month].date == response.data[item].date) {
                            mMonth[month].isPresent = response.data[item].attendance
                            mMonth[month].punchIn = response.data[item].in_time.toString()
                            mMonth[month].punchOut = response.data[item].out_time.toString()
                            if (response.data[item].attendance == "Present") {
                                mPresentCount++
                            }
                            if (!response.data[item].in_time.isNullOrEmpty()) {
                                mTotalWorkingHour += calculateMinutes(
                                    response.data[item].in_time.toString(),
                                    response.data[item].out_time.toString()
                                ).toInt()
                            }
                        }
                    }
                }

                val totalHours = mTotalWorkingHour / 60
                val totalMinutes = mTotalWorkingHour % 60
                val totalWorkingTime = String.format("%02d:%02d", totalHours, totalMinutes)

                avgWork = calculateAverageHours(mTotalWorkingHour / 60.0, mPresentCount)
                avgWork?.let {
                    val progress = ((it / 8.0) * 100).toFloat()
                    binding.cpb.updateProgress(progress.coerceIn(0f, 100f))
                }

                binding.txtTotalPresent.text = mPresentCount.toString()
                binding.txtTotalWorking.text = totalWorkingTime

                // ✅ Instead of resetting adapter, just update the list
                val adapter = binding.rvEmpAttendList.adapter as? AdapterEmployeeRecord
                if (adapter != null) {
                    adapter.submitList(mMonth)
                } else {
                    binding.rvEmpAttendList.layoutManager = LinearLayoutManager(this)
                    val newAdapter = AdapterEmployeeRecord(this)
                    binding.rvEmpAttendList.adapter = newAdapter
                    newAdapter.submitList(mMonth)
                }
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

            val currentDate =
                SimpleDateFormat("MMM-yy", Locale.getDefault()).format(calendar.time)
            binding.txtDate.setText(currentDate)

            fetchAttendanceData()

            llCalendar.setOnClickListener {
                showDatePicker()
            }

            imageBack.setOnClickListener {
               onBackPressedDispatcher.onBackPressed()
                finish()
            }



            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                fetchAttendanceData()

            }





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


   /* private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("MMM/yy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.txtDate.setText("$formattedDate")
                mSelectedDate =
                    SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(selectedDate.time)
                settingsViewModel.getMonthlyAttendance(
                    this@EmployeeAttendanceRecordActivity,
                    mSelectedDate, getEmployeeDetails()?.id.toString(),
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }*/


    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { _, year, monthOfYear, _ ->  // Ignore day selection
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, 1) // Always set the 1st of the month

                val dateFormat = SimpleDateFormat("MMM-yy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.txtDate.setText(formattedDate)

                mSelectedDate = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(selectedDate.time)


                if (getIsCOMPANYLogin()==true){

                    settingsViewModel.getMonthlyAttendance(
                        this@EmployeeAttendanceRecordActivity,
                        mSelectedDate,
                        mEMPID
                    )
                }else{
                    settingsViewModel.getMonthlyAttendance(
                        this@EmployeeAttendanceRecordActivity,
                        mSelectedDate, getEmployeeDetails()?.id.toString(),
                    )
                }


            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH) // Set current day
        )

        // Hide the "Day" selector using reflection (may not work on all devices)
        try {
            val datePicker = datePickerDialog.datePicker
            val daySpinner = datePicker.findViewById<View>(
                resources.getIdentifier("day", "id", "android")
            )
            daySpinner?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        datePickerDialog.show()
    }


    fun getAllDatesFromMonth(yearMonth: String): List<DateItem> {
        val dateList = mutableListOf<DateItem>()

        // Parse the input string into year and month
        val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val parsedDate = formatter.parse(yearMonth)

        // Extract year and month from parsed date
        val calendar = Calendar.getInstance()
        calendar.time = parsedDate

        // Get the number of days in the selected month
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Loop through all days and add them to the list
        for (day in 1..maxDays) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            dateList.add(DateItem(date))
        }

        return dateList
    }



    fun calculateAverageHours(totalHours: Double, presentDays: Int): Float? {
        if (presentDays == 0) return 0f
        return (totalHours / presentDays).toFloat()
    }



}