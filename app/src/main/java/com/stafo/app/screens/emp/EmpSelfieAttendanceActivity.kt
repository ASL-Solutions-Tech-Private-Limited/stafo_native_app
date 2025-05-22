package com.stafo.app.screens.emp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
     /*       val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
            startActivityForResult(cameraIntent, 123)*/

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            startActivityForResult(cameraIntent, 123)
        }

        onClickListener()
        observeViewModel()


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



            tvTakeSelfie.setOnClickListener {

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
            }

            btnPunchIn.setOnClickListener {
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
            }



        }


    }





    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mSelfieAttendanceEmpResponse.observe(this) {

            if (it.status) {



                Log.d("res","${it.similarity}")
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else {
                CustomToast(this, it.message)
            }


        }

        settingsViewModel.mGetAttendanceBranchResponse.observe(this) { response ->

            if (response?.status == true) {
                val branchData = response.data
                if (branchData != null && branchData.latitude != null && branchData.longitude != null) {
                    Log.d("res","check branch")
                    checkBranch = true
                    branchLat = branchData.latitude.toDouble()
                    branchLong = branchData.longitude.toDouble()
                    radar = branchData.radar.toFloat()
                } else {
                    checkBranch = false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        }


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


