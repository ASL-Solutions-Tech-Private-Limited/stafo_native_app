package com.stafo.app.screens.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.databinding.ActivityMobileSignUpBinding
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.google.android.material.textfield.TextInputEditText

class MobileSignUp : AppCompatActivity() {
    private lateinit var binding:ActivityMobileSignUpBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMobileSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        binding?.apply {

            btnNext.setOnClickListener {
                if (validateBasicInfo()){

                    observeViewModel()


                }
            }


        }
    }


    private fun observeViewModel() {
        authViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        authViewModel.sendOTP(this,binding.tieMobileNo.text.toString().trim())

        authViewModel.mOtpResponse.observe(this) {
            if (it.success) {
                CustomToast(this, it.message)

                val mobile = binding.tieMobileNo.text.toString().trim()
                val otp = it.otp.toString()
                val i = Intent(this@MobileSignUp, OtpVerifyActivity::class.java)
                i.putExtra("mobile", mobile)
                i.putExtra("otp", otp)
                startActivity(i)

                Log.d("otp","otp: ${it.otp}")
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

    private fun validateBasicInfo(): Boolean {
        return listOf(
            binding.tieMobileNo to "Please enter mobile number"
        ).all { validateField(it.first, it.second) }
    }

    private fun validateField(view: TextInputEditText?, errorMsg: String): Boolean {
        return if (view?.text.isNullOrEmpty()) {
            view?.error = errorMsg
            view?.requestFocus()
            false
        } else {
            true
        }
    }
}