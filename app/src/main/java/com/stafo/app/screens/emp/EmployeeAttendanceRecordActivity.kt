package com.stafo.app.screens.emp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterEmployeeRecord
import com.stafo.app.base.model.DateItem
import com.stafo.app.databinding.ActivityEmployeeAttendanceRecordBinding
import com.stafo.app.screens.settings.AttendanceRequestActivity
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.calculateMinutes
import com.stafo.app.utils.convertTo12HourFormat3
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import com.stafo.app.utils.isWeekOff
import com.stafo.app.utils.prepareHolidaySet
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
    // private var mEmployeeDetails: EmployeeDataList? = null

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
        /*mEmployeeDetails = intent.getStringExtra("EmployeeDetails").let {
            Gson().fromJson(it, EmployeeDataList::class.java)
        }*/

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
        settingsViewModel.mAttendanceHistoryResponse.observe(this) { response ->

            if (response.status && response.data != null) {

                binding.txtMsg.visibility = View.GONE

                val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                // ✅ Maps for fast lookup
                val attendanceMap = response.data.associateBy { it.date }
                val holidaySet = prepareHolidaySet(response.holidays!!) // pass your holiday API list

                val mMonth = getAllDatesFromMonth(mSelectedDate)

                var mPresentCount = 0
                var mTotalWorkingMinutes = 0

                for (i in mMonth.indices) {

                    val item = mMonth[i]

                    // ✅ Skip placeholders or invalid dates
                    if (item.isPlaceholder || item.date.isNullOrBlank()) {
                        continue
                    }

                    val dateStr = item.date

                    val localDate = try {
                        LocalDate.parse(dateStr)
                    } catch (e: Exception) {
                        continue // extra safety
                    }

                    val attendance = attendanceMap[dateStr]

                    when {

                        // 🎉 HOLIDAY (Highest Priority)
                        holidaySet.contains(dateStr) -> {
                            item.isPresent = "Holiday"
                        }

                        // 🛌 WEEK OFF
                        isWeekOff(localDate, response.shifts?.getOrNull(0)) -> {
                            item.isPresent = "Week Off"
                        }

                        // ✅ PRESENT / API DATA
                        attendance != null -> {

                            item.isPresent = attendance.attendance
                            item.punchIn = attendance.in_time ?: ""
                            item.punchOut = attendance.out_time ?: ""

                            if (attendance.attendance == "Present") {
                                mPresentCount++
                            }

                            if (!attendance.in_time.isNullOrEmpty() &&
                                !attendance.out_time.isNullOrEmpty()
                            ) {
                                mTotalWorkingMinutes += calculateMinutes(
                                    attendance.in_time,
                                    attendance.out_time
                                ).toInt()
                            }
                        }

                        // ❌ ABSENT
                        else -> {
                            item.isPresent = "Absent"
                        }
                    }
                }

                // ✅ TODAY TIME LOGIC
                val todayAttendance = attendanceMap[today]

                val timeToShow = when {
                    todayAttendance?.out_time?.isNotBlank() == true ->
                        convertTo12HourFormat3(todayAttendance.out_time)

                    todayAttendance?.in_time?.isNotBlank() == true ->
                        convertTo12HourFormat3(todayAttendance.in_time)

                    else -> "--"
                }

                binding.tvTodayTime.text = timeToShow

                // ✅ TOTAL TIME
                val totalHours = mTotalWorkingMinutes / 60
                val totalMinutes = mTotalWorkingMinutes % 60
                val totalWorkingTime = String.format("%02d:%02d", totalHours, totalMinutes)

                binding.txtTotalPresent.text = mPresentCount.toString()
                binding.txtTotalWorking.text = totalWorkingTime

                // ✅ AVERAGE + PROGRESS
                avgWork = (if (mPresentCount > 0) {
                    mTotalWorkingMinutes / 60.0 / mPresentCount
                } else 0.0)?.toFloat()

                avgWork?.let {
                    val progress = ((it / 9.0) * 100).toFloat()
                    binding.cpb.updateProgress(progress.coerceIn(0f, 100f))
                }

                // ✅ RecyclerView Setup
                val adapter = binding.rvEmpAttendList.adapter as? AdapterEmployeeRecord

                if (adapter != null) {
                    adapter.submitList(mMonth)
                } else {

                    binding.rvEmpAttendList.layoutManager = GridLayoutManager(this, 7)

                    val newAdapter = AdapterEmployeeRecord(this, mEMPID)
                    binding.rvEmpAttendList.adapter = newAdapter

                    newAdapter.submitList(mMonth) {

                        Handler(Looper.getMainLooper()).postDelayed({

                            val currentDatePosition = newAdapter.getCurrentDatePosition()

                            if (currentDatePosition != -1) {
                                binding.rvEmpAttendList.smoothScrollToPosition(currentDatePosition)
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



            if (getIsCOMPANYLogin(this@EmployeeAttendanceRecordActivity)) {
                imageSettings.visibility = View.GONE
            } else imageSettings.visibility = View.VISIBLE


            imageSettings.setOnClickListener { view ->
                showPopupMenu(view)
            }

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                fetchAttendanceData()

            }


        }
    }


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val menu = popupMenu.menu
        val options = resources.getStringArray(R.array.request_attendance_com)
        options.forEachIndexed { index, option ->
            menu.add(0, index, index, option)
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                0 -> {
                    startActivity(Intent(this, EditAttendanceActivity::class.java))
                    true
                }

                1 -> {
                    startActivity(Intent(this, AttendanceRequestActivity::class.java))
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
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
            allDates.add(
                DateItem(
                    date = "",
                    isPresent = "",
                    punchIn = "",
                    punchOut = "",
                    isPlaceholder = true
                )
            )
        }

        // Add actual month days
        for (day in 1..lastDay) {
            val date = yearMonth.atDay(day).format(dateFormatter)
            allDates.add(
                DateItem(
                    date = date,
                    isPresent = "",
                    punchIn = "",
                    punchOut = "",
                    isPlaceholder = false
                )
            )
        }

        return allDates
    }


    fun calculateAverageHours(totalHours: Double, presentDays: Int): Float? {
        if (presentDays == 0) return 0f
        return (totalHours / presentDays).toFloat()
    }


}