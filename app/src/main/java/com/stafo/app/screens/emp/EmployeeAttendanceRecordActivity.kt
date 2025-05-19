package com.stafo.app.screens.emp

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterEmployeeRecord
import com.stafo.app.base.model.DateItem
import com.stafo.app.databinding.ActivityEmployeeAttendanceRecordBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.calculateMinutes
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.stafo.app.utils.convertTo12HourFormat2
import com.stafo.app.utils.convertTo12HourFormat3
import com.stafo.app.utils.showCustomMonthYearPicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class EmployeeAttendanceRecordActivity : AppCompatActivity() {
    private val TAG = "EmployeeAttendanceRecor"
    private lateinit var binding: ActivityEmployeeAttendanceRecordBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var mEMPID = ""
    private val calendar = Calendar.getInstance()
    private var mSelectedDate = ""
    private var avgWork: Float? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeeAttendanceRecordBinding.inflate(layoutInflater)
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


        onClickListener()
        observeViewModel()


    }

    private fun fetchAttendanceData() {
        if (getIsCOMPANYLogin(this) == true) {

            settingsViewModel.getMonthlyAttendance(
                this@EmployeeAttendanceRecordActivity,
                mSelectedDate,
                mEMPID
            )
        } else {
            settingsViewModel.getMonthlyAttendance(
                this@EmployeeAttendanceRecordActivity,
                mSelectedDate, getEmployeeDetails()?.id.toString(),
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

                val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val todayAttendance = response.data.find { it.date == today }

                if (todayAttendance != null) {
                    val inTime = todayAttendance.in_time?.takeIf { it.isNotBlank() }
                    val outTime = todayAttendance.out_time?.takeIf { it.isNotBlank() }

                    val timeToShow = when {
                        outTime != null -> convertTo12HourFormat3(outTime)
                        inTime != null -> convertTo12HourFormat3(inTime)
                        else -> "--"
                    }

                    binding.tvTodayTime.text = timeToShow

                } else binding.tvTodayTime.text = "00.00"






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
                    val progress = ((it / 9.0) * 100).toFloat()
                    binding.cpb.updateProgress(progress.coerceIn(0f, 100f))
                }

                binding.txtTotalPresent.text = mPresentCount.toString()
                binding.txtTotalWorking.text = totalWorkingTime

                val adapter = binding.rvEmpAttendList.adapter as? AdapterEmployeeRecord
                if (adapter != null) {
                    adapter.submitList(mMonth)
                } else {
                    binding.rvEmpAttendList.layoutManager = GridLayoutManager(this,7)
                    val newAdapter = AdapterEmployeeRecord(this, mEMPID)
                    binding.rvEmpAttendList.adapter = newAdapter
                    newAdapter.submitList(mMonth)

                    newAdapter.submitList(mMonth) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val currentDatePosition = newAdapter.getCurrentDatePosition()
                            if (currentDatePosition != -1) {
                                binding.rvEmpAttendList.smoothScrollToPosition(currentDatePosition) // Smooth scrolling
                            }
                        }, 300)
                    }
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        finish()
    }

    private fun onClickListener() {
        binding.apply {

            val currentDate =
                SimpleDateFormat("MMM yy", Locale.getDefault()).format(calendar.time)
            binding.txtDate.setText(currentDate)

            fetchAttendanceData()

           /* llCalendar.setOnClickListener {
                showDatePicker()
            }*/

            llCalendar.setOnClickListener {
                showCustomMonthYearPicker(this@EmployeeAttendanceRecordActivity) { formattedDate, displayDate ->
                    mSelectedDate = formattedDate
                    binding.txtDate.text = displayDate
                    if (getIsCOMPANYLogin(this@EmployeeAttendanceRecordActivity) == true) {
                        settingsViewModel.getMonthlyAttendance(
                            this@EmployeeAttendanceRecordActivity,
                            mSelectedDate,
                            mEMPID
                        )
                    } else {
                        settingsViewModel.getMonthlyAttendance(
                            this@EmployeeAttendanceRecordActivity,
                            mSelectedDate, getEmployeeDetails()?.id.toString(),
                        )
                    }
                }
            }

            imageBack.setOnClickListener {
                onBackPressed()
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


    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("MMM yy", Locale.getDefault())
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
    }





    private fun showMonthYearPicker(onMonthSelected: (month: Int, year: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Month & Year")
            .setCalendarConstraints(constraints)
            .setSelection(today)
            .build()

        datePicker.show(supportFragmentManager, "MonthYearPicker")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedCalendar = Calendar.getInstance().apply { timeInMillis = selection }
            val selectedMonth = selectedCalendar.get(Calendar.MONTH)
            val selectedYear = selectedCalendar.get(Calendar.YEAR)

            onMonthSelected(selectedMonth, selectedYear)
        }
    }

    private fun getMonthName(month: Int): String {
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().apply {
            set(Calendar.MONTH, month)
        }.time)
    }


    /*fun getAllDatesFromMonth(yearMonth: String): List<DateItem> {
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
    }*/


    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllDatesFromMonth(monthStr: String): List<DateItem> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        val yearMonth = YearMonth.parse(monthStr, formatter)

        val firstOfMonth = yearMonth.atDay(1)
        val lastDay = yearMonth.lengthOfMonth()

        val dayOfWeekOfFirst = firstOfMonth.dayOfWeek.value % 7 // Sunday = 0

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val allDates = mutableListOf<DateItem>()

        // Fill empty cells before the first day
        repeat(dayOfWeekOfFirst) {
            allDates.add(DateItem(date = "", isPresent = "", punchIn = "", punchOut = "", isPlaceholder = true))
        }

        // Add actual month days
        for (day in 1..lastDay) {
            val date = yearMonth.atDay(day).format(dateFormatter)
            allDates.add(DateItem(date = date, isPresent = "", punchIn = "", punchOut = "", isPlaceholder = false))
        }

        return allDates
    }





    fun calculateAverageHours(totalHours: Double, presentDays: Int): Float? {
        if (presentDays == 0) return 0f
        return (totalHours / presentDays).toFloat()
    }


}