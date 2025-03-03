package com.asl_emp_mng.app.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityOtpVerifyBinding
import com.asl_emp_mng.app.screens.dashboard.EmployeeDashboard
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.screens.emp.NewDeviceRegisterActivity
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.setCompanyDetails
import com.asl_emp_mng.app.utils.setEmployeeDetails
import com.asl_emp_mng.app.utils.setIsCOMPANYLogin
import com.asl_emp_mng.app.utils.setIsEMPLogin
import com.asl_emp_mng.app.utils.setUserAccessToken

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


        mobile = intent.extras?.getString("mobile") ?: ""
        otp = intent.extras?.getString("otp") ?: ""

        deviceID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        binding.llOtp.setText(otp)
        onClickListener()
        observeViewModel()
    }

    private fun onClickListener() {
        startTimer()
        binding?.apply {
            tvOtpMobileNo.text = "+91${mobile}"
            llOtp.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
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
                authViewModel.verifyOTP(this@OtpVerifyActivity, mobile, otp,deviceID)
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

                if (it.data?.device_change=="no"){
                    if (it.data?.company != null) {
                        setUserAccessToken(it.data?.token ?: "")
                        setIsCOMPANYLogin(true)
                        setCompanyDetails(it.data.company)
                        startActivity(Intent(this@OtpVerifyActivity, EmployerDashboard::class.java))
                        finish()
                    } else if (it.data?.employee != null) {
                        // saveToken(this, "token", it.data?.token ?: "")
                        setUserAccessToken(it.data?.token ?: "")
                        setIsEMPLogin(true)
                        setEmployeeDetails(it.data.employee)
                        startActivity(Intent(this@OtpVerifyActivity, EmployeeDashboard::class.java))
                        finish()
                    } else {

                        // saveToken(this, "token", it.data?.token ?: "")
                        startActivity(Intent(this@OtpVerifyActivity, SignUpActivity::class.java).apply {
                            putExtra("mobile", mobile)
                        })
                        finish()
                    }
                }else if (it.data?.device_change=="yes"){

                    if (it.data?.employee != null) {
                        setUserAccessToken(it.data?.token ?: "")
                        setEmployeeDetails(it.data.employee)

                        startActivity(Intent(this@OtpVerifyActivity, NewDeviceRegisterActivity::class.java).apply {
                            putExtra("deviceId", it.data?.device_id)
                        })
                        finish()
                    }



                }



            } else {
                if (it.message==""){
                    val intent = Intent(this@OtpVerifyActivity, NewDeviceRegisterActivity::class.java)
                    intent.putExtra("mobile", mobile)
                    startActivity(intent)
                }
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
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.llTimer.visibility = View.VISIBLE
                binding.llResendCode.visibility = View.GONE
                binding.tvTimerTime.text = "" + millisUntilFinished / 1000
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