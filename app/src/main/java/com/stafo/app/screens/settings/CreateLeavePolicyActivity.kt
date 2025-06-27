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
import com.stafo.app.base.request.AddBranchRequest
import com.stafo.app.databinding.ActivityCreateLeavePolicyBinding
import com.stafo.app.screens.settings.dataClass.BranchItem
import com.stafo.app.screens.settings.dataClass.CreateHolidayRequest
import com.stafo.app.screens.settings.dataClass.CreateLeavePolicyRequest
import com.stafo.app.screens.settings.dataClass.HolidayPostData
import com.stafo.app.screens.settings.dataClass.LeaveItem
import com.stafo.app.screens.settings.dataClass.LeavePolicyPostData
import com.stafo.app.screens.settings.dataClass.LeaveTypeRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class CreateLeavePolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateLeavePolicyBinding
    private lateinit var adapter: DynamicAdapter
    private val dynamicFields = mutableListOf<DynamicField>()

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var actionType:String=""
    private var leaveTypeId:Int=0
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


        onClickListener()
        observeViewModel()
    }

    private fun onClickListener() {
        binding.apply {
            actionType = intent.getStringExtra("leave_type")?:""
            val leaveData = intent.getSerializableExtra("leave_data") as? LeaveItem
            if (actionType=="Edit"){
                leaveData?.let {
                    leaveTypeId=it.id
                    tvRegistrationTitle.text="Update Leave Policy"


                    tieBranchName.setText(it.name)
                    tieBranchRadius.setText(it.no_of_days.toString()?:"")
                    tieBranchAddress.setText(it.description ?: "")
                    checkBox.isChecked=if (it.is_paid==1) true else false

                }
            }

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnAddBranch.setOnClickListener {
                if (isValidate()) {

                    val noDays= binding.tieBranchRadius.text.toString().trim()
                    val isPaid = if (binding.checkBox.isChecked) "1" else "0"

                    if (actionType=="Edit"){

                        val request =  LeaveTypeRequest(
                            name = binding.tieBranchName.text.toString(),
                            no_of_days = noDays.toInt(),
                            description = binding.tieBranchAddress.text.toString(),
                            is_paid = isPaid,

                            )

                        Log.e("leave", "request leave type update  : ${request}")
                        settingsViewModel.updateLeaveType(this@CreateLeavePolicyActivity, leaveTypeId,request)




                    }else{
                        getEmployeeComId()?.let { it1 ->
                            val request =  LeaveTypeRequest(
                                company_id = it1.toInt(),
                                name = binding.tieBranchName.text.toString(),
                                no_of_days = noDays.toInt(),
                                description = binding.tieBranchAddress.text.toString(),
                                is_paid = isPaid,

                            )

                            Log.e("leave", "request leave type : ${request}")
                            settingsViewModel.createLeaveType(this@CreateLeavePolicyActivity, request)

                        }
                    }



                }
            }
        }
    }


    /*  private fun setupRecyclerView() {
          adapter = DynamicAdapter(dynamicFields)
          binding.recyclerView.layoutManager = LinearLayoutManager(this)
          binding.recyclerView.adapter = adapter
      }*/

    /*  private fun onClickListener() {
          binding?.apply {


              llcAddMore.setOnClickListener {
                  addDynamicField()
              }



              ivBack.setOnClickListener {
                  onBackPressedDispatcher.onBackPressed()
              }

             *//* btnAddPolicy.setOnClickListener {

              *//**//*  if (dynamicFields.size>0){
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
                }*//**//*




            }*//*


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
    }*/

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mLeaveTypeCreateResponse.observe(this) {


            if (it.success) {
                CustomToast(this, it.message)
               onBackPressed()

            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mLeaveTypeUpdateResponse.observe(this) {


            if (it.success) {
                CustomToast(this, it.message)
                onBackPressed()

            } else {
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        finish()
    }

    private fun isValidate(): Boolean {
        binding.apply {
            if (tieBranchName.text.isNullOrEmpty()) {
                tieBranchName.error = "Please enter leave type name"
                tieBranchName.requestFocus()
                return false
            } else if (tieBranchRadius.text.isNullOrEmpty()) {
                tieBranchRadius.error = "Please enter no of days"
                tieBranchRadius.requestFocus()
                return false
            }
        }
        return true
    }
}