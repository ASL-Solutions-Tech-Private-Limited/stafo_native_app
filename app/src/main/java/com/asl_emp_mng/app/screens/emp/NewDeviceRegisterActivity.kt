package com.asl_emp_mng.app.screens.emp

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.BranchAdapter
import com.asl_emp_mng.app.databinding.ActivityNewDeviceRegisterBinding
import com.asl_emp_mng.app.screens.settings.AddBranchActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeComId

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)
        deviceID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        setOnClickEvents()
        observeViewModel()

    }

    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        settingsViewModel.mViewBranchResponse.observe(this) {

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
            getEmployeeComId()?.let {
                settingsViewModel.getViewBranchList(
                    this@NewDeviceRegisterActivity,
                    it
                )
            }
        }

    }
}