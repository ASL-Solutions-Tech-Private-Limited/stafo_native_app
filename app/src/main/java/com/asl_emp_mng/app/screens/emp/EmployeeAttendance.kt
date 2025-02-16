package com.asl_emp_mng.app.screens.emp

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.EmployeeAttendanceAdapter
import com.asl_emp_mng.app.base.model.EmployeeAttendanceModel
import com.asl_emp_mng.app.databinding.ActivityEmployeeAttendanceBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.utils.CustomLoader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmployeeAttendance : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeAttendanceBinding
    private lateinit var rvAdapter: EmployeeAttendanceAdapter
    private lateinit var attendList: List<EmployeeAttendanceModel>

    private val calendar = Calendar.getInstance()

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var mEMPId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeeAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)
        mEMPId = intent.getStringExtra("EMPID").toString()
        onClickListener()
        observeViewModel()

        // loadAttendList()

    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                //binding.tieDateJoining.setText("$formattedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun onClickListener() {
        binding?.apply {


            settingsViewModel.getEmpList(this@EmployeeAttendance,"2025-02-07")


            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                settingsViewModel.getEmpList(this@EmployeeAttendance,"2025-02-07")

            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            //progressBar.updateProgress(50.0F)
            //  progressBar.updateProgress(Random().nextInt(100).toFloat())


        }
    }


    private fun getToken(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mEmployeeListResponse.observe(this) {
            Log.d("res", it.message)
            if (it.status) {

                Log.d("res", it.data.toString())
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