package com.stafo.app.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.EmpListAdapter
import com.stafo.app.base.adapter.RadioShiftAdapter
import com.stafo.app.databinding.ActivityViewAllEmployeeBinding
import com.stafo.app.screens.settings.dataClass.AssignShiftRequest
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.SetAttendanceTypeRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.screens.settings.dataClass.InActiveEmpRequest
import com.stafo.app.screens.settings.dataClass.RemoveSelfieRequest
import com.stafo.app.screens.settings.dataClass.ShiftDataList
import java.util.Calendar

class ViewAllEmployeeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllEmployeeBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: EmpListAdapter
    private lateinit var shiftID: String
    private var mFrom = "View All"

    private var empList: List<GetEmployee> = listOf()
    private var filteredList: List<GetEmployee> = listOf()
    private var shiftList: List<ShiftDataList> = listOf()

    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var shiftBottomSheetDialog: BottomSheetDialog
    private lateinit var rvRadioShift: RecyclerView
    private lateinit var rvRadioShiftAdapter: RadioShiftAdapter
    private val calendar = Calendar.getInstance()

    private var attendanceLocation: String = "from office"

    private var isSelfie: String = "false"
    private var isQR: String = "false"
    private var isGeo: String = "false"
    private var isAllow: String = "false"
    private var attendanceType: String = "false"


    var selectedShiftIds: List<String> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewAllEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        mFrom = intent.getStringExtra("FROM").toString()

        setOnClickEvents()
        observeViewModel()
        setupSearchListener()
    }


    override fun onResume() {
        super.onResume()
        settingsViewModel.getAllEmployeeList(this@ViewAllEmployeeActivity)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) {

            if (it.status) {


                if (it.data.isNotEmpty()) {

                    binding.etDirSearch.isFocusable = true
                    binding.etDirSearch.isFocusableInTouchMode = true

                    empList = it.data
                    filteredList = empList

                    val layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvViewEmpList.setLayoutManager(layoutManager)
                    rvAdapter =
                        EmpListAdapter(empList, this, mFrom, object : EmpListAdapter.onGeoClick {
                            override fun onEMPClick(empID: String, type: String) {
                                if (type == "Request Location") {
                                    settingsViewModel.sendGeoLocationRequest(
                                        this@ViewAllEmployeeActivity,
                                        empID, "0"
                                    )
                                }
                            }

                        })
                    binding.rvViewEmpList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()


                } else {
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE
                }


            } else {
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
            }
        }


        settingsViewModel.mShiftListResponse.observe(this) {

            if (it.success) {

                if (it.data.isNotEmpty()) {
                    shiftList = it.data
                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    rvRadioShift.setLayoutManager(layoutManager)
                    rvRadioShiftAdapter = RadioShiftAdapter(
                        shiftList,
                        this,
                        object : RadioShiftAdapter.ActionClickListener {
                            override fun onActionClick(selectedShifts: List<String>) {

                                selectedShiftIds = selectedShifts
                            }
                        })
                    rvRadioShift.adapter = rvRadioShiftAdapter
                    rvAdapter.notifyDataSetChanged()
                } else {
                    CustomToast(this, "No shifts available.Please add shifts first!")
                    shiftBottomSheetDialog.dismiss()
                }


            } else {
                CustomToast(this, it.message)
                shiftBottomSheetDialog.dismiss()
            }
        }


        settingsViewModel.mInActiveEmpResponse.observe(this) {

            if (it.status) {

                settingsViewModel.getAllEmployeeList(this@ViewAllEmployeeActivity)
                CustomToast(this, it.message)

            } else {
                CustomToast(this, it.message)
            }
        }


        settingsViewModel.mRemoveSelfieResponse.observe(this) {

            if (it.status) {

                settingsViewModel.getAllEmployeeList(this@ViewAllEmployeeActivity)
                CustomToast(this, it.message)

            } else {
                CustomToast(this, it.message)
            }
        }



        settingsViewModel.mShiftAssignmentResponse.observe(this) {

            if (it.success) {
                CustomToast(this, it.message)
                shiftBottomSheetDialog.dismiss()
                settingsViewModel.getAllEmployeeList(this@ViewAllEmployeeActivity)

            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mSendGeoLocationResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mSetAttendanceTypeResponse.observe(this) {
            if (it.status) {
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


    fun showActiveAlert(id: String, status: String) {
        val builder = AlertDialog.Builder(this@ViewAllEmployeeActivity)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? You want to change status this employee!")

        builder.setPositiveButton("Yes") { dialog, _ ->

            val request = InActiveEmpRequest(
                id = id,
                status = status
            )

            settingsViewModel.postActiveInactiveEmp(this@ViewAllEmployeeActivity, request)

            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    fun showRemoveAlert(id: String) {
        val builder = AlertDialog.Builder(this@ViewAllEmployeeActivity)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? You want to remove selfie image of this employee!")

        builder.setPositiveButton("Yes") { dialog, _ ->

            val request = RemoveSelfieRequest(
                employee_id = id
            )

            settingsViewModel.postRemoveSelfie(this@ViewAllEmployeeActivity, request)

            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
    private fun setOnClickEvents() {


        settingsViewModel.getAllEmployeeList(this@ViewAllEmployeeActivity)


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            settingsViewModel.getAllEmployeeList(this@ViewAllEmployeeActivity)

        }

        binding.imageBack.setOnClickListener {
           onBackPressed()
        }


    }


    @SuppressLint("MissingInflatedId")
    fun showCustomBottomSheet(id: Int, getAttendanceType: String?) {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.attendance_mode_bottom_sheet_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)

        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)
        val llFromOffice = view.findViewById<LinearLayout>(R.id.ll_from_office)
        val llFromAny = view.findViewById<LinearLayout>(R.id.ll_from_any)
        val imgOffice = view.findViewById<ImageView>(R.id.img_office)
        val imgAny = view.findViewById<ImageView>(R.id.img_any)
        val switchAllow = view.findViewById<SwitchCompat>(R.id.switch_allow)
        val switchSelfie = view.findViewById<SwitchCompat>(R.id.switch_selfie)
        val switchQr = view.findViewById<SwitchCompat>(R.id.switch_qr)
        val switchGeo = view.findViewById<SwitchCompat>(R.id.switch_geo)

        val btnSetAttendanceType = view.findViewById<AppCompatButton>(R.id.btn_setAttendance_type)


        when (getAttendanceType) {
            "geo" -> {
                switchGeo.isChecked = true
                attendanceType = "geo"
            }

            "qr code" -> {
                switchQr.isChecked = true
                attendanceType = "qr code"
            }

            "selfie" -> {
                switchSelfie.isChecked = true
                attendanceType = "selfie"
            }
        }


        switchAllow.setOnCheckedChangeListener { _, isChecked ->
            attendanceType = if (isChecked) "true" else "false"
        }


        switchGeo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                attendanceType = "geo"
                switchQr.isChecked = false
                switchSelfie.isChecked = false
            }
        }

        switchQr.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                attendanceType = "qr code"
                switchGeo.isChecked = false
                switchSelfie.isChecked = false
            }
        }

        switchSelfie.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                attendanceType = "selfie"
                switchGeo.isChecked = false
                switchQr.isChecked = false
            }
        }



        llFromOffice.setOnClickListener {

            attendanceLocation = "from office"
            llFromOffice.setBackgroundResource(R.drawable.custom_switch_card_bg)
            llFromAny.setBackgroundResource(R.drawable.custom_switch_card_bg2)

            imgOffice.setImageResource(R.drawable.ic_lv_active_radio)
            imgAny.setImageResource(R.drawable.ic_lv_inactive_radio)

        }

        llFromAny.setOnClickListener {
            attendanceLocation = "from anywhere"
            llFromOffice.setBackgroundResource(R.drawable.custom_switch_card_bg2)
            llFromAny.setBackgroundResource(R.drawable.custom_switch_card_bg)

            imgOffice.setImageResource(R.drawable.ic_lv_inactive_radio)
            imgAny.setImageResource(R.drawable.ic_lv_active_radio)
        }

        btnSetAttendanceType.setOnClickListener {

            if (attendanceType == "false") {
                CustomToast(this, "Please select attendance type!")
                Log.d("res", "get val $isGeo $isSelfie $isAllow $isQR $attendanceLocation")
            } else {
                Log.d("res", "get val $isGeo $isSelfie $isAllow $isQR $attendanceLocation")
                val request = SetAttendanceTypeRequest(
                    employee_id = id,
                    attendance_type = attendanceType
                )
                Log.d("res", "post: $request")
                settingsViewModel.setAttendanceTypeEmployee(this, request)
            }


        }



        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }





        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()


    }

    @SuppressLint("MissingInflatedId")
    fun showShiftCustomBottomSheet(id: String) {
        shiftBottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.shift_time_bottom_sheet_layout, null)

        shiftBottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        shiftBottomSheetDialog.setCancelable(false)

        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)
        rvRadioShift = view.findViewById(R.id.rv_radio_shift)
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btn_submit)
        val cbSelectAll = view.findViewById<CheckBox>(R.id.cbSelectAll)

        cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            rvRadioShiftAdapter.setMultiSelectionEnabled(isChecked)
        }



        btnSubmit.setOnClickListener {


            if (selectedShiftIds.isNotEmpty()) {

                settingsViewModel.assignShift(this, id, selectedShiftIds)

            } else {
                CustomToast(this, "Please select shift!")
            }

        }



        getEmployeeComId()?.let { settingsViewModel.getShiftList(this, it) }




        btnCancel.setOnClickListener {
            shiftID = ""
            shiftBottomSheetDialog.dismiss()
        }


        shiftBottomSheetDialog.setContentView(view)


        shiftBottomSheetDialog.show()


    }


    private fun setupSearchListener() {
        binding.etDirSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            empList
        } else {
            empList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true) ||
                        it.branch_name.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }
}