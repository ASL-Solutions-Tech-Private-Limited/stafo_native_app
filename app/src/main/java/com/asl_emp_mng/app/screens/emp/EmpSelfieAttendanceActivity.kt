package com.asl_emp_mng.app.screens.emp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
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
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.PunchInType
import com.asl_emp_mng.app.databinding.ActivityEmpSelfieAttendanceBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.PunchInRequest
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.mmi.MapmyIndiaMapView
import com.mmi.layers.Marker
import com.mmi.layers.Polygon
import com.mmi.layers.UserLocationOverlay
import com.mmi.layers.location.GpsLocationProvider
import com.mmi.util.GeoPoint
import java.io.File
import java.io.FileOutputStream

class EmpSelfieAttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpSelfieAttendanceBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

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
        binding?.apply {

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
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
                val bitmap = binding.sivEmpPunch.drawable?.toBitmap()

                if (bitmap != null) {
                    val file = bitmapToFile(bitmap, Bitmap.CompressFormat.JPEG, "jpg")
                    val employeeId = getEmployeeDetails()?.id

                    if (file != null) {
                        employeeId?.let { empId ->
                            settingsViewModel.selfieAttendanceEmpolyee(
                                this@EmpSelfieAttendanceActivity,
                                empId,
                                file
                            )
                        }
                    } else {
                        CustomToast(this@EmpSelfieAttendanceActivity, "Failed to post image file.")
                    }
                } else {
                    CustomToast(this@EmpSelfieAttendanceActivity, "No image captured.")
                }
            }


        }


    }


    /*    private fun bitmapToFile(bitmap: Bitmap): File? {
            return try {
                val file = File(cacheDir, "selfie_${System.currentTimeMillis()}.png")
                file.createNewFile()

                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }*/

    private fun bitmapToFile(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        fileExtension: String
    ): File? {
        return try {
            val file = File(cacheDir, "selfie_${System.currentTimeMillis()}.$fileExtension")
            file.createNewFile()

            val outputStream = FileOutputStream(file)
            bitmap.compress(format, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mSelfieAttendanceEmpResponse.observe(this) {

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK) {
            val imageBitmap: Bitmap? = data?.extras?.get("data") as? Bitmap

            if (imageBitmap != null) {
                binding.sivEmpPunch.visibility = View.VISIBLE
                binding.sivEmpPunch.setImageBitmap(imageBitmap)
            } else {
                CustomToast(this, "Failed to capture image. Try again.")
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               /* val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                startActivityForResult(cameraIntent, 123)*/

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                startActivityForResult(cameraIntent, 123)
            } else {
                CustomToast(this, "Camera Permission Denined..")
            }
        }
    }
}