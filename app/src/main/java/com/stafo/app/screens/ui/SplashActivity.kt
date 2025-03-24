package com.stafo.app.screens.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.stafo.app.R
import com.stafo.app.base.BaseActivity
import com.stafo.app.databinding.ActivitySplashBinding
import com.stafo.app.screens.auth.LoginWithOTPActivity
import com.stafo.app.screens.auth.OnBoardingActivity
import com.stafo.app.screens.dashboard.EmployeeDashboard
import com.stafo.app.screens.dashboard.EmployerDashboard
import com.stafo.app.utils.CommonViewModel
import com.stafo.app.utils.getIsCOMPANYLogin
import com.stafo.app.utils.getIsEMPLogin
import com.stafo.app.utils.isOnBoardingScreenShown
import com.stafo.app.utils.setFBToken

class SplashActivity : BaseActivity<ActivitySplashBinding, CommonViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_splash
    override val viewModel: CommonViewModel by lazy { CommonViewModel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        makeStatusBarTransparent()

        viewDataBinding?.lifecycleOwner = this

        val delayMillis = 300L
        registerFirebase()
        Handler(Looper.getMainLooper()).postDelayed({
            viewDataBinding?.imgSplash?.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            viewDataBinding?.imgSplash?.startAnimation(animation)
        }, 100)

      /*  Handler(Looper.getMainLooper()).postDelayed({
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
        }, delayMillis)*/


        Handler(Looper.getMainLooper()).postDelayed({
            val isCompanyLogin = getIsCOMPANYLogin(this)
            val isEmployeeLogin = getIsEMPLogin(this)

            Log.d("DEBUG", "isOnBoardingScreenShown: ${isOnBoardingScreenShown(this)}")
            Log.d("DEBUG", "isCompanyLogin: $isCompanyLogin")
            Log.d("DEBUG", "isEmployeeLogin: $isEmployeeLogin")

            if (isOnBoardingScreenShown(this)) {
                when {
                    isCompanyLogin -> {
                        startActivity(Intent(this, EmployerDashboard::class.java))
                    }
                    isEmployeeLogin -> {
                        startActivity(Intent(this, EmployeeDashboard::class.java))
                    }
                    else -> {
                        startActivity(Intent(this, LoginWithOTPActivity::class.java))
                    }
                }
            } else {
                startActivity(Intent(this, OnBoardingActivity::class.java))
            }
            finish()
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

    private fun registerFirebase() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful)
                    return@OnCompleteListener
                // Get new FCM registration token
                val token = task.result
                Log.e("TAG", "registerFirebase: $token", )
                if (!token.isNullOrBlank()) {
                    setFBToken(token)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}