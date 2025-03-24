package com.stafo.app.screens.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityFeedbackBinding
import com.stafo.app.screens.settings.dataClass.SendFeedbackRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import com.google.android.material.textfield.TextInputEditText

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()
    }

    private fun observeViewModel() {

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mSendFeedbackResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
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

    private fun onClickListener() {
        binding.apply {


            binding.imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }




            btnSendFeedback.setOnClickListener {

                if (isValidate()) {

                    if (getIsCOMPANYLogin(this@FeedbackActivity) == true){
                        val request= SendFeedbackRequest(
                            company_id = getEmployeeComId().toString(),
                            message = tieFeedback.text.toString()
                        )
                        settingsViewModel.postFeedback(this@FeedbackActivity, request)

                    }else{
                        val request= SendFeedbackRequest(
                            employee_id = getEmployeeDetails()?.id.toString(),
                            message = tieFeedback.text.toString()
                        )
                        settingsViewModel.postFeedback(this@FeedbackActivity, request)
                    }

                }


            }


        }
    }


    private fun isValidate(): Boolean {
        return listOf(
            binding.tieFeedback to "Please enter  your feedback",
        ).all { validateField3(it.first, it.second) }
    }


    private fun validateField3(view: TextInputEditText?, errorMsg: String): Boolean {
        return if (view?.text.isNullOrEmpty()) {
            CustomToast(this, errorMsg)
            false
        } else {
            true
        }
    }
}