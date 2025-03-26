package com.stafo.app.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.stafo.app.R
import com.stafo.app.base.BaseActivity
import com.stafo.app.databinding.ActivityLoginBinding
import com.stafo.app.screens.dashboard.EmployerDashboard
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class LoginActivity : BaseActivity<ActivityLoginBinding, AuthViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_login
    override val viewModel: AuthViewModel by lazy { AuthViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        viewDataBinding?.lifecycleOwner = this
        obversers()

        onClickListeners()
    }

    private fun onClickListeners() {
        viewDataBinding?.apply {
            tvRegisterNow.setOnClickListener {
                startActivity(Intent(this@LoginActivity, MobileSignUp::class.java))
            }
            btnSignIn.setOnClickListener {
                if (isValidFields()) {
                    viewModel.getLoaderLiveData().observe(this@LoginActivity) { handleLoader(it) }

                    viewModel.userLogin(
                        this@LoginActivity,
                        userNumber.text.toString(),
                        userPassword.text.toString()
                    )
                }
                //startActivity(Intent(this@LoginActivity, EmployeeDashboard::class.java))
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

        viewModel.mLoginResponse.observe(this) {
            if (it.success) {

                val name = it.data.company.company_name
                val i = Intent(this@LoginActivity, EmployerDashboard::class.java)
                i.putExtra("name", name)
                startActivity(i)

                saveToken(this,"token",it.data.token)

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


    private fun saveToken(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }
    private fun isValidFields(): Boolean {
        viewDataBinding?.apply {
            if (userNumber.text.isNullOrEmpty()) {
                userNumber.error = "Please enter mobile number"
                userNumber.requestFocus()
                return false
            } else if (userPassword.text.isNullOrEmpty()) {
                userPassword.error = "Please enter password"
                userPassword.requestFocus()
                return false
            } else if (!cbRememberMe.isChecked) {
                CustomToast(this@LoginActivity, "Please check remember me")
                return false
            }
        }
        viewDataBinding?.apply {
            btnSignIn.isEnabled = true
            btnSignIn.alpha = 1f
        }
        return true
    }



}