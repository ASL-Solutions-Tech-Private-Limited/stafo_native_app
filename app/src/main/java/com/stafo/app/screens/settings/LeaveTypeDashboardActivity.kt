package com.stafo.app.screens.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.BranchAdapter
import com.stafo.app.databinding.ActivityLeaveTypeDashboardBinding
import com.stafo.app.screens.settings.adapter.AdapterLeaveTypeList
import com.stafo.app.screens.settings.dataClass.BranchItem
import com.stafo.app.screens.settings.dataClass.LeaveItem
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class LeaveTypeDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaveTypeDashboardBinding
    private lateinit var rvAdapter: AdapterLeaveTypeList

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var leaveItemList: List<LeaveItem> = listOf()

    private var filteredList: List<LeaveItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLeaveTypeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        setOnClickEvents()
        observeViewModel()
        setupSearchListener()


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
            leaveItemList
        } else {
            leaveItemList.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mLeaveTypeListResponse.observe(this) {

            if (it.data.isNotEmpty()) {
                binding.txtMsg.visibility = View.GONE
                binding.rvShowLeaveTypeList.visibility = View.VISIBLE
                leaveItemList = it.data
                filteredList = leaveItemList

                binding.etDirSearch.isFocusable = true
                binding.etDirSearch.isFocusableInTouchMode = true

                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvShowLeaveTypeList.setLayoutManager(layoutManager)
                rvAdapter = AdapterLeaveTypeList(leaveItemList, this,
                    onEditClick = { leaveType ->
                        val intent = Intent(this, CreateLeavePolicyActivity::class.java)
                        intent.putExtra("leave_type", "Edit")
                        intent.putExtra("leave_data", leaveType)
                        startActivity(intent)
                    },
                    onDeleteClick = { leaveType ->
                        showCompanyDeleteDialog(leaveType.id)
                    }

                )
                binding.rvShowLeaveTypeList.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()
            } else {
                binding.rvShowLeaveTypeList.visibility = View.GONE
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
            }


        }


        settingsViewModel.mLeaveTypeDeleteResponse.observe(this) {

            if (it.status) {
                getEmployeeComId()?.let {
                    settingsViewModel.getLeaveTypeList(
                        this@LeaveTypeDashboardActivity,
                        it.toInt()
                    )
                }
                CustomToast(this, it.message)
            } else {
                CustomToast(this, it.message)
            }


        }


    }

    override fun onResume() {
        super.onResume()
        getEmployeeComId()?.let {
            settingsViewModel.getLeaveTypeList(
                this@LeaveTypeDashboardActivity,
                it.toInt()
            )
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

    private fun setOnClickEvents() {
        binding.apply {
            getEmployeeComId()?.let {
                settingsViewModel.getLeaveTypeList(
                    this@LeaveTypeDashboardActivity,
                    it.toInt()
                )
            }

            ivBack.setOnClickListener {
                onBackPressed()

            }


            swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = false
                getEmployeeComId()?.let {
                    settingsViewModel.getLeaveTypeList(
                        this@LeaveTypeDashboardActivity,
                        it.toInt()
                    )
                }

            }

            btnAddLeaveType.setOnClickListener {
                startActivity(
                    Intent(
                        this@LeaveTypeDashboardActivity,
                        CreateLeavePolicyActivity::class.java
                    )
                )
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }


        }


    }

    private fun showCompanyDeleteDialog(itemId: Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? Delete this.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            settingsViewModel.deleteLeaveType(this, itemId)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}