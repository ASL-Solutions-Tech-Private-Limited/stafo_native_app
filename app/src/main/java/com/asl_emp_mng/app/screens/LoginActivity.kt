package com.asl_emp_mng.app.screens

import android.content.Intent
import android.os.Bundle
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.BaseActivity
import com.asl_emp_mng.app.databinding.ActivityLoginBinding
import com.asl_emp_mng.app.screens.ui.EmployeeDashboard
import com.asl_emp_mng.app.screens.ui.EmployerDashboard
import com.asl_emp_mng.app.utils.CommonViewModel

class LoginActivity : BaseActivity<ActivityLoginBinding, CommonViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_login
    override val viewModel: CommonViewModel by lazy { CommonViewModel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding?.lifecycleOwner = this

        viewDataBinding?.apply {
            tvRegisterNow.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            }
            btnSignIn.setOnClickListener {
                startActivity(Intent(this@LoginActivity, EmployeeDashboard::class.java))
            }
        }

    }

}