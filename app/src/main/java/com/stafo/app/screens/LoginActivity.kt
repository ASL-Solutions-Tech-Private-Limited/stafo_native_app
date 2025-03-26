package com.stafo.app.screens

import android.content.Intent
import android.os.Bundle
import com.stafo.app.R
import com.stafo.app.base.BaseActivity
import com.stafo.app.databinding.ActivityLoginBinding
import com.stafo.app.screens.dashboard.EmployeeDashboard
import com.stafo.app.screens.dashboard.EmployerDashboard
import com.stafo.app.utils.CommonViewModel

class LoginActivity : BaseActivity<ActivityLoginBinding, CommonViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_login
    override val viewModel: CommonViewModel by lazy { CommonViewModel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding?.lifecycleOwner = this

        viewDataBinding?.apply {
            tvRegisterNow.setOnClickListener {

                startActivity(Intent(this@LoginActivity, EmployerDashboard::class.java))
                //startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))

            }
            btnSignIn.setOnClickListener {
                startActivity(Intent(this@LoginActivity, EmployeeDashboard::class.java))
            }
        }

    }

}