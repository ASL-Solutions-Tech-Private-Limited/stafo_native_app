package com.stafo.app.screens.emp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityNewDeviceRegisterBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.ChangeDeviceRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeDetails

class NewDeviceRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewDeviceRegisterBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var deviceID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewDeviceRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        deviceID = intent.extras?.getString("deviceId") ?: ""

        setOnClickEvents()
        observeViewModel()

    }

    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        settingsViewModel.mChangeDeviceResponse.observe(this) {

            if (it.success){
                binding?.btnRequestRegister?.isEnabled = false
                binding?.btnRequestRegister?.alpha = .5f
                binding?.btnRequestRegister?.text = "Pending"
                binding?.txtAdmin?.text = "Your request is pending"
                CustomToast(this,it.message)
            }else{
                CustomToast(this,it.message)
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

    private fun setOnClickEvents() {

        binding.btnRequestRegister.setOnClickListener {

            val request= ChangeDeviceRequest(
                employee_id = getEmployeeDetails()?.id.toString(),
                status = "pending"
            )
            settingsViewModel.requestDeviceChange(this@NewDeviceRegisterActivity, request)
        }

    }
}