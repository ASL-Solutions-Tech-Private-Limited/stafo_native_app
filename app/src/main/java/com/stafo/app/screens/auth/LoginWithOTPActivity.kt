package com.stafo.app.screens.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.stafo.app.R
import com.stafo.app.base.BaseActivity
import com.stafo.app.databinding.ActivityLoginWithOtpactivityBinding
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class LoginWithOTPActivity : BaseActivity<ActivityLoginWithOtpactivityBinding, AuthViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_login_with_otpactivity
    override val viewModel: AuthViewModel by lazy { AuthViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    private val MY_PERMISSIONS_REQUEST_LOCATION: Int = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        viewDataBinding?.lifecycleOwner = this

        requestPermissions()
        validateField()
        onClickListeners()
        observers()
    }

    override fun onResume() {
        super.onResume()
        if (arePermissionsGranted()) {
            enableLoginButton()
        }
    }

    private fun onClickListeners() {
        viewDataBinding?.apply {


            btnSignIn.setOnClickListener {

                val mobileNumber = viewDataBinding?.tieMobileNo?.text?.toString()?.trim()

                if (!mobileNumber.isNullOrEmpty()) {
                    if (arePermissionsGranted()) {
                        viewModel?.sendOTP(
                            this@LoginWithOTPActivity,
                            tieMobileNo.text.toString().trim()
                        )
                    } else {
                        requestPermissions()
                    }

                } else CustomToast(this@LoginWithOTPActivity, "Please enter your mobile number!")

            }

            tvRegisterNow.setOnClickListener {
                //startActivity(Intent(this@LoginWithOTPActivity, MobileSignUp::class.java))
                startActivity(Intent(this@LoginWithOTPActivity, SignUpActivity::class.java))

            }
        }
    }

    private fun validateField() {
        viewDataBinding?.apply {
            tieMobileNo.addTextChangedListener {
                if (it.toString().length == 10) {
                    btnSignIn.isEnabled = true
                    btnSignIn.alpha = 1f
                } else {
                    btnSignIn.isEnabled = false
                    btnSignIn.alpha = .3f
                }
            }
        }
    }

    private fun observers() {
        viewModel.getLoaderLiveData().observe(this) {
            if (it.equals("load", ignoreCase = true)) {
                if (!customLoader.isShowing) customLoader.show()
            } else if (it.equals("stop", ignoreCase = true)) {
                if (customLoader.isShowing) customLoader.dismiss()
            }
        }

        viewModel.mOtpResponse.observe(this) {
            if (it.success) {
                val mobile = viewDataBinding?.tieMobileNo?.text.toString().trim()
                val otp = it.otp
                val intent = Intent(this@LoginWithOTPActivity, OtpVerifyActivity::class.java)
                intent.putExtra("mobile", mobile)
                intent.putExtra("otp", otp)
                startActivity(intent)
            } else {
                val msg = it.message
                if (msg == "User not found.") {
                    showDialog()
                } else {
                    CustomToast(this, it.message)
                }

            }
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this@LoginWithOTPActivity)
        builder.setTitle(R.string.app_name)
        builder.setMessage("User not found. Please register your mobile number as a company first, then try logging in again.")

        builder.setPositiveButton("Yes") { dialog, _ ->

            val intent = Intent(this@LoginWithOTPActivity, SignUpActivity::class.java)
            intent.putExtra("mobile", viewDataBinding?.tieMobileNo?.text.toString().trim())
            startActivity(intent)


            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun arePermissionsGranted(): Boolean {
        val locationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return locationGranted && notificationGranted
    }

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
        } else {
            true
        }

        if (fineLocationGranted && coarseLocationGranted && notificationGranted) {
            if (isLocationEnabled()) {
                enableLoginButton()
            } else {
                showEnableLocationDialog()
            }
        } else {
            if (isPermissionPermanentlyDenied()) {
                showSettingsDialog()
            } else {
                Toast.makeText(this, "Permissions are required to continue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionRequest.launch(permissions.toTypedArray())
    }

    private fun isPermissionPermanentlyDenied(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) &&
                        (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                                !ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.POST_NOTIFICATIONS
                                ))
                )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }



    private fun showEnableLocationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Location")
            .setMessage("Location services are turned off. Please enable them.")
            .setPositiveButton("Open Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }



    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("To continue, please allow location and notification permissions in settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }


    private fun enableLoginButton() {
        viewDataBinding?.btnSignIn?.apply {
            isEnabled = true
            alpha = 1f
            setOnClickListener {
                val mobileNumber = viewDataBinding?.tieMobileNo?.text?.toString()?.trim()

                if (!mobileNumber.isNullOrEmpty()) {
                    if (arePermissionsGranted()) {

                        viewModel.sendOTP(
                            this@LoginWithOTPActivity,
                            viewDataBinding?.tieMobileNo?.text.toString().trim()
                        )

                    } else {
                        requestPermissions()
                    }






                } else CustomToast(this@LoginWithOTPActivity, "Please enter your mobile number!")


            }
        }
    }
}
