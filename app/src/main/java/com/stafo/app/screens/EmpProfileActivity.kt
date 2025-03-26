package com.stafo.app.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.base.model.ProfileType
import com.stafo.app.databinding.ActivityEmpProfileBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.DataBranch
import com.stafo.app.screens.settings.dataClass.DataDepartment
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import java.text.SimpleDateFormat
import java.util.Locale

class EmpProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpProfileBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var mDepartmentList: ArrayList<DataDepartment>? = ArrayList()
    private var mBranchList: ArrayList<DataBranch>? = ArrayList()
    private var mJobTitleList: ArrayList<String>? = ArrayList()

    private var selectJobTitle: String = ""
    private var selectBranch: Int = 1
    private var selectDepartment: Int = 1
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

            getEmployeeComId()?.let {
                settingsViewModel.getBranchList(
                    this@EmpProfileActivity,
                    it
                )
            }
            getEmployeeComId()?.let {
                settingsViewModel.getDepartmentList(
                    this@EmpProfileActivity,
                    it
                )
            }


            settingsViewModel.getJobTitleList(this@EmpProfileActivity)


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


        settingsViewModel.mFetchEmployeeDetailsResponse.observe(this) {

            if (it.status) {
                it.data?.let { data ->

                    Log.d("res","data: ${it.data}")

                    fun String?.orDash(): String = if (this.isNullOrEmpty()) "-" else this

                   // Basic details
                    binding.txtName.text = data.name.orDash()
                    binding.txtMobile.text = data.phone.orDash()
                    binding.txtEmail.text = data.email.orDash()
                    binding.txtAddress.text = data.address.orDash()
                    binding.txtDob.text = formatDate(data.dateOfBirth.toString())

                   // Professional details
                    binding.txtCComName.text = it.companyName
                    //binding.txtCComEmpType.text = data.employeeTypeId?.toString().orDash()

                    binding.txtCComEmpId.text = data.empId.orDash()
                    binding.txtCComPfNo.text = data.pfNumber?.orDash()
                    binding.txtCComOfficialEmail.text = data.email.orDash()

                    selectBranch = data.branchId


                    if (selectBranch != null) {
                        val index = mBranchList?.indexOfFirst { it.id == selectBranch }
                        if (index != -1) {
                            binding.txtCComBranch.setText(index?.let { it1 -> mBranchList?.get(it1)?.branch_name })
                            binding.txtLComBranch.setText(index?.let { it1 -> mBranchList?.get(it1)?.branch_name })
                        }
                    }


                    selectDepartment = data.departmentId

                    if (selectDepartment != null) {
                        val index = mDepartmentList?.indexOfFirst { it.id == selectDepartment }
                        if (index != -1) {
                            binding.txtCComDepartment.setText(index?.let { it1 -> mDepartmentList?.get(it1)?.name })
                            binding.txtLComDepartment.setText(index?.let { it1 -> mDepartmentList?.get(it1)?.name })
                        }
                    }





                    val position = data.position ?: ""
                    selectJobTitle = position

                    if (position.isNotEmpty() && mJobTitleList != null) {
                        val index = mJobTitleList!!.indexOf(position)
                        if (index != -1) {
                            binding.txtCComJobTitle.setText(mJobTitleList!![index])
                            binding.txtLComJobTitle.setText(mJobTitleList!![index])
                        }
                    }


                     // Last company details
                    binding.txtLComName.text = it.companyName
                   // binding.txtLComEmpType.text = data.employeeTypeId?.toString().orDash()


                    binding.txtCComJoining.text =formatDate(data.dateOfJoining.toString())
                    binding.txtLComJoining.text =formatDate(data.dateOfJoining.toString())

                    binding.txtCComLeaving.text =formatDate(data.dateOfLeaving.toString())
                    binding.txtLComLeaving.text =formatDate(data.dateOfLeaving.toString())

                    binding.txtLComEmpId.text = data.empId.orDash()
                    binding.txtLComPfNo.text = data.pfNumber?.orDash()
                    binding.txtLComOfficialEmail.text = data.email.orDash()








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


        settingsViewModel.mJobTitleResponse.observe(this) { response ->
            if (response.status) {
                mJobTitleList = ArrayList(response.data.map { it.name })
            }
        }

        settingsViewModel.mBranchListResponse.observe(this) {
            mBranchList = it.data

        }

        settingsViewModel.mDepartmentListResponse.observe(this) {
            mDepartmentList = it.data
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
        if (inputDate.isNullOrEmpty()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}