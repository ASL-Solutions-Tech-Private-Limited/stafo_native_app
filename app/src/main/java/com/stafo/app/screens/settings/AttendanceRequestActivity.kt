package com.stafo.app.screens.settings

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
        setupSearchListener()

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

            if (getIsCOMPANYLogin(this@AttendanceRequestActivity)) {
                getEmployeeComId()?.let {
                    settingsViewModel.attendanceRequestList(this@AttendanceRequestActivity, it,false)
                }
            } else {
                userType="emp"
                getEmployeeDetails()?.let { it1 ->
                    settingsViewModel.attendanceRequestList(
                        this@AttendanceRequestActivity, it1.id.toString(),true)
                }

            }





            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false

                if (getIsCOMPANYLogin(this@AttendanceRequestActivity)) {
                    getEmployeeComId()?.let {
                        settingsViewModel.attendanceRequestList(this@AttendanceRequestActivity, it,false)
                    }
                } else {
                    userType="emp"
                    getEmployeeDetails()?.let { it1 ->
                        settingsViewModel.attendanceRequestList(
                            this@AttendanceRequestActivity, it1.id.toString(),true)
                    }

                }


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
                val request = AttendanceActionRequest(status = status)
                settingsViewModel.attendanceRequestStatusUpdate(
                    this@AttendanceRequestActivity, id, request
                )
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

}