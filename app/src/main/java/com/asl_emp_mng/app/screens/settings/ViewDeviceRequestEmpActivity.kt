package com.asl_emp_mng.app.screens.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.AdapterDeviceRequest
import com.asl_emp_mng.app.base.adapter.EmpListAdapter
import com.asl_emp_mng.app.base.adapter.RadioShiftAdapter
import com.asl_emp_mng.app.databinding.ActivityViewDeviceRequestEmpBinding
import com.asl_emp_mng.app.screens.settings.dataClass.AssignShiftRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ChangeDeviceRequest
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyAcceptDeviceRequest
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyViewRequestDevice
import com.asl_emp_mng.app.screens.settings.dataClass.DeviceRequest
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmployee
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeComId
import com.google.android.material.bottomsheet.BottomSheetDialog

class ViewDeviceRequestEmpActivity : AppCompatActivity() {
    private lateinit var binding:ActivityViewDeviceRequestEmpBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: AdapterDeviceRequest

    private var empList: List<DeviceRequest> = listOf()
    private var filteredList: List<DeviceRequest> = listOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityViewDeviceRequestEmpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)



        setOnClickEvents()
        observeViewModel()
        setupSearchListener()


    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetCompanyViewRequestDeviceResponse.observe(this) {
              if (it.success){
                  if (it.data.isNotEmpty()){

                      binding.etDirSearch.isFocusable = true
                      binding.etDirSearch.isFocusableInTouchMode = true

                      empList=it.data
                      filteredList=empList

                      val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                      binding.rvRequestDeviceList.setLayoutManager(layoutManager)
                      rvAdapter=AdapterDeviceRequest(empList,this)
                      binding.rvRequestDeviceList.adapter = rvAdapter
                      rvAdapter.notifyDataSetChanged()

                  }else{
                      binding.etDirSearch.isFocusable = false
                      binding.etDirSearch.isFocusableInTouchMode = false
                      binding.txtMsg.visibility = View.VISIBLE
                  }
              }else{
                  binding.etDirSearch.isFocusable = false
                  binding.etDirSearch.isFocusableInTouchMode = false
                  binding.txtMsg.visibility = View.VISIBLE
              }

        }

        settingsViewModel.mChangeDeviceResponse.observe(this) {
           if (it.success){
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
       getEmployeeComId()?.let {
           val request= CompanyViewRequestDevice(
                company_id = it.toInt()
            )
           settingsViewModel.companyAcceptRequestDeviceChange(this@ViewDeviceRequestEmpActivity,request)
        }





        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            getEmployeeComId()?.let {
                val request= CompanyViewRequestDevice(
                    company_id = it.toInt()
                )
                settingsViewModel.companyAcceptRequestDeviceChange(this@ViewDeviceRequestEmpActivity,request)
            }

        }

        binding.imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }



    }





    private fun setupSearchListener() {
        binding.etDirSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            empList
        } else {
            empList.filter {
                it.name.contains(query, ignoreCase = true)||it.phone.contains(query,ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }


    fun acceptDeviceRequest(empId:Int,status:String,deviceID:String){
        val request=CompanyAcceptDeviceRequest(
            employee_id =empId,
            status=status,
            device_id = deviceID
        )
        settingsViewModel.companyAcceptRequestDeviceChange(this@ViewDeviceRequestEmpActivity, request)
    }
}