package com.stafo.app.screens.emp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmpSelfieAttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpSelfieAttendanceBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()


    private var selfieImage: File? = null
    private var branchLat:Double = 0.0
    private var branchLong :Double = 0.0
    private var radar  :Float = 0.0f
    private var checkBranch  :Boolean = false

    private var isSubmitting = false
    private var isFetchingLocation = false

    private var imageCapture: ImageCapture? = null

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



  /*      if (ContextCompat.checkSelfPermission(
                this@EmpSelfieAttendanceActivity,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@EmpSelfieAttendanceActivity,
                arrayOf(android.Manifest.permission.CAMERA),
                100
            )
        } else {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            startActivityForResult(cameraIntent, 123)
        }

        onClickListener()
        observeViewModel()*/





        observeViewModel()
        onClickListener()
    }

    private fun onClickListener() {
        binding.apply {
            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            val employeeId = getEmployeeDetails()?.id

            employeeId?.let { empId ->

                val request= GetAttendanceBranchRequest(
                    employee_id =empId.toString()
                )

                settingsViewModel.getEmpAttendanceBranch(this@EmpSelfieAttendanceActivity, request)
            }

            if (ContextCompat.checkSelfPermission(this@EmpSelfieAttendanceActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@EmpSelfieAttendanceActivity, arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                startCamera()
            }

          /*  tvTakeSelfie.setOnClickListener {

                if (ContextCompat.checkSelfPermission(
                        this@EmpSelfieAttendanceActivity,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    ActivityCompat.requestPermissions(
                        this@EmpSelfieAttendanceActivity,
                        arrayOf(android.Manifest.permission.CAMERA),
                        100
                    )
                } else {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                    startActivityForResult(cameraIntent, 123)
                }
            }*/

           /* btnPunchIn.setOnClickListener {
                if (isSubmitting) return@setOnClickListener

                if (selfieImage == null) {
                    CustomToast(this@EmpSelfieAttendanceActivity, "Please upload a selfie first")
                } else {
                    isSubmitting = true
                    btnPunchIn.isEnabled = false

                    if (checkBranch) {
                        getCurrentLocation { userLat, userLong ->
                            val distance = getDistance(userLat, userLong, branchLat, branchLong)
                            val tolerance = 1.0f

                            if (distance <= radar + tolerance) {
                                getEmployeeDetails()?.id?.let { empId ->
                                    settingsViewModel.selfieAttendanceEmpolyee(
                                        this@EmpSelfieAttendanceActivity,
                                        empId,
                                        selfieImage
                                    )
                                }
                            } else {
                                CustomToast(this@EmpSelfieAttendanceActivity, "You are outside the allowed area. Move closer.")
                            }

                            isSubmitting = false
                            btnPunchIn.isEnabled = true
                        }
                    } else {
                        getEmployeeDetails()?.id?.let { empId ->
                            settingsViewModel.selfieAttendanceEmpolyee(
                                this@EmpSelfieAttendanceActivity,
                                empId,
                                selfieImage
                            )
                        }

                        isSubmitting = false
                        btnPunchIn.isEnabled = true
                    }
                }
            }*/



        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
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
                    @Suppress("DEPRECATION")
                    windowManager.defaultDisplay.rotation
                }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(rotation)
                .build()

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
                            getCurrentLocation { userLat, userLong ->
                                val distance = getDistance(userLat, userLong, branchLat, branchLong)
                                val tolerance = 1.0f
                                if (distance <= radar + tolerance) {
                                    getEmployeeDetails()?.id?.let { empId ->
                                        settingsViewModel.selfieAttendanceEmpolyee(
                                            this@EmpSelfieAttendanceActivity,
                                            empId,
                                            selfieImage
                                        )
                                    }
                                } else {
                                    CustomToast(this@EmpSelfieAttendanceActivity, "You are outside the allowed area. Move closer.")
                                }

                            }
                        } else {
                            getEmployeeDetails()?.id?.let { empId ->
                                settingsViewModel.selfieAttendanceEmpolyee(
                                    this@EmpSelfieAttendanceActivity,
                                    empId,
                                    selfieImage
                                )
                            }

                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    CustomToast(this@EmpSelfieAttendanceActivity, "Capture failed! try again.")


                }
            }
        )
    }

    fun onApiResponseSuccess() {
        binding.cameraOverlayView.setStrokeColor(Color.GREEN)
    }

    fun onApiResponseError() {
        binding.cameraOverlayView.setStrokeColor(Color.RED)
    }

    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        settingsViewModel.mSelfieAttendanceEmpResponse.observe(this) {
            if (it.status) {
                binding.rtlAttendanceMsg.visibility=View.VISIBLE
                val currentTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
                val formattedDateTime = formatter.format(currentTime)
                binding.tvPunchTime.text = formattedDateTime
                binding.tvPunchUser.text= "Punched by ${getEmployeeDetails()?.name?:""}"
                val bitmap = BitmapFactory.decodeFile(selfieImage?.absolutePath)
                binding.civPunchSelfie.setImageBitmap(bitmap)
                onApiResponseSuccess()
                Handler(Looper.getMainLooper()).postDelayed({
                    onBackPressedDispatcher.onBackPressed()
                    finish()
                }, 1000)
            } else {
                onApiResponseError()
                binding.rtlAttendanceMsg.visibility=View.GONE
            }


        }

        settingsViewModel.mGetAttendanceBranchResponse.observe(this) { response ->
            if (response?.status == true) {
                val branchData = response.data
                if (branchData != null && branchData.latitude != null && branchData.longitude != null) {
                    checkBranch = true
                    branchLat = branchData.latitude.toDouble()
                    branchLong = branchData.longitude.toDouble()
                    radar = branchData.radar.toFloat()
                    Handler(Looper.getMainLooper()).postDelayed({
                        takePhoto()
                    }, 3000)
                } else {
                    checkBranch = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        takePhoto()
                    }, 3000)
                }
            } else {
                checkBranch = false

            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (Double, Double) -> Unit) {
        if (isFetchingLocation) return
        isFetchingLocation = true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                isFetchingLocation = false
                callback(location.latitude, location.longitude)
            } else {
                val locationRequest = LocationRequest.create().apply {
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                    interval = 1000
                    numUpdates = 1
                }

                fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        fusedLocationClient.removeLocationUpdates(this)
                        isFetchingLocation = false

                        val freshLocation = locationResult.lastLocation
                        if (freshLocation != null) {
                            callback(freshLocation.latitude, freshLocation.longitude)
                        } else {
                            CustomToast(this@EmpSelfieAttendanceActivity, "Unable to fetch accurate location.")
                            isSubmitting = false
                        }
                    }
                }, Looper.getMainLooper())
            }
        }.addOnFailureListener {
            isFetchingLocation = false
            isSubmitting = false
            CustomToast(this@EmpSelfieAttendanceActivity, "Failed to get location.")
        }
    }



    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK) {
            val imageBitmap: Bitmap? = data?.extras?.get("data") as? Bitmap

            if (imageBitmap != null) {
                binding.sivEmpPunch.visibility = View.VISIBLE
                binding.sivEmpPunch.setImageBitmap(imageBitmap)

                val imageFile = bitmapToFile(imageBitmap, this@EmpSelfieAttendanceActivity)

                if (imageFile != null) {

                    selfieImage=imageFile
                }

                } else {

                    CustomToast(this, "Failed to process image.")

                }



            } else {
                CustomToast(this, "Failed to capture image. Try again.")
            }
        }*/


    private fun bitmapToFile(bitmap: Bitmap, context: Context): File? {
        return try {

            val fileName = "selfie_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, fileName)
            file.createNewFile()

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    }


