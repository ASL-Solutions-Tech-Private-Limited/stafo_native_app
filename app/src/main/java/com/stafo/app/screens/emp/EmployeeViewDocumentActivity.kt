package com.stafo.app.screens.emp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterViewEmpDocument
import com.stafo.app.databinding.ActivityEmployeeViewDocumentBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.EmployeeViewDocumentRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeDetails

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

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