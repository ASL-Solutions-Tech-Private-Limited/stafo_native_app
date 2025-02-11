package com.asl_emp_mng.app.screens.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmpLeaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpLeaveBinding


    private var setToDate = true
    private var todate = ""
    private var fromdate: String? = ""
    private var reason: String? = ""
    private var nodays = 0f

    var cal = Calendar.getInstance()
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

    }

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

            /* tvStopService.setOnClickListener {
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
                    fromdate = binding.edtFromDate.text.toString().trim()
                    todate = binding.edtToDate.text.toString().trim()
                    reason = binding.edtDescription.text.toString().trim()


                }
            }

            imageSettings.setOnClickListener { view ->
                showPopupMenu(view)
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

    private fun onDateSet() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (!setToDate) {
            fromdate = sdf.format(cal.time)

            binding.edtFromDate.setText(fromdate)


        } else if (setToDate) {
            todate = sdf.format(cal.time)
            binding.edtToDate.setText(todate)
            setNoDay()
        }
    }

    private fun setNoDay() {
        try {
            fromdate = binding.edtFromDate.text.toString().trim()
            todate = binding.edtToDate.text.toString().trim()

            if (fromdate != null && fromdate != "" && todate != null && todate != "") {

                val myFormat = "dd/MM/yyyy"
                val mDateFormat = SimpleDateFormat(myFormat)
                val mDate11 = mDateFormat.parse(fromdate)
                val mDate22 = mDateFormat.parse(todate)


                if (mDate22.before(mDate11)) {

                    val alertDialog = AlertDialog.Builder(this@EmpLeaveActivity)
                        .setTitle("Invalid Date Range")
                        .setMessage("The to date cannot be earlier than the from date.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    alertDialog.show()

                } else {
                    this.nodays = (Commonfunctions.differanceInDays(todate, fromdate) + 1).toFloat()

                    if (nodays <= 0) {

                    } else {
                        val formattedNoDays = if (nodays == nodays.toInt().toFloat()) {
                            nodays.toInt().toString()
                        } else {
                            nodays.toString()
                        }

                        binding.nodTxt.text = "No of leave : $formattedNoDays days"
                    }
                }


            }
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