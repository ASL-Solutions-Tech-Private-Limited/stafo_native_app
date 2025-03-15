package com.stafo.app.screens.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.stafo.app.base.adapter.AdapterDeviceRequest
import com.stafo.app.databinding.ActivityViewDeviceRequestEmpBinding
import com.stafo.app.screens.settings.dataClass.CompanyAcceptDeviceRequest
import com.stafo.app.screens.settings.dataClass.CompanyViewRequestDevice
import com.stafo.app.screens.settings.dataClass.DeviceRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

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