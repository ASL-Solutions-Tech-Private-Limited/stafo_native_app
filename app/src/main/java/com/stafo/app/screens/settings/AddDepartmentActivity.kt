package com.stafo.app.screens.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityAddDepartmentBinding
import com.stafo.app.screens.settings.dataClass.DepartmentCreateRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class AddDepartmentActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddDepartmentBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAddDepartmentBinding.inflate(layoutInflater)
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

        settingsViewModel.mDepartmentCreateResponse.observe(this) {

            if (it.success){
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
            }else{
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


            btnAddDepartment.setOnClickListener {
                if (isValidate()) {
                    val request =  DepartmentCreateRequest(
                        name = tieDepartmentName.text.toString(),
                        description =tieDescription.text.toString()
                    )

                    settingsViewModel.createDepartment(this@AddDepartmentActivity, request)

                }
            }


            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }
    }

    private fun isValidate(): Boolean {
        binding.apply {
            if (tieDepartmentName.text.isNullOrEmpty()) {
                tieDepartmentName.error = "Please enter department name"
                tieDepartmentName.requestFocus()
                return false
            }
        }
        return true
    }
}