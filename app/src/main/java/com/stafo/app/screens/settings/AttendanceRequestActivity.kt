package com.stafo.app.screens.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ActivityAttendanceRequestBinding
import com.stafo.app.screens.emp.EditAttendanceActivity
import com.stafo.app.screens.settings.adapter.AdapterAttendanceRequest
import com.stafo.app.screens.settings.dataClass.AttendanceActionRequest
import com.stafo.app.screens.settings.dataClass.AttendanceRequestData
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin

class AttendanceRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceRequestBinding
    private lateinit var rvAdapter: AdapterAttendanceRequest
    private var list: List<AttendanceRequestData> = listOf()
    private var filteredList: List<AttendanceRequestData> = listOf()

    private var userType: String = ""

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAttendanceRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()
        if (getIsCOMPANYLogin(this)) {
            binding.llSearchHead.visibility = View.VISIBLE
            binding.btnRequestAttendance.visibility = View.GONE
            setupSearchListener()
        } else {
            binding.llSearchHead.visibility = View.GONE
            binding.btnRequestAttendance.visibility = View.VISIBLE
            binding.btnRequestAttendance.setOnClickListener {
                startActivity(Intent(this, EditAttendanceActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadRequestData()
    }

    private fun loadRequestData() {
        if (getIsCOMPANYLogin(this)) {
            getEmployeeComId()?.let {
                settingsViewModel.attendanceRequestList(this, it, false)
            }
        } else {
            userType = "emp"
            getEmployeeDetails()?.let { details ->
                settingsViewModel.attendanceRequestList(this, details.id.toString(), true)
            }
        }
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mAttendanceRequestListResponse.observe(this) {

            if (it.status) {
                if (it.data.isNotEmpty()) {

                    binding.txtMsg.visibility = View.GONE
                    binding.rvLeaveList.visibility = View.VISIBLE
                    list = it.data
                    filteredList = list

                    if (list.isNotEmpty()) {
                        binding.etDirSearch.isFocusable = true
                        binding.etDirSearch.isFocusableInTouchMode = true
                        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
                            this@AttendanceRequestActivity, LinearLayoutManager.VERTICAL, false
                        )
                        binding.rvLeaveList.setLayoutManager(layoutManager)
                        rvAdapter = AdapterAttendanceRequest(list, this@AttendanceRequestActivity,userType)
                        binding.rvLeaveList.adapter = rvAdapter
                    } else {
                        binding.etDirSearch.isFocusable = false
                        binding.etDirSearch.isFocusableInTouchMode = false
                        binding.txtMsg.visibility = View.VISIBLE
                    }


                } else {
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE
                    binding.rvLeaveList.visibility = View.GONE
                }
            } else {
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
                binding.rvLeaveList.visibility = View.GONE
            }


        }
        settingsViewModel.mAttendanceRequestActionResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
                getEmployeeComId()?.let {
                    settingsViewModel.attendanceRequestList(this@AttendanceRequestActivity, it,false)
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
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                loadRequestData()
            }

            imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
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
            list
        } else {
            list.filter {
                it.employee.name.contains(query, ignoreCase = true) || it.employee.email.contains(
                    query, ignoreCase = true
                ) || it.employee.phone.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }

    fun actionRequest(id: Int, status: String) {
        AlertDialog.Builder(this).setTitle("Confirm Action")
            .setMessage("Are you sure? You want to mark this request as $status?")
            .setPositiveButton("OK") { dialog, _ ->
                val request = AttendanceActionRequest(
                    request_id = id,
                    status = status
                )
                settingsViewModel.attendanceRequestStatusUpdate(
                    this@AttendanceRequestActivity, id, request
                )
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun openRejectDialog(id: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reject_missed_punch, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val rbHalfDay = dialogView.findViewById<android.widget.RadioButton>(R.id.rb_half_day)
        val rbAbsent = dialogView.findViewById<android.widget.RadioButton>(R.id.rb_absent)
        val etRejectReason = dialogView.findViewById<android.widget.EditText>(R.id.et_reject_reason)
        val btnCancel = dialogView.findViewById<android.view.View>(R.id.btn_cancel_reject)
        val btnSubmit = dialogView.findViewById<android.view.View>(R.id.btn_submit_reject)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
            val reason = etRejectReason.text.toString().trim()
            if (reason.isEmpty()) {
                CustomToast(this, "Please enter rejection reason")
                return@setOnClickListener
            }

            val attendanceType = when {
                rbHalfDay.isChecked -> "Halfday"
                rbAbsent.isChecked -> "Absent"
                else -> "Present"
            }

            val request = AttendanceActionRequest(
                request_id = id,
                status = "Rejected",
                reject_reason = reason,
                reject_attendance_type = attendanceType
            )

            settingsViewModel.attendanceRequestStatusUpdate(
                this@AttendanceRequestActivity, id, request
            )
            dialog.dismiss()
        }

        dialog.show()
    }

}