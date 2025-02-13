package com.asl_emp_mng.app.screens.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.BaseActivity
import com.asl_emp_mng.app.databinding.ActivitySplashBinding
import com.asl_emp_mng.app.screens.auth.LoginWithOTPActivity
import com.asl_emp_mng.app.screens.auth.OnBoardingActivity
import com.asl_emp_mng.app.screens.dashboard.EmployeeDashboard
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.utils.CommonViewModel
import com.asl_emp_mng.app.utils.getIsCOMPANYLogin
import com.asl_emp_mng.app.utils.getIsLogin
import com.asl_emp_mng.app.utils.isOnBoardingScreenShown

class SplashActivity : BaseActivity<ActivitySplashBinding, CommonViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_splash
    override val viewModel: CommonViewModel by lazy { CommonViewModel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        viewDataBinding?.lifecycleOwner = this

        val delayMillis = 3000L // Total delay time

        Handler(Looper.getMainLooper()).postDelayed({
            viewDataBinding?.imgSplash?.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            viewDataBinding?.imgSplash?.startAnimation(animation)
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isOnBoardingScreenShown() && getIsCOMPANYLogin() == true) {
                startActivity(Intent(this, EmployerDashboard::class.java))
                finish()
            } else if (isOnBoardingScreenShown() && getIsLogin() == true) {
                startActivity(Intent(this, EmployeeDashboard::class.java))
                finish()
            } else if (isOnBoardingScreenShown()) {
                startActivity(Intent(this, LoginWithOTPActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
            }
        }, delayMillis)


    }

    private fun makeStatusBarTransparent() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun getToken(key: String): String? {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }
}