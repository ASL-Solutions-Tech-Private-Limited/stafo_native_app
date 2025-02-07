package com.asl_emp_mng.app.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityOtpVerifyBinding

class OtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerifyBinding
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
              startActivity(Intent(this@OtpVerifyActivity,EmployeeAttendance::class.java))
            }

            llResendCode.setOnClickListener {

            }


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

    }

    private fun startTimer(){
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.llTimer.visibility= View.VISIBLE
                binding.llResendCode.visibility= View.GONE
                binding.tvTimerTime.text=""+millisUntilFinished / 1000
            }
            override fun onFinish() {
                binding.llTimer.visibility= View.GONE
                binding.llResendCode.visibility= View.VISIBLE
            }
        }.start()
    }
}