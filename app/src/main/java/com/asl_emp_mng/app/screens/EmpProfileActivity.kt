package com.asl_emp_mng.app.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.base.model.ProfileType
import com.asl_emp_mng.app.databinding.ActivityEmpProfileBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeComId
import com.asl_emp_mng.app.utils.getEmployeeDetails
import java.text.SimpleDateFormat
import java.util.Locale

class EmpProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpProfileBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onClickListener()
        observeViewModel()

    }

    private fun onClickListener() {
        binding?.apply {




            val profileType = ProfileType.valueOf(
                intent.getStringExtra("PROFILE_TYPE") ?: ProfileType.BASIC.name
            )

            if (profileType == ProfileType.BASIC) {
                binding.rlBasicDetails.visibility = View.VISIBLE

            } else if (profileType == ProfileType.EDUCATION) {
                binding.rlEducationDetails.visibility = View.VISIBLE

            } else if (profileType == ProfileType.DOCUMENT) {
                binding.rlDocumentDetails.visibility = View.VISIBLE
            } else {
                binding.nsvCompanyDetail.visibility = View.VISIBLE
            }



            imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            settingsViewModel.fetchEmployeeDetails(this@EmpProfileActivity, getEmployeeDetails()?.id.toString())

        }
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        getEmployeeComId()?.let { settingsViewModel.getBranchList(this, it) }




        settingsViewModel.mFetchEmployeeDetailsResponse.observe(this) {

            if (it.status) {
                it.data?.let { data ->

                    Log.d("res","data: ${it.data}")

                    // Extension function to handle null or empty values
                    fun String?.orDash(): String = if (this.isNullOrEmpty()) "-" else this

                   // Basic details
                    binding.txtName.text = data.name.orDash()
                    binding.txtMobile.text = data.phone.orDash()
                    binding.txtEmail.text = data.email.orDash()
                    binding.txtAddress.text = data.address.orDash()
                    binding.txtDob.text = formatDate(data.dateOfBirth.orDash())

                   // Professional details
                    binding.txtCComName.text = data.companyId?.toString().orDash()
                    binding.txtCComBranch.text = data.branchId?.toString().orDash()
                    binding.txtCComDepartment.text = data.departmentId?.toString().orDash()
                    binding.txtCComEmpType.text = data.employeeTypeId?.toString().orDash()
                    binding.txtCComJoining.text = formatDate(data.dateOfJoining.orDash())
                    binding.txtCComLeaving.text =formatDate(data.dateOfLeaving.orDash())
                    binding.txtCComEmpId.text = data.empId.orDash()
                    binding.txtCComJobTitle.text = data.position.orDash()
                    binding.txtCComPfNo.text = data.pfNumber?.toString().orDash()
                    binding.txtCComOfficialEmail.text = data.email.orDash()

                     // Last company details
                    binding.txtLComName.text = data.companyId?.toString().orDash()
                    binding.txtLComBranch.text = data.branchId?.toString().orDash()
                    binding.txtLComDepartment.text = data.departmentId?.toString().orDash()
                    binding.txtLComEmpType.text = data.employeeTypeId?.toString().orDash()
                    binding.txtLComJoining.text = formatDate(data.dateOfJoining.orDash())
                    binding.txtLComLeaving.text = formatDate(data.dateOfLeaving.orDash())
                    binding.txtLComEmpId.text = data.empId.orDash()
                    binding.txtLComJobTitle.text = data.position.orDash()
                    binding.txtLComPfNo.text = data.pfNumber?.toString().orDash()
                    binding.txtLComOfficialEmail.text = data.email.orDash()





                    /*    //basic details
                        binding.txtName.text=data.name
                        binding.txtMobile.text=data.phone
                        binding.txtEmail.text=data.email
                        binding.txtAddress.text=data.address
                        binding.txtDob.text=data.dateOfBirth

                        //professional details
                        binding.txtCComName.text= data.companyId.toString()
                        binding.txtCComBranch.text= data.branchId.toString()
                        binding.txtCComDepartment.text= data.departmentId.toString()
                        binding.txtCComEmpType.text= data.employeeTypeId.toString()
                        binding.txtCComJoining.text= data.dateOfJoining.toString()
                        binding.txtCComLeaving.text= data.dateOfLeaving.toString()
                        binding.txtCComEmpId.text= data.empId
                        binding.txtCComJobTitle.text= data.position.toString()
                        binding.txtCComPfNo.text= data.pfNumber.toString()
                        binding.txtCComOfficialEmail.text= data.email

                        //last company
                        binding.txtLComName.text= data.companyId.toString()
                        binding.txtLComBranch.text= data.branchId.toString()
                        binding.txtLComDepartment.text= data.departmentId.toString()
                        binding.txtLComEmpType.text= data.employeeTypeId.toString()
                        binding.txtLComJoining.text= data.dateOfJoining.toString()
                        binding.txtLComLeaving.text= data.dateOfLeaving.toString()
                        binding.txtLComEmpId.text= data.empId
                        binding.txtLComJobTitle.text= data.position.toString()
                        binding.txtLComPfNo.text= data.pfNumber.toString()
                        binding.txtLComOfficialEmail.text= data.email*/




                }
            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mmUpdateEmployeeProfileResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
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

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MMM/yy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}