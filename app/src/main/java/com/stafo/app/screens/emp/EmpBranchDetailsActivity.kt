package com.stafo.app.screens.emp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityEmpBranchDetailsBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeBranchId
import com.stafo.app.utils.getEmployeeComId

class EmpBranchDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpBranchDetailsBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpBranchDetailsBinding.inflate(layoutInflater)
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

        val branchId = getEmployeeBranchId()?.toIntOrNull()
        if (branchId == null) {
            Log.e("Branch", "Invalid Branch ID")
            return
        }

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mViewBranchResponse.observe(this) {

            if (it.data.isNotEmpty()){
                val branchDetails = it.data.find { it.id == branchId }

                if (branchDetails != null) {

                    binding.txtBranchName.text = branchDetails.branch_name
                    binding.txtBranchAddress.text = branchDetails.branch_address
                } else {
                    Log.e("Branch", "No branch found with ID: $branchId")
                }



            }else{

            }



        }

    }


    private fun onClickListener() {
        binding?.apply {


            getEmployeeComId()?.let { settingsViewModel.getViewBranchList(this@EmpBranchDetailsActivity, it) }


            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
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