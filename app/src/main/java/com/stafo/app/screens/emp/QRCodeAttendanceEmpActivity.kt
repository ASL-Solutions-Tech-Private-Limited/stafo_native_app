package com.stafo.app.screens.emp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.gms.location.LocationServices
import com.stafo.app.R
import com.stafo.app.databinding.ActivityQrcodeAttendanceEmpBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.QRAttendanceMarkRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeDetails
import com.google.zxing.integration.android.IntentIntegrator
import com.stafo.app.screens.settings.dataClass.GetAttendanceBranchRequest

class QRCodeAttendanceEmpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrcodeAttendanceEmpBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var codeScanner: CodeScanner



    private var branchLat:Double = 0.0
    private var branchLong :Double = 0.0
    private var radar  :Float = 0.0f

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


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startScanning()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
        }


    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (Double, Double) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback(location.latitude, location.longitude)
            } else {
                CustomToast(this, "Unable to fetch location. Ensure GPS is enabled.")
            }
        }
    }

    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun onClickListener() {
        binding.apply {


            val employeeId = getEmployeeDetails()?.id

            employeeId?.let { empId ->

                val request= GetAttendanceBranchRequest(
                    employee_id =empId.toString()
                )

                settingsViewModel.getEmpAttendanceBranch(this@QRCodeAttendanceEmpActivity, request)
            }


            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }




        }


    }


    private fun startScanning() {
        // val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, binding.scannerView)


        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
        codeScanner.startPreview()

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {


                Log.d("res",it.text)

                getCurrentLocation { userLat, userLong ->


                    val distance = getDistance(userLat, userLong, branchLat, branchLong)

                    if (distance <= radar) {

                        val employeeId = getEmployeeDetails()?.id
                        employeeId?.let { empId ->

                            val request = QRAttendanceMarkRequest(
                                employee_id = empId,
                                qrcode = it.text
                            )
                            settingsViewModel.markAttendanceQREmp(this@QRCodeAttendanceEmpActivity, request)
                        }
                    } else {
                        CustomToast(this@QRCodeAttendanceEmpActivity, "You are outside the allowed area. Move closer.")
                    }
                }

                codeScanner.releaseResources()
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {

                CustomToast(this,"${it.message}")
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanning()
        } else {
           CustomToast(this,"Camera permission denied")
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAttendanceBranchResponse.observe(this) {

            if (it.status) {

                if (it.data.latitude != null && it.data.longitude!=null ){
                    branchLat = it.data.latitude.toDouble()
                    branchLong = it.data.longitude.toDouble()
                    radar = it.data.radar.toFloat()
                }

            } else {
                CustomToast(this, it.message)
            }


        }



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


}