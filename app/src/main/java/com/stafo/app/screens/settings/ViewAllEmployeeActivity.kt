package com.stafo.app.screens.settings

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CheckBox
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
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.SetAttendanceTypeRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.screens.settings.dataClass.InActiveEmpRequest
import com.stafo.app.screens.settings.dataClass.RemoveSelfieRequest
import com.stafo.app.screens.settings.dataClass.Shift
import com.stafo.app.screens.settings.dataClass.ShiftDataList
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    private lateinit var cbSelectAll: CheckBox
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

                    var mAssignShift = emptyList<Shift>()
                    if (::rvAdapter.isInitialized) {
                        mAssignShift = rvAdapter.getAssignShift()
                    }
                    shiftList = formattedList

                    val allMatched = shiftList.all { shift ->
                        mAssignShift.any { it.id.toString() == shift.id.toString() }
                    }
                    cbSelectAll.isChecked = allMatched


                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    rvRadioShift.setLayoutManager(layoutManager)
                    rvRadioShiftAdapter = RadioShiftAdapter(
                        shiftList,
                        this,
                        object : RadioShiftAdapter.ActionClickListener {
                            override fun onActionClick(selectedShifts: List<String>) {

                                selectedShiftIds = selectedShifts
                            }
                        },mAssignShift)
                    rvRadioShift.adapter = rvRadioShiftAdapter
                    rvAdapter.notifyDataSetChanged()
                } else {
                    binding.blurOverlay.visibility = View.GONE
                    CustomToast(this, "No shifts available.Please add shifts first!")
                    shiftBottomSheetDialog.dismiss()
                }


            } else {
                binding.blurOverlay.visibility = View.GONE
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
                binding.blurOverlay.visibility = View.GONE
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
                binding.blurOverlay.visibility = View.GONE
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

    private fun is24HourFormat(time: String): Boolean {
        return try {
            val format24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            format24.isLenient = false
            format24.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun convertTo12HourFormat(time24: String): String {
        return try {
            val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = sdf24.parse(time24)
            sdf12.format(date!!)
        } catch (e: Exception) {
            time24
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
        applyGlassyOverlay(binding.blurOverlay)
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.attendance_mode_bottom_sheet_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)

        }

        bottomSheetDialog.setCancelable(false)

        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)
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
            binding.blurOverlay.visibility = View.GONE
            bottomSheetDialog.dismiss()
        }





        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()



    }

    @SuppressLint("MissingInflatedId")
    fun showShiftCustomBottomSheet(id: String) {

        applyGlassyOverlay(binding.blurOverlay)
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
         cbSelectAll = view.findViewById(R.id.cbSelectAll)

        cbSelectAll.setOnCheckedChangeListener { _, isChecked ->

            if (::rvRadioShiftAdapter.isInitialized){
                rvRadioShiftAdapter.setMultiSelectionEnabled(isChecked)
            }
            //rvRadioShiftAdapter.setMultiSelectionEnabled(isChecked)
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
            binding.blurOverlay.visibility = View.GONE
            shiftID = ""
            shiftBottomSheetDialog.dismiss()
        }


        shiftBottomSheetDialog.setContentView(view)
        shiftBottomSheetDialog.show()


    }

    @SuppressLint("NewApi")
    private fun applyGlassyOverlay(blurView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect = RenderEffect.createBlurEffect(
                30f, 30f, Shader.TileMode.CLAMP
            )
            blurView.setRenderEffect(blurEffect)
        } else {
            blurView.setBackgroundColor(Color.parseColor("#99FFFFFF"))
        }

        blurView.visibility = View.VISIBLE
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
                        it.phone.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }
}