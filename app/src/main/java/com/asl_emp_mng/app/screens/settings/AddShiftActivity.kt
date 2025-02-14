package com.asl_emp_mng.app.screens.settings

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
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
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.EmpItemAdapter
import com.asl_emp_mng.app.base.adapter.ShiftAdapter
import com.asl_emp_mng.app.base.model.CompanyInfo
import com.asl_emp_mng.app.base.model.Employee
import com.asl_emp_mng.app.databinding.ActivityAddShiftBinding
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftCreateRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftDataList
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Random


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
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)

        onClickListener()

        observeViewModel()

    }




    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.getShiftList(this)

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
                settingsViewModel.getShiftList(this@AddShiftActivity)

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
            timePickerDialog(edtShiftStartTime)
        }
        edtShiftEndTime.setOnClickListener {
            timePickerDialog(edtShiftEndTime)
        }


        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btn_add_shift)

        btnSubmit.setOnClickListener {
            if (isValidate()) {
                val requestBody = ShiftCreateRequest(
                    shift_name = edtShiftName.text.toString(),
                    start_time = edtShiftStartTime.text.toString(),
                    end_time = edtShiftEndTime.text.toString()
                )
                settingsViewModel.createNewShift(this, requestBody)
            }
        }

        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()


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

    private fun timePickerDialog(view: AppCompatEditText) {

        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            view.setText(timeFormat.format(cal.time))
        }
        TimePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()

    }

}