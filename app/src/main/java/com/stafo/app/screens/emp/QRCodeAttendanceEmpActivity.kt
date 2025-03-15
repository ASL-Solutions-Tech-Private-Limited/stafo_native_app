package com.stafo.app.screens.emp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityQrcodeAttendanceEmpBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.QRAttendanceMarkRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeDetails
import com.google.zxing.integration.android.IntentIntegrator

class QRCodeAttendanceEmpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrcodeAttendanceEmpBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQrcodeAttendanceEmpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()


    }

    private fun onClickListener() {
        binding.apply {

            val intentIntegrator = IntentIntegrator(this@QRCodeAttendanceEmpActivity)
            intentIntegrator.setPrompt("Scan barcode to mark Attendance")
            intentIntegrator.setOrientationLocked(true)
            intentIntegrator.initiateScan()



            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }


    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mQRAttendanceMarkResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                CustomToast(this, "Cancelled")

            } else {

                val employeeId = getEmployeeDetails()?.id
                employeeId?.let { empId ->

                    val request = QRAttendanceMarkRequest(
                        employee_id = empId,
                        qrcode = intentResult.contents
                    )
                    settingsViewModel.markAttendanceQREmp(this@QRCodeAttendanceEmpActivity, request)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}