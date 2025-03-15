package com.stafo.app.screens.settings

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
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
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                binding.rvShowShiftList.setLayoutManager(layoutManager)
                rvAdapter = ShiftAdapter(it.data, this@AddShiftActivity)
                binding.rvShowShiftList.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()

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

    private fun onClickListener() {
        binding?.apply {

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
                showCustomBottomSheet()
            }
            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showCustomBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.custom_bottom_sheet_add_shift_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)

        edtShiftName = view.findViewById(R.id.edt_shift_name)
        edtShiftStartTime = view.findViewById(R.id.edt_shift_start_time)
        edtShiftEndTime = view.findViewById(R.id.edt_shift_end_time)
        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)

        edtShiftStartTime.setOnClickListener {
            timePickerDialog(edtShiftStartTime, true)
        }
        edtShiftEndTime.setOnClickListener {
            timePickerDialog(edtShiftEndTime, false)
        }


        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btn_add_shift)

        btnSubmit.setOnClickListener {
            if (isValidate()) {

                val startTime = edtShiftStartTime.text.toString()
                val endTime = edtShiftEndTime.text.toString()

                if (!isEndTimeValid(startTime, endTime)) {
                    CustomToast(this, "End time cannot be earlier than start time!")
                    return@setOnClickListener
                } else {
                    val formattedStartTime = convertTo24HourFormat(startTime)
                    val formattedEndTime = convertTo24HourFormat(endTime)


                    val requestBody = ShiftCreateRequest(
                        shift_name = edtShiftName.text.toString(),
                        start_time = formattedStartTime,
                        end_time = formattedEndTime
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

    private fun isEndTimeValid(startTime: String, endTime: String): Boolean {
        if (startTime.isEmpty() || endTime.isEmpty()) return false

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()

        try {
            startCal.time = timeFormat.parse(startTime)!!
            endCal.time = timeFormat.parse(endTime)!!
            return !endCal.before(startCal)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    private fun isValidate(): Boolean {
        binding?.apply {
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

    /*  private fun timePickerDialog(view: AppCompatEditText, isStartTime: Boolean) {
          val cal = Calendar.getInstance()
          val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
              cal.set(Calendar.HOUR_OF_DAY, hour)
              cal.set(Calendar.MINUTE, minute)

              // Convert to 12-hour format with AM/PM
              val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
              view.setText(timeFormat.format(cal.time))



          }

          TimePickerDialog(
              view.context, // Use view's context
              timeSetListener,
              cal.get(Calendar.HOUR_OF_DAY),
              cal.get(Calendar.MINUTE),
              false // Keeps the picker in 24-hour mode
          ).show()
      }*/


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


}