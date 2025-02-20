package com.asl_emp_mng.app.screens.emp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
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
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.getUserAccessToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmplyeeAttendaceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeAttendanceBinding
    private lateinit var rvAdapter: EmployeeAttendanceAdapter
    private lateinit var attendList: List<EmployeeAttendanceModel>
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

    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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
            settingsViewModel.getEmpList(this@EmplyeeAttendaceListActivity, mSelectedDate)

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                settingsViewModel.getEmpList(this@EmplyeeAttendaceListActivity, mSelectedDate)

            }

            imageBack.setOnClickListener {
                startActivity(Intent(this@EmplyeeAttendaceListActivity, EmployerDashboard::class.java))
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
            Log.d("res", it.message)
            if (it.status) {

                Log.d("res", "token ${getUserAccessToken()}")
                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvEmpAttendList.setLayoutManager(layoutManager)
                rvAdapter = EmployeeAttendanceAdapter(it.data, this)
                binding.rvEmpAttendList.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()

            } else {
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


}