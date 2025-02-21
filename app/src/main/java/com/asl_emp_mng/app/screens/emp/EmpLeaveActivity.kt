package com.asl_emp_mng.app.screens.emp

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.Commonfunctions
import com.asl_emp_mng.app.base.service.LocationForegroundService
import com.asl_emp_mng.app.databinding.ActivityEmpLeaveBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeLeaveRequestBody
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeDetails
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

class EmpLeaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpLeaveBinding


    private var setToDate = true
    private var todate = ""
    private var fromdate: String = ""
    private var reason: String = ""
    private var nodays = 0f

    var cal = Calendar.getInstance()

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpLeaveBinding.inflate(layoutInflater)
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


        settingsViewModel.mEmpLeaveRequestResponse.observe(this) {

            CustomToast(this, it.message)
            onBackPressedDispatcher.onBackPressed()
            finish()

        }

    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickListener() {


        val options = resources.getStringArray(R.array.leave_type)
        val adapter = ArrayAdapter(this, R.layout.custom_spinner_item, options)
        binding.spinnerLeaveType.setAdapter(adapter)






        binding?.apply {

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }



            edtFromDate.setOnClickListener { _ ->

                setToDate = false
                showCalender()
            }

            edtToDate.setOnClickListener { _ ->
                setToDate = true
                showCalender()
            }

          /*   tvStopService.setOnClickListener {
                 stopLocationService()
             }

               tvStartService.setOnClickListener {
                   if (hasLocationPermission()) {
                       startLocationService()
                   } else {
                       requestLocationPermission()
                   }
               }*/

            btnLeave.setOnClickListener {
                if (isValidate()) {
                    Log.d("res","post :")

                    val request = EmployeeLeaveRequestBody(
                        employee_id = getEmployeeDetails()?.id.toString(),
                        from_date = binding.edtFromDate.text.toString(),
                        to_date = binding.edtToDate.text.toString(),
                        reason = binding.edtDescription.text.toString()

                    )

                    Log.d("res","post :${request}")

                    settingsViewModel.requestLeaveEmp(this@EmpLeaveActivity, request)


                }
            }




        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val menu = popupMenu.menu
        val options = resources.getStringArray(R.array.emp_leave)
        options.forEachIndexed { index, option ->
            menu.add(0, index, index, option)
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                0 -> {
                    startActivity(Intent(this, EmployeeLeaveHistoryActivity::class.java))
                    true
                }

                1 -> {
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun isValidate(): Boolean {
        binding?.apply {
            if (edtFromDate.text.isNullOrEmpty()) {
                edtFromDate.error = "Please enter from date"
                edtFromDate.requestFocus()
                return false
            } else if (edtToDate.text.isNullOrEmpty()) {
                edtToDate.error = "Please enter to date"
                edtToDate.requestFocus()
                return false
            } else if (edtDescription.text.isNullOrEmpty()) {
                edtDescription.error = "Please enter description"
                edtDescription.requestFocus()
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalender() {

        try {

            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    onDateSet()
                }

            DatePickerDialog(
                this@EmpLeaveActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()


        } catch (e: Exception) {
            println("error$e")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDateSet() {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        if (!setToDate) {
            fromdate = sdf.format(cal.time)
            binding.edtFromDate.setText(fromdate)
        } else {
            todate = sdf.format(cal.time)
            binding.edtToDate.setText(todate)
            setNoDay()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNoDay() {
        try {
            val myFormat = "yyyy/MM/dd"
            val sdf = SimpleDateFormat(myFormat, Locale.US)

            val fromDateStr = binding.edtFromDate.text.toString().trim()
            val toDateStr = binding.edtToDate.text.toString().trim()

            if (fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                return // Exit if either date is empty
            }

            val fromDate = sdf.parse(fromDateStr)
            val toDate = sdf.parse(toDateStr)

            if (fromDate == null || toDate == null) {
                return
            }


            if (toDate.before(fromDate)) {
                binding.nodTxt.text = "No of leave: 0 days"
                AlertDialog.Builder(this@EmpLeaveActivity)
                    .setTitle("Invalid Date Range")
                    .setMessage("The 'To Date' cannot be earlier than the 'From Date'.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
                return
            }

            val daysBetween = ChronoUnit.DAYS.between(
                fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            ) + 1

            val formattedNoDays = daysBetween.toInt().toString()
            binding.nodTxt.text = "No of leave: $formattedNoDays days"

        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }



    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the service
                startLocationService()
            } else {
                // Permission denied, show a message
                Toast.makeText(
                    this,
                    "Location permission is required for this service.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopLocationService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        stopService(serviceIntent)
    }

    companion object {
        private const val REQUEST_CODE_LOCATION = 1001
    }

}