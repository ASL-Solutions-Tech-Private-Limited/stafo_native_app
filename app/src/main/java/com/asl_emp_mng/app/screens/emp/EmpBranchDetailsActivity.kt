package com.asl_emp_mng.app.screens.emp

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.asl_emp_mng.app.databinding.ActivityEmpBranchDetailsBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.GeoLocationHistResquest
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeBranchId
import com.asl_emp_mng.app.utils.getEmployeeComId
import com.mmi.util.GeoPoint

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)

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