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
import com.asl_emp_mng.app.base.adapter.AdapterPolicy
import com.asl_emp_mng.app.base.adapter.AdapterViewEmpDocument
import com.asl_emp_mng.app.databinding.ActivityEmployeeViewDocumentBinding
import com.asl_emp_mng.app.databinding.ActivityPolicyBinding
import com.asl_emp_mng.app.screens.settings.AddPolicyActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeViewDocumentRequest
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeComId
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getIsCOMPANYLogin

class EmployeeViewDocumentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeViewDocumentBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: AdapterViewEmpDocument

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityEmployeeViewDocumentBinding.inflate(layoutInflater)
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

    private fun onClickListener() {
        binding?.apply {

            val request= EmployeeViewDocumentRequest(
                employee_id = getEmployeeDetails()?.id.toString()
            )
            settingsViewModel.viewEmployeeDocuments(this@EmployeeViewDocumentActivity, request)

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                val request= EmployeeViewDocumentRequest(
                    employee_id = getEmployeeDetails()?.id.toString()
                )
                settingsViewModel.viewEmployeeDocuments(this@EmployeeViewDocumentActivity, request)
            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mEmployeeViewDocumentResponse.observe(this) {

            if (it.data.isNotEmpty()){
               binding.layoutNotView.visibility = View.GONE

                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvDocList.setLayoutManager(layoutManager)
                rvAdapter = AdapterViewEmpDocument(it.data, this)
                binding.rvDocList.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()
            }else{
                binding.layoutNotView.visibility = View.VISIBLE
                //CustomToast(this,it.message)
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