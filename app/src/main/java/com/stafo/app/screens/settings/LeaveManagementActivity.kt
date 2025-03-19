package com.stafo.app.screens.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.LeavesManagementAdapter
import com.stafo.app.databinding.ActivityLeaveManagementBinding
import com.stafo.app.databinding.CustomEmpDetailsBottomSheetLayoutBinding
import com.stafo.app.screens.settings.dataClass.ApproveLeaveRequest
import com.stafo.app.screens.settings.dataClass.LeaveData
import com.stafo.app.screens.settings.dataClass.LeaveRequestBody
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getFormatDate
import com.google.android.material.bottomsheet.BottomSheetDialog

class LeaveManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaveManagementBinding
    private lateinit var rvAdapter: LeavesManagementAdapter

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var leaveList: List<LeaveData> = listOf()
    private var filteredList: List<LeaveData> = listOf()

    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLeaveManagementBinding.inflate(layoutInflater)
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

    private fun onClickListener() {
        binding?.apply {

            getEmployeeComId()?.let {
                val request = LeaveRequestBody(
                    companyId = it,
                    employeeId = ""
                )
                settingsViewModel.getAllLeaveList(this@LeaveManagementActivity, request)
            }




            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false

                getEmployeeComId()?.let {
                    val request = LeaveRequestBody(
                        companyId = it,
                        employeeId = ""
                    )
                    settingsViewModel.getAllLeaveList(this@LeaveManagementActivity, request)
                }


                //settingsViewModel.getPendingLeaveList(this@LeaveManagementActivity)
            }

            imageSettings.setOnClickListener { view ->
                showPopupMenu(view)
            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mLeaveResponse.observe(this) {

            if (it.data.isNotEmpty()) {

                leaveList = it.data.filter { leave -> leave.status == "pending" }
                filteredList = leaveList

                if (leaveList.isNotEmpty()) {
                    binding.rvShowLeaveList.visibility=View.VISIBLE
                    binding.txtMsg.visibility = View.GONE

                    binding.etDirSearch.isFocusable = true
                    binding.etDirSearch.isFocusableInTouchMode = true

                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.rvShowLeaveList.setLayoutManager(layoutManager)
                    rvAdapter = LeavesManagementAdapter(leaveList, this)
                    binding.rvShowLeaveList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()
                } else {
                    binding.rvShowLeaveList.visibility=View.GONE
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE
                }


            } else {
                binding.rvShowLeaveList.visibility=View.GONE
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
            }


        }
        settingsViewModel.mApproveLeaveResponse.observe(this) {

            CustomToast(this, it.message)
            getEmployeeComId()?.let {
                val request = LeaveRequestBody(
                    companyId = it,
                    employeeId = ""
                )
                settingsViewModel.getAllLeaveList(this@LeaveManagementActivity, request)
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
            leaveList
        } else {
            leaveList.filter {
                it.employeeBasicInfo.name.contains(query, ignoreCase = true) ||
                        it.employeeBasicInfo.email.contains(query, ignoreCase = true) ||
                        it.employeeBasicInfo.phone.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val menu = popupMenu.menu
        val options = resources.getStringArray(R.array.leave_settings)
        options.forEachIndexed { index, option ->
            menu.add(0, index, index, option)
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                0 -> {
                    startActivity(Intent(this, CreateLeavePolicyActivity::class.java))
                    true
                }

                1 -> {
                    startActivity(Intent(this, LeaveRequestHistoryActivity::class.java))
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    fun showCustomBottomSheet(list: List<LeaveData>, position: Int, duration: String) {
        bottomSheetDialog = BottomSheetDialog(this)
        val binding = CustomEmpDetailsBottomSheetLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)
        binding.txtEmpName.text = list[position].employeeBasicInfo.name
        binding.txtDuration.text = duration
        binding.txtLeaveDate.text = getFormatDate(list[position].fromDate) + " - " + getFormatDate(list[position].toDate)
        binding.txtStartDate.text = getFormatDate(list[position].fromDate)
        binding.txtEndDate.text = getFormatDate(list[position].toDate)
        binding.txtRemarks.text = list[position].reason


        binding.bottomSheetCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }


    fun approveRequest(id: String, status: String) {

        val body = ApproveLeaveRequest(
            id = id,
            status = status
        )

        settingsViewModel.postPendingLeave(this@LeaveManagementActivity, body)
    }




}