package com.stafo.app.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.base.request.VerifyOtpRequestBody
import com.stafo.app.databinding.ActivityOtpVerifyBinding
import com.stafo.app.screens.dashboard.EmployeeDashboard
import com.stafo.app.screens.dashboard.EmployerDashboard
import com.stafo.app.screens.emp.NewDeviceRegisterActivity
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getAndroidVersion
import com.stafo.app.utils.getDeviceName
import com.stafo.app.utils.getFBToken
import com.stafo.app.utils.setCompanyDetails
import com.stafo.app.utils.setEmployeeDetails
import com.stafo.app.utils.setIsCOMPANYLogin
import com.stafo.app.utils.setIsEMPLogin
import com.stafo.app.utils.setUserAccessToken

class OtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var mobile: String
    private lateinit var otp: String
    private lateinit var deviceID: String


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        Log.e("TAG", "onCreate: ${getFBToken()}")

        mobile = intent.extras?.getString("mobile") ?: ""
        otp = intent.extras?.getString("otp") ?: ""

        deviceID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)



        onClickListener()
        observeViewModel()
        binding.llOtp.requestFocus()
    }

    private fun onClickListener() {
        startTimer()
        binding.apply {

            binding.btnOtpVerify.isEnabled = false
            binding.btnOtpVerify.alpha = 0.3f


            tvOtpMobileNo.text = "+91${mobile}"



            llOtp.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 6) {
                        btnOtpVerify.isEnabled = true
                        btnOtpVerify.alpha = 1f
                    } else {
                        btnOtpVerify.isEnabled = false
                        btnOtpVerify.alpha = .5f
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })



            btnOtpVerify.setOnClickListener {

                if (!llOtp.text.toString().isNullOrEmpty()) {

                    val getOtp = llOtp.text.toString()

                    val request = VerifyOtpRequestBody(
                        mobile_number = mobile,
                        otp = getOtp,
                        device_id = deviceID,
                        firebase_token = getFBToken() ?: "",
                        device_name = getDeviceName(),
                        android_version = getAndroidVersion()
                    )




                    authViewModel.verifyOTP(this@OtpVerifyActivity, request)

                } else CustomToast(this@OtpVerifyActivity, "Please enter your OTP!")

            }

            ivBack.setOnClickListener { _ ->
                finish()
            }

            llResendCode.setOnClickListener {
                authViewModel.sendOTP(this@OtpVerifyActivity, mobile)
            }

        }
    }


    private fun observeViewModel() {

        authViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        authViewModel.mVerifyOtpResponse.observe(this) {
            if (it.success) {
                if (it.data?.company != null) {
                    setUserAccessToken(it.data?.token ?: "")
                    setIsCOMPANYLogin(this, true)
                    setCompanyDetails(it.data.company)
                    startActivity(Intent(this@OtpVerifyActivity, EmployerDashboard::class.java))
                    finish()
                } else if (it.data?.employee != null) {
                    if (it.data?.device_change == "yes") {
                        // setUserAccessToken(it.data?.token ?: "")
                         setEmployeeDetails(it.data.employee)
                        // setIsEMPLogin(this,true)
                        startActivity(
                            Intent(
                                this@OtpVerifyActivity,
                                NewDeviceRegisterActivity::class.java
                            ).apply {
                                putExtra("deviceId", it.data?.device_id)
                            })
                        finish()
                    } else {
                        setUserAccessToken(it.data?.token ?: "")
                        setIsEMPLogin(this, true)
                        setEmployeeDetails(it.data.employee)
                        startActivity(
                            Intent(
                                this@OtpVerifyActivity,
                                EmployeeDashboard::class.java
                            )
                        )
                        finish()
                    }
                } else {
                    startActivity(Intent(this@OtpVerifyActivity, SignUpActivity::class.java).apply {
                        putExtra("mobile", mobile)
                    })
                    finish()
                }
            } else {
                /*if (it.message==""){
                    val intent = Intent(this@OtpVerifyActivity, NewDeviceRegisterActivity::class.java)
                    intent.putExtra("mobile", mobile)
                    startActivity(intent)
                }*/
                CustomToast(this, it.message)
            }
        }

        authViewModel.mOtpResponse.observe(this) {
            if (it.success) {
                otp = it.otp
                startTimer()
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


    private fun setFocusChangeListener(editText: EditText) {


        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                editText.setBackgroundResource(R.drawable.bg_focused)
            } else {
                if (!editText.text.isNullOrEmpty()) {
                    editText.setBackgroundResource(R.drawable.bg_focused)
                } else {
                    editText.setBackgroundResource(R.drawable.otp_edittext)
                }
            }
        }
    }

    private fun startTimer() {
        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.llTimer.visibility = View.VISIBLE
                binding.llResendCode.visibility = View.GONE

                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60

                binding.tvTimerTime.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.llTimer.visibility = View.GONE
                binding.llResendCode.visibility = View.VISIBLE
            }
        }.start()
    }


    private fun saveToken(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }
}