package com.stafo.app.screens.emp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityEditAttendanceBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.AttendanceUpdateRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.showFormatDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditAttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAttendanceBinding

    private var empId: String = ""
    private var postItemId: String = ""
    private var empName: String = ""
    private var selectedDate: String = ""
    private var selectedInTime: String = ""
    private var selectedOutTime: String = ""
    private var selectedAttendanceType: String = ""
    private var attendanceDayValue: Int = -1

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        // employee name & item Id
        postItemId = intent.getStringExtra("EMPID") ?: ""
        selectedDate = intent.getStringExtra("mDate") ?: ""
        empName = intent.getStringExtra("EMPNAME") ?: ""
        Log.e("attendance", "get : $postItemId")
        setOnClickListeners()
        observeViewModel()
    }

    private fun setOnClickListeners() {
        binding.apply {
            settingsViewModel.getAllEmployeeList(this@EditAttendanceActivity)

            if (!empName.isNullOrBlank()) {
                binding.tieEmpName.visibility = View.VISIBLE
                binding.tieEmpName.setText(empName)
                binding.tieDate.setText(showFormatDate(selectedDate))
                tvTopTitle.text = "Edit Attendance"
            } else {
                binding.tieEmpName.visibility = View.GONE
                tvTopTitle.text = "Request Attendance"
            }

            val options = resources.getStringArray(R.array.attendance_type)

            val adapterSpinner = object : ArrayAdapter<String>(
                this@EditAttendanceActivity, R.layout.custom_spinner_item, options
            ) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int, convertView: View?, parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(R.id.tv_item)
                    textView.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                    return view
                }

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(R.id.tv_item)
                    textView.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                    return view
                }
            }

            binding.spinnerAttendanceType.adapter = adapterSpinner
            binding.spinnerAttendanceType.setSelection(0)

            binding.spinnerAttendanceType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View?, position: Int, id: Long
                    ) {
                        selectedAttendanceType = if (position != 0) {
                            parent.getItemAtPosition(position).toString()
                        } else {
                            ""
                        }

                        if (selectedAttendanceType == "Present") llcLeave.visibility =
                            View.VISIBLE else llcLeave.visibility = View.GONE
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }


            binding.rgLeav.setOnCheckedChangeListener { _, checkedId ->
                attendanceDayValue = when (checkedId) {
                    R.id.rd_full_day -> 0
                    R.id.rd_half_day -> 1
                    else -> -1
                }
            }

            tieDate.setOnClickListener {
                showDatePicker()
            }

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            edtInTime.setOnClickListener {
                showTimePicker(edtInTime)
            }

            edtOutTime.setOnClickListener {
                showTimePicker(edtOutTime)
            }

            btnUpdateAttendance.setOnClickListener {
                if (selectedAttendanceType.isBlank()) {
                    CustomToast(this@EditAttendanceActivity, "Please select attendance type")
                    return@setOnClickListener
                }

                if (selectedDate.isBlank()) {
                    CustomToast(this@EditAttendanceActivity, "Please select date")
                    return@setOnClickListener
                }


                if (selectedAttendanceType == "Present") {
                    if (selectedInTime.isBlank()) {
                        CustomToast(this@EditAttendanceActivity, "Please select In Time")
                        return@setOnClickListener
                    }

                    if (selectedOutTime.isBlank()) {
                        CustomToast(this@EditAttendanceActivity, "Please select Out Time")
                        return@setOnClickListener
                    }

                    if (attendanceDayValue == -1) {
                        CustomToast(this@EditAttendanceActivity, "Please select Full/Half Day")
                        return@setOnClickListener
                    }


                    if (!empName.isNullOrBlank()) {
                        val request = AttendanceUpdateRequest(
                            employee_id = empId.toInt(),
                            attendance = selectedAttendanceType,
                            date = selectedDate,
                            in_time = selectedInTime,
                            out_time = selectedOutTime,
                            halfday = attendanceDayValue
                        )

                        settingsViewModel.updateAttendance(
                            this@EditAttendanceActivity, postItemId.toInt(), request
                        )
                    }else{

                        getEmployeeDetails()?.let { it1 ->
                            val request = AttendanceUpdateRequest(
                                employee_id = it1.id,
                                company_id = it1.company_id,
                                branch_id = it1.branch_id?.toIntOrNull() ?: 0,
                                department_id = it1.department_id?.toIntOrNull() ?: 0,
                                attendance = selectedAttendanceType,
                                date = selectedDate,
                                in_time = selectedInTime,
                                out_time = selectedOutTime,
                                halfday = attendanceDayValue
                            )

                            settingsViewModel.attendanceRequest(
                                this@EditAttendanceActivity, request
                            )

                        }
                    }







                } else {

                    if (!empName.isNullOrBlank()) {
                        val request = AttendanceUpdateRequest(
                            employee_id = empId.toInt(),
                            attendance = selectedAttendanceType,
                            date = selectedDate,
                            in_time = selectedInTime,
                            out_time = selectedOutTime,
                            halfday = attendanceDayValue
                        )

                        settingsViewModel.updateAttendance(
                            this@EditAttendanceActivity, postItemId.toInt(), request
                        )
                    } else {
                        getEmployeeDetails()?.let { it1 ->
                            val request = AttendanceUpdateRequest(
                                employee_id = it1.id,
                                company_id = it1.company_id,
                                branch_id = it1.branch_id?.toIntOrNull() ?: 0,
                                department_id = it1.department_id?.toIntOrNull() ?: 0,
                                attendance = selectedAttendanceType,
                                date = selectedDate,
                                in_time = selectedInTime,
                                out_time = selectedOutTime,
                                halfday = attendanceDayValue
                            )

                            settingsViewModel.attendanceRequest(
                                this@EditAttendanceActivity, request
                            )

                        }


                    }
                }


            }


        }
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }


        settingsViewModel.mGetAllEmployeeResponse.observe(this) {

            if (it.status) {


                if (it.data.isNotEmpty()) {
                    val matchedEmployee = it.data.find { employee ->
                        employee.name.equals(empName, ignoreCase = true)
                    }

                    if (matchedEmployee != null) {
                        val matchedEmpId = matchedEmployee.id

                        Log.e("attendance", "${matchedEmployee}")

                        empId = matchedEmpId.toString()

                    } else {
                        empId = ""
                    }

                }


            } else {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
        }

        settingsViewModel.mAttendanceUpdateResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else CustomToast(this, it.message)

        }
        settingsViewModel.mAttendanceRequestResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
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


    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val postFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val displayDate = displayFormat.format(selectedCal.time)
                val postDate = postFormat.format(selectedCal.time)
                binding.tieDate.setText(displayDate)
                selectedDate = postDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(targetEditText: AppCompatEditText) {
        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, { _, selectedHour, selectedMinute ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }

                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = timeFormat.format(selectedCal.time)
                // Set values
                targetEditText.setText(formattedTime)

                // Store values
                if (targetEditText.id == binding.edtInTime.id) {
                    selectedInTime =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCal.time)
                } else if (targetEditText.id == binding.edtOutTime.id) {
                    selectedOutTime =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCal.time)
                }
            }, hour, minute, false
        )

        timePickerDialog.show()
    }

}