package com.asl_emp_mng.app.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.BaseViewModel
import com.asl_emp_mng.app.base.model.CompanyInfo
import com.asl_emp_mng.app.base.model.OwnerInfo
import com.asl_emp_mng.app.databinding.ActivityOtpVerifyBinding
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.screens.ui.EmployeeAttendance
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast

class OtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerifyBinding


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

        onClickListener()
    }

    private fun addTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }

            override fun afterTextChanged(s: Editable?) {


                when (editText.id) {
                    R.id.otp_edit_text1 -> {
                        if (editText.length() == 1) binding.otpEditText2.requestFocus()
                    }

                    R.id.otp_edit_text2 -> {
                        if (editText.length() == 1) binding.otpEditText3.requestFocus()
                        else if (editText.text.isNullOrEmpty()) binding.otpEditText1.requestFocus()
                    }

                    R.id.otp_edit_text3 -> {
                        if (editText.length() == 1) binding.otpEditText4.requestFocus()
                        else if (editText.text.isNullOrEmpty()) binding.otpEditText2.requestFocus()
                    }

                    R.id.otp_edit_text4 -> {
                        if (editText.length() == 1) {
                            val inputManager =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            currentFocus?.let {
                                inputManager.hideSoftInputFromWindow(
                                    it.windowToken,
                                    InputMethodManager.HIDE_NOT_ALWAYS
                                )
                            }
                        } else if (editText.text.isNullOrEmpty()) {
                            binding.otpEditText3.requestFocus()
                        }
                    }
                }
            }
        })
    }

    private fun onClickListener() {

        val editTexts = listOf(
            binding.otpEditText1,
            binding.otpEditText2,
            binding.otpEditText3,
            binding.otpEditText4
        )

        for (editText in editTexts) {
            addTextWatcher(editText)
            setFocusChangeListener(editText)
        }

        startTimer()






        binding?.apply {


            btnOtpVerify.setOnClickListener {
                if (isValidate()) {

                    String

                    val userOTP = binding.otpEditText1.text.toString()
                        .trim() + binding.otpEditText2.text.toString()
                        .trim() + binding.otpEditText3.text.toString()
                        .trim() + binding.otpEditText4.text.toString()


                    verifyUserOtp(userOTP)

                } else {
                    Toast.makeText(
                        this@OtpVerifyActivity,
                        "Your entire otp is incorrect or empty!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            ivBack.setOnClickListener { _ ->
                finish()
                // startActivity(Intent(this@OtpVerifyActivity, EmployeeAttendance::class.java))
            }

            llResendCode.setOnClickListener {

            }


        }
    }


    private fun observeViewModel() {

        authViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        val companyInfo = intent.getSerializableExtra("companyInfo") as? CompanyInfo
        val ownerInfo = intent.getSerializableExtra("ownerInfo") as? OwnerInfo

      /*  val companyInfo = CompanyInfo(
            company_name = "ABCrt23",
            company_type = "2",
            business_type = "2",
            registration_number = "ACJD1231AS45f",
            gst_number = "3812612684",
            pan_number = "ASJDO1231A4",
            mobile_no = "8746439862",
            email = "demo23@abc.com",
            password = "12345689",
            password_confirmation="12345689",
            country = "87",
            state = "1",
            city = "1",
            address = "Kolkata",
            pin = "785412"
        )

        val ownerInfo = OwnerInfo(
            first_name = "Islam ",
            last_name = "hamid ",
            email = "nikhilshaw25s234@gmail.com",
            mobile = "9876523880"

        )*/






        if (companyInfo != null && ownerInfo !=null) {
            authViewModel.registerUser(this, companyInfo, ownerInfo)
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

    private fun isValidate(): Boolean {
        binding?.apply {
            if (otpEditText1.text.isNullOrEmpty()) {
                return false
            } else if (otpEditText2.text.isNullOrEmpty()) {
                return false
            } else if (otpEditText3.text.isNullOrEmpty()) {
                return false
            } else if (otpEditText4.text.isNullOrEmpty()) {
                return false
            }
        }
        return true
    }

    private fun verifyUserOtp(otp: String) {


        observeViewModel()


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