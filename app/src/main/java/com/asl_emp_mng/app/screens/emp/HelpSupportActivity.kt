package com.asl_emp_mng.app.screens.emp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.ProfileType
import com.asl_emp_mng.app.databinding.ActivityHelpSupportBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getCompanyDetails
import com.asl_emp_mng.app.utils.getEmployeeComId
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getIsCOMPANYLogin

class HelpSupportActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHelpSupportBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHelpSupportBinding.inflate(layoutInflater)
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

    private fun onClickListener() {
        binding?.apply {
            if (getIsCOMPANYLogin() == true) {

                rtlCompany.visibility=View.VISIBLE
                rtlEmployee.visibility=View.GONE


            } else {
                rtlCompany.visibility=View.GONE
                rtlEmployee.visibility=View.VISIBLE


                settingsViewModel.fetchEmployeeDetails(this@HelpSupportActivity, getEmployeeDetails()?.id.toString())




            }



            imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            tvContactLink.setOnClickListener {
                val url = "http://stafo.in/about-us"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }


        }
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }


        settingsViewModel.mFetchEmployeeDetailsResponse.observe(this) {

            if (it.status) {
                it.data?.let { data ->

                    Log.d("res","data: ${it.data}")

                    fun String?.orDash(): String = if (this.isNullOrEmpty()) "-" else this

                    binding.tvCompanyName.text = it.companyName.orDash()










                }
            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mmUpdateEmployeeProfileResponse.observe(this) {

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
}