package com.asl_emp_mng.app.screens.settings

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityAadhaarOtpVerifyBinding
import com.asl_emp_mng.app.databinding.ActivityOtpVerifyBinding
import com.asl_emp_mng.app.screens.auth.AuthViewModel
import com.asl_emp_mng.app.screens.auth.SignUpActivity
import com.asl_emp_mng.app.screens.dashboard.EmployeeDashboard
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.setCompanyDetails
import com.asl_emp_mng.app.utils.setEmployeeDetails
import com.asl_emp_mng.app.utils.setIsCOMPANYLogin
import com.asl_emp_mng.app.utils.setIsEMPLogin
import com.asl_emp_mng.app.utils.setUserAccessToken

class AadhaarOtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAadhaarOtpVerifyBinding
    private lateinit var mobile: String
    private lateinit var otp: String
    private lateinit var deviceID: String


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAadhaarOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)



    }



    private fun onClickListener() {
        startTimer()
        binding?.apply {

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
                authViewModel.verifyOTP(this@AadhaarOtpVerifyActivity, mobile, otp,deviceID)
            }

            ivBack.setOnClickListener { _ ->
                finish()
            }

            llResendCode.setOnClickListener {
                authViewModel.sendOTP(this@AadhaarOtpVerifyActivity, mobile)
            }

        }
    }


    private fun observeViewModel() {

        authViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        authViewModel.mVerifyOtpResponse.observe(this) {
            if (it.success) {
                CustomToast(this, it.message)
            } else {
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
}