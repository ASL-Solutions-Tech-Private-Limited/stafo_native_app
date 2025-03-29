package com.stafo.app.screens.emp

import android.os.Bundle
import android.util.Log
import android.view.View
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
                    binding.rlEmp.visibility= View.VISIBLE
                    binding.txtStatusBranch.visibility= View.GONE

                    binding.txtBranchName.text = branchDetails.branch_name
                    binding.txtBranchAddress.text = branchDetails.branch_address
                } else {
                    binding.rlEmp.visibility= View.GONE
                    binding.txtStatusBranch.visibility= View.VISIBLE

                }



            }else{
                binding.rlEmp.visibility= View.GONE
                binding.txtStatusBranch.visibility= View.VISIBLE
            }



        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        finish()
    }

    private fun onClickListener() {
        binding.apply {


            getEmployeeComId()?.let { settingsViewModel.getViewBranchList(this@EmpBranchDetailsActivity, it) }


            imageBack.setOnClickListener {
                onBackPressed()

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