package com.asl_emp_mng.app.screens.auth

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.BaseActivity
import com.asl_emp_mng.app.databinding.ActivityLoginWithOtpactivityBinding
import com.asl_emp_mng.app.utils.CustomLoader

class LoginWithOTPActivity : BaseActivity<ActivityLoginWithOtpactivityBinding, AuthViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_login_with_otpactivity
    override val viewModel: AuthViewModel by lazy { AuthViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding?.lifecycleOwner = this


        validateField()
        onClickListeners()
        obversers()
    }

    private fun onClickListeners() {
        viewDataBinding?.apply {
            tieMobileNo.setText("8709305214")
            btnSignIn.setOnClickListener {
                viewModel?.sendOTP(this@LoginWithOTPActivity, tieMobileNo.text.toString().trim())
            }

            tvRegisterNow.setOnClickListener {
                startActivity(Intent(this@LoginWithOTPActivity, MobileSignUp::class.java))
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


    private fun obversers() {
        viewModel.getLoaderLiveData().observe(this) {
            if (it.equals("load", ignoreCase = true)) {
                if (!customLoader.isShowing)
                    customLoader.show()
            } else if (it.equals("stop", ignoreCase = true)) {
                if (customLoader.isShowing)
                    customLoader.dismiss()
            }
        }

        viewModel.mOtpResponse.observe(this) {
            if (it.success) {
                val mobile = viewDataBinding?.tieMobileNo?.text.toString().trim()
                val otp = it.otp
                val i = Intent(this@LoginWithOTPActivity, OtpVerifyActivity::class.java)
                i.putExtra("mobile", mobile)
                i.putExtra("otp", otp)
                startActivity(i)

            }
        }
    }

}
