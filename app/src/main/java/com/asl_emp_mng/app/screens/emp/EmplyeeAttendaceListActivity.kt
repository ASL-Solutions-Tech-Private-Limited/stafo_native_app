package com.asl_emp_mng.app.screens.emp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.base.adapter.EmployeeAttendanceAdapter
import com.asl_emp_mng.app.base.model.EmployeeAttendanceModel
import com.asl_emp_mng.app.databinding.ActivityEmployeeAttendanceBinding
import com.asl_emp_mng.app.screens.dashboard.EmployeeDashboard
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeDataList
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveData
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.getUserAccessToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmplyeeAttendaceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeAttendanceBinding
    private lateinit var rvAdapter: EmployeeAttendanceAdapter


    private var attendList: List<EmployeeDataList> = listOf()
    private var filteredList: List<EmployeeDataList> = listOf()

    private val calendar = Calendar.getInstance()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var mSelectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val curren = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        mSelectedDate = curren

        onClickListener()
        observeViewModel()
        setupSearchListener()


    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MMM/yy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.txtDate.setText("$formattedDate")
                mSelectedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                settingsViewModel.getEmpList(this@EmplyeeAttendaceListActivity, mSelectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@EmplyeeAttendaceListActivity, EmployerDashboard::class.java))
        finish()
    }

    private fun onClickListener() {
        binding?.apply {

            val currentDate =
                SimpleDateFormat("dd/MMM/yy", Locale.getDefault()).format(calendar.time)
            binding.txtDate.setText(currentDate)


            settingsViewModel.getEmpList(this@EmplyeeAttendaceListActivity, mSelectedDate)

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                settingsViewModel.getEmpList(this@EmplyeeAttendaceListActivity, mSelectedDate)

            }

            imageBack.setOnClickListener {
                startActivity(
                    Intent(
                        this@EmplyeeAttendaceListActivity,
                        EmployerDashboard::class.java
                    )
                )
                finish()
            }

            llCalendar.setOnClickListener {
                showDatePicker()
            }

            //progressBar.updateProgress(50.0F)
            //  progressBar.updateProgress(Random().nextInt(100).toFloat())


        }
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mEmployeeListResponse.observe(this) {
            Log.d("res", "token ${getUserAccessToken()}")
            if (it.status) {


                if (it.data.isNotEmpty()) {
                    binding.txtMsg.visibility = View.GONE

                    binding.etDirSearch.isFocusable = true
                    binding.etDirSearch.isFocusableInTouchMode = true


                    attendList=it.data
                    filteredList=attendList


                    val layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvEmpAttendList.setLayoutManager(layoutManager)
                    rvAdapter = EmployeeAttendanceAdapter(attendList, this)
                    binding.rvEmpAttendList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()
                } else {
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE
                }




               /* if (it.data.isNotEmpty()) {
                    binding.etDirSearch.isFocusable = true
                    binding.etDirSearch.isFocusableInTouchMode = true

                    attendList = it.data
                    filteredList = attendList

                    rvAdapter.updateList(filteredList)
                } else {
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE

                    rvAdapter.updateList(emptyList())
                }*/


            } else {
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
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
            attendList
        } else {
            attendList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }


   /* private fun setupRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvEmpAttendList.layoutManager = layoutManager
        rvAdapter = EmployeeAttendanceAdapter(emptyList(), this)
        binding.rvEmpAttendList.adapter = rvAdapter
    }*/


}