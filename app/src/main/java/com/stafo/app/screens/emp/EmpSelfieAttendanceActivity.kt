package com.stafo.app.screens.emp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.stafo.app.R
import com.stafo.app.databinding.ActivityEmpSelfieAttendanceBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetAttendanceBranchRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class EmpSelfieAttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpSelfieAttendanceBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()


    private var selfieImage: File? = null
    private var branchLat: Double = 0.0
    private var branchLong: Double = 0.0
    private var radar: Float = 0.0f
    private var checkBranch: Boolean = false
    private var showLoaderLocation: Boolean = false

    private var isSubmitting = false
    private var isFetchingLocation = false

    private var imageCapture: ImageCapture? = null
    private var mEmpID = ""

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpSelfieAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        if (getIsCOMPANYLogin(this@EmpSelfieAttendanceActivity)) {
            mEmpID = intent.getStringExtra("EMP_ID").toString()
        } else mEmpID = getEmployeeDetails()?.id.toString()


        observeViewModel()
        onClickListener()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        showLoaderLocation=false
    }

    private fun onClickListener() {
        binding.apply {
            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }






            if (ContextCompat.checkSelfPermission(
                    this@EmpSelfieAttendanceActivity, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@EmpSelfieAttendanceActivity, arrayOf(Manifest.permission.CAMERA), 100
                )
            } else {
                getCurrentLocation { lat, lng ->
                    if (lat != 0.0 && lng != 0.0) {
                        startCamera()
                        mEmpID.let { empId ->

                            val request = GetAttendanceBranchRequest(
                                employee_id = empId
                            )
                            settingsViewModel.getEmpAttendanceBranch(this@EmpSelfieAttendanceActivity, request)
                        }
                    } else {
                        CustomToast(this@EmpSelfieAttendanceActivity, "Unable to get location. Try again.")
                    }
                }

            }

        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation { lat, lng ->
                if (lat != 0.0 && lng != 0.0) {
                    startCamera()
                    mEmpID.let { empId ->

                        val request = GetAttendanceBranchRequest(
                            employee_id = empId
                        )
                        settingsViewModel.getEmpAttendanceBranch(this@EmpSelfieAttendanceActivity, request)
                    }
                } else {
                    CustomToast(this@EmpSelfieAttendanceActivity, "Unable to get location. Try again.")
                }
            }
        } else {
            CustomToast(this, "Camera permission denied")
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val rotation =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    display?.rotation ?: Surface.ROTATION_0
                } else {
                    @Suppress("DEPRECATION") windowManager.defaultDisplay.rotation
                }

            imageCapture = ImageCapture.Builder().setTargetRotation(rotation).build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (e: Exception) {
                Log.e("TAG", "startCamera: ${e.localizedMessage}")
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun takePhoto() {
        showLoaderLocation=true
        val imageCapture = imageCapture ?: return
        val outputFile = File(externalCacheDir, "selfie.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    selfieImage = outputFile
                    selfieImage?.let {
                        if (checkBranch) {

                            if (branchLat == 0.0 || branchLong == 0.0) {
                                CustomToast(this@EmpSelfieAttendanceActivity, "Please try again.")
                                onBackPressedDispatcher.onBackPressed()
                                finish()
                                return
                            }

                            getCurrentLocation { currentLat, currentLong ->

                                val distance = calculateDistance(
                                    currentLat, currentLong, branchLat, branchLong
                                )



                                if (distance <= radar) {

                                    settingsViewModel.selfieAttendanceEmpolyee(
                                        this@EmpSelfieAttendanceActivity,
                                        mEmpID.toInt(),
                                        selfieImage
                                    )
                                } else {
                                    onApiResponseError()
                                    CustomToast(
                                        this@EmpSelfieAttendanceActivity,
                                        "Please move closer to the branch area to punch attendance."
                                    )
                                    isSubmitting = false
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        onBackPressedDispatcher.onBackPressed()
                                        finish()
                                    }, 700)
                                }
                            }

                        } else {

                            isSubmitting = false
                            settingsViewModel.selfieAttendanceEmpolyee(
                                this@EmpSelfieAttendanceActivity, mEmpID.toInt(), selfieImage
                            )
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    CustomToast(this@EmpSelfieAttendanceActivity, "Capture failed! try again.")
                }
            })
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (Double, Double) -> Unit) {
        if(!showLoaderLocation){
            showLoadingDialog()
            showAlertView()
        }



        if (isFetchingLocation) return
        isFetchingLocation = true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            isFetchingLocation = false
            CustomToast(this, "Please turn on GPS to mark attendance.")
            return
        }

        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 2000L
            fastestInterval = 1000L
            numUpdates = 1
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                isFetchingLocation = false

                val location = locationResult.lastLocation
                if (location != null) {
                    hideAlertView()
                    loadingDialog.dismiss()
                    if (location.accuracy <= 50f) {
                        callback(location.latitude, location.longitude)
                    } else {
                        CustomToast(
                            this@EmpSelfieAttendanceActivity, "Location not accurate. Try again."
                        )
                        isSubmitting = false
                        onBackPressedDispatcher.onBackPressed()
                        finish()
                    }
                } else {
                    hideAlertView()
                    loadingDialog.dismiss()
                    CustomToast(this@EmpSelfieAttendanceActivity, "Unable to get location.")
                    isSubmitting = false
                }
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFetchingLocation) {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                isFetchingLocation = false
                isSubmitting = false
            }
        }, 10_000)

        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }


    private fun showAlertView() {
        val alertView =binding.viewAlert
        alertView.apply {
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        }
        binding.rlSelfiePunchIn.visibility=View.GONE
    }

   private fun hideAlertView() {
        val alertView =binding.viewAlert
        alertView.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                alertView.visibility = View.GONE
            }
            .start()
        binding.rlSelfiePunchIn.visibility=View.VISIBLE
    }





    fun calculateDistance(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun showLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialog_loading)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog.show()
    }


    private fun onApiResponseSuccess() {
        binding.cameraOverlayView.setStrokeColor(Color.GREEN)
    }

    fun onApiResponseError() {
        binding.cameraOverlayView.setStrokeColor(Color.RED)
    }


    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        settingsViewModel.mSelfieAttendanceEmpResponse.observe(this) {
            Log.e("AttendSelfie", "observeViewModel: $it")
            if (it.status) {
                binding.rtlAttendanceMsg.visibility = View.VISIBLE
                val currentTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
                val formattedDateTime = formatter.format(currentTime)
                binding.tvPunchTime.text = formattedDateTime
                binding.tvPunchUser.text = "Punched by ${getEmployeeDetails()?.name ?: ""}"
                val bitmap = BitmapFactory.decodeFile(selfieImage?.absolutePath)
                binding.civPunchSelfie.setImageBitmap(bitmap)
                onApiResponseSuccess()
                Handler(Looper.getMainLooper()).postDelayed({
                    onBackPressedDispatcher.onBackPressed()
                    finish()
                }, 1000)
            } else {
                CustomToast(this, it.message)
                onApiResponseError()
                binding.rtlAttendanceMsg.visibility = View.GONE

            }


        }

        settingsViewModel.mGetAttendanceBranchResponse.observe(this) { response ->
            if (response?.status == true) {

                Log.e("LocationDebug", "observeViewModel: ${response.data}")
                val branchData = response.data
                if (branchData != null && branchData.latitude != null && branchData.longitude != null) {
                    checkBranch = true
                    branchLat = branchData.latitude.toDouble()
                    branchLong = branchData.longitude.toDouble()
                    radar = branchData.radar.toFloat()
                    Handler(Looper.getMainLooper()).postDelayed({
                        takePhoto()
                    }, 100)
                } else {
                    checkBranch = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        takePhoto()
                    }, 100)
                }
            } else {
                checkBranch = false

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


