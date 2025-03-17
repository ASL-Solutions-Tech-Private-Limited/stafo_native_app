package com.stafo.app.screens.settings

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.DynamicAdapter
import com.stafo.app.base.model.DynamicField
import com.stafo.app.databinding.ActivityCreateLeavePolicyBinding
import com.stafo.app.screens.settings.dataClass.CreateHolidayRequest
import com.stafo.app.screens.settings.dataClass.CreateLeavePolicyRequest
import com.stafo.app.screens.settings.dataClass.HolidayPostData
import com.stafo.app.screens.settings.dataClass.LeavePolicyPostData
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class CreateLeavePolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateLeavePolicyBinding
    private lateinit var adapter: DynamicAdapter
    private val dynamicFields = mutableListOf<DynamicField>()

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateLeavePolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setupRecyclerView()
        addDynamicField()
        onClickListener()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = DynamicAdapter(dynamicFields)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun onClickListener() {
        binding?.apply {


            llcAddMore.setOnClickListener {
                addDynamicField()
            }



            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnAddPolicy.setOnClickListener {

                if (dynamicFields.size>0){
                    if (adapter.isValid()) {

                        val allFields = adapter.getAllFields()

                        val leavePolicyList = allFields.map { field ->
                            LeavePolicyPostData(
                                name = field.selectedOption,
                                leaveType =field.userInput
                            )
                        }

                        val request = CreateLeavePolicyRequest(leavePolicyList)

                        Log.d("res","post data: $request")

                        settingsViewModel.createLeavePolicyCompany(this@CreateLeavePolicyActivity,request)





                    } else {
                        CustomToast(this@CreateLeavePolicyActivity, "Please fill blank field!")
                    }
                }else {
                    CustomToast(this@CreateLeavePolicyActivity, "Please add leave policy!")
                }




            }


        }
    }

    private fun addDynamicField() {
        val options = resources.getStringArray(R.array.leave_type).toList()
        val newField = DynamicField("Enter Number of Leave", options)

        if (::adapter.isInitialized) {
            adapter.addField(newField)
        } else {
            adapter = DynamicAdapter(dynamicFields)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mCreateLeavePolicyResponse.observe(this) {


            if (it.status) {
                CustomToast(this,it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()

            } else {
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

}