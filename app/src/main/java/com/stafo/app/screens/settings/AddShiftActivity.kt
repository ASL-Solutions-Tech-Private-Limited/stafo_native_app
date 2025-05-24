package com.stafo.app.screens.settings

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.ShiftAdapter
import com.stafo.app.databinding.ActivityAddShiftBinding
import com.stafo.app.screens.settings.dataClass.ShiftCreateRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.screens.settings.dataClass.ShiftDataList
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddShiftActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddShiftBinding


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: ShiftAdapter

    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var edtShiftName: AppCompatEditText
    private lateinit var edtShiftStartTime: AppCompatEditText
    private lateinit var edtShiftEndTime: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddShiftBinding.inflate(layoutInflater)
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


    override fun onResume() {
        super.onResume()
        getEmployeeComId()?.let {
            settingsViewModel.getShiftList(
                this@AddShiftActivity,
                it
            )
        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        getEmployeeComId()?.let { settingsViewModel.getShiftList(this, it) }

        settingsViewModel.mShiftListResponse.observe(this) {

            if (it.success) {

                if (!it.data.isNullOrEmpty()) {

                    val formattedList = it.data.map { shift ->
                        val start = shift.start_time
                        val end = shift.end_time

                        val formattedStart = if (!start.isNullOrEmpty() && is24HourFormat(start)) {
                            convertTo12HourFormat(start)
                        } else start

                        val formattedEnd = if (!end.isNullOrEmpty() && is24HourFormat(end)) {
                            convertTo12HourFormat(end)
                        } else end

                        ShiftDataList(
                            id = shift.id,
                            shift_name = shift.shift_name,
                            start_time = formattedStart,
                            end_time = formattedEnd,
                            created_at = shift.created_at,
                            updated_at = shift.updated_at,
                            sunday = shift.sunday,
                            monday = shift.monday,
                            tuesday = shift.tuesday,
                            wednesday = shift.wednesday,
                            thursday = shift.thursday,
                            friday = shift.friday,
                            saturday = shift.saturday
                        )
                    }

                    if (::rvAdapter.isInitialized) {
                        rvAdapter.updateList(formattedList)
                    } else {
                        rvAdapter = ShiftAdapter(formattedList, this@AddShiftActivity)
                        binding.rvShowShiftList.layoutManager = LinearLayoutManager(this)
                        binding.rvShowShiftList.adapter = rvAdapter
                    }
                }


            } else {
                CustomToast(this, it.message)
            }
        }
        settingsViewModel.mDeleteResponse.observe(this) {

            if (it.status) {
                getEmployeeComId()?.let { empId ->
                    settingsViewModel.getShiftList(this@AddShiftActivity, empId)
                }
                CustomToast(this, it.message)

            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mUpdateShiftResponse.observe(this) {

            if (it.success) {
                getEmployeeComId()?.let { empId ->
                    settingsViewModel.getShiftList(this@AddShiftActivity, empId)
                }
                CustomToast(this, it.message)
                bottomSheetDialog.dismiss()
            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mShiftCreateResponse.observe(this) {

            if (it.success) {
                CustomToast(this, it.message)
                bottomSheetDialog.dismiss()
                getEmployeeComId()?.let {
                    settingsViewModel.getShiftList(
                        this@AddShiftActivity,
                        it
                    )
                }
            } else {
                CustomToast(this, it.message)
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

    fun is24HourFormat(time: String): Boolean {
        return try {
            val format24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            format24.isLenient = false
            format24.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun convertTo12HourFormat(time24: String): String {
        return try {
            val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = sdf24.parse(time24)
            sdf12.format(date!!)
        } catch (e: Exception) {
            time24
        }
    }


    private fun onClickListener() {
        binding.apply {

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                getEmployeeComId()?.let {
                    settingsViewModel.getShiftList(
                        this@AddShiftActivity,
                        it
                    )
                }

            }



            llcAddShift.setOnClickListener {
                var dataClass = ShiftDataList(
                    id = 0,
                    shift_name = "shift.shift_name",
                    start_time = "formattedStart",
                    end_time = "formattedEnd",
                    created_at = "shift.created_at",
                    updated_at = "",
                    sunday = 0,
                    monday = 0,
                    tuesday = 0,
                    wednesday = 0,
                    thursday = 0,
                    friday = 0,
                    saturday = 0
                )
                showCustomBottomSheet("Add", dataClass)
            }
            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }
    }

    @SuppressLint("MissingInflatedId")
    fun showCustomBottomSheet(type: String, editShift: ShiftDataList) {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.custom_bottom_sheet_add_shift_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)

        val titleBottom = view.findViewById<TextView>(R.id.text_view)
        edtShiftName = view.findViewById(R.id.edt_shift_name)
        edtShiftStartTime = view.findViewById(R.id.edt_shift_start_time)
        edtShiftEndTime = view.findViewById(R.id.edt_shift_end_time)
        val cbSunday = view.findViewById<CheckBox>(R.id.cb_sunday)
        val cbMonday = view.findViewById<CheckBox>(R.id.cb_monday)
        val cbTuesday = view.findViewById<CheckBox>(R.id.cb_tuesday)
        val cbWednesday = view.findViewById<CheckBox>(R.id.cb_wednesday)
        val cbThursday = view.findViewById<CheckBox>(R.id.cb_thursday)
        val cbFriday = view.findViewById<CheckBox>(R.id.cb_friday)
        val cbSaturday = view.findViewById<CheckBox>(R.id.cb_saturday)
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btn_add_shift)

        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)

        edtShiftStartTime.setOnClickListener {
            timePickerDialog(edtShiftStartTime, true)
        }
        edtShiftEndTime.setOnClickListener {
            timePickerDialog(edtShiftEndTime, false)
        }

        if (type == "Edit") {

            titleBottom.text = "Edit Shift"
            btnSubmit.text="Submit"
            edtShiftName.setText(editShift.shift_name)
            edtShiftStartTime.setText(editShift.start_time)
            edtShiftEndTime.setText(editShift.end_time)

            cbSunday.isChecked = editShift.sunday == 1
            cbMonday.isChecked = editShift.monday == 1
            cbTuesday.isChecked = editShift.tuesday == 1
            cbWednesday.isChecked = editShift.wednesday == 1
            cbThursday.isChecked = editShift.thursday == 1
            cbFriday.isChecked = editShift.friday == 1
            cbSaturday.isChecked = editShift.saturday == 1
        }


        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        btnSubmit.setOnClickListener {


            if (isValidate()) {
                val startTime = edtShiftStartTime.text.toString()
                val endTime = edtShiftEndTime.text.toString()

                val formattedStartTime = convertTo24HourFormat(startTime)
                val formattedEndTime = convertTo24HourFormat(endTime)

                if (type == "Edit") {

                    val requestBody = ShiftCreateRequest(
                        shift_name = edtShiftName.text.toString(),
                        start_time = formattedStartTime,
                        end_time = formattedEndTime,
                        sunday = cbSunday.isChecked,
                        monday = cbMonday.isChecked,
                        tuesday = cbTuesday.isChecked,
                        wednesday = cbWednesday.isChecked,
                        thursday = cbThursday.isChecked,
                        friday = cbFriday.isChecked,
                        saturday = cbSaturday.isChecked
                    )
                    settingsViewModel.updateShift(this, editShift.id, requestBody)

                } else {
                    val requestBody = ShiftCreateRequest(
                        shift_name = edtShiftName.text.toString(),
                        start_time = formattedStartTime,
                        end_time = formattedEndTime,
                        sunday = cbSunday.isChecked,
                        monday = cbMonday.isChecked,
                        tuesday = cbTuesday.isChecked,
                        wednesday = cbWednesday.isChecked,
                        thursday = cbThursday.isChecked,
                        friday = cbFriday.isChecked,
                        saturday = cbSaturday.isChecked
                    )
                    settingsViewModel.createNewShift(this, requestBody)
                }


            }
        }

        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()


    }


    private fun convertTo24HourFormat(time: String): String {
        return try {
            val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val date = inputFormat.parse(time)!!
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            time
        }
    }


    private fun isValidate(): Boolean {
        binding.apply {
            if (edtShiftName.text.isNullOrEmpty()) {
                edtShiftName.error = "Please enter shift name"
                edtShiftName.requestFocus()
                return false
            } else if (edtShiftStartTime.text.isNullOrEmpty()) {
                edtShiftStartTime.error = "Please enter shift start time"
                edtShiftStartTime.requestFocus()
                return false
            } else if (edtShiftEndTime.text.isNullOrEmpty()) {
                edtShiftEndTime.error = "Please enter shift end time"
                edtShiftEndTime.requestFocus()
                return false
            }
        }
        return true
    }


    private fun timePickerDialog(view: AppCompatEditText, isStartTime: Boolean) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val selectedTime = timeFormat.format(cal.time)

            if (isStartTime) {
                view.setText(selectedTime)
            } else {
                val startTimeText = edtShiftStartTime.text.toString()
                if (startTimeText.isNotEmpty()) {
                    val startCal = Calendar.getInstance()
                    startCal.time = timeFormat.parse(startTimeText)!!

                    if (cal.before(startCal)) {
                        CustomToast(this, "End time cannot be earlier than start time!")
                        return@OnTimeSetListener
                    }
                }
                view.setText(selectedTime)
            }
        }

        TimePickerDialog(
            view.context,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }


    fun deleteShift(shiftId: Int) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? Delete this.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            settingsViewModel.companyDeleteShift(this, shiftId)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()


    }


}