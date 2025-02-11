package com.asl_emp_mng.app.screens.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.CompanyInfo
import com.asl_emp_mng.app.base.model.OwnerInfo
import com.asl_emp_mng.app.databinding.ActivityOtpVerifyBinding
import com.asl_emp_mng.app.databinding.ActivitySignUpBinding
import com.asl_emp_mng.app.screens.auth.dataClass.DataBusinessType
import com.asl_emp_mng.app.screens.auth.dataClass.DataCity
import com.asl_emp_mng.app.screens.auth.dataClass.DataCompanyType
import com.asl_emp_mng.app.screens.auth.dataClass.DataCountry
import com.asl_emp_mng.app.screens.auth.dataClass.DataStates
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.google.android.material.textfield.TextInputEditText

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val authViewModel: AuthViewModel by viewModels()

    private var mCompanyTypeList: ArrayList<DataCompanyType>? = ArrayList()
    private var mBusinessTypeList: ArrayList<DataBusinessType>? = ArrayList()

    private lateinit var companyTypeDialog: SearchableDialog
    private lateinit var businessTypeDialog: SearchableDialog
    private lateinit var userSelectCTypeId: String
    private lateinit var userSelectBTypeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeViewModel()
        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        binding?.apply {

            btnNext.setOnClickListener {
                if (validateBasicInfo()) {
                    Log.d("res", "c:$userSelectCTypeId b:$userSelectBTypeId")

                    val companyInfo = CompanyInfo(
                        company_name = tieCompanyName.text.toString(),
                        company_type = userSelectCTypeId,
                        business_type = userSelectBTypeId,
                        registration_number = "",
                        gst_number = "",
                        pan_number = "",
                        mobile_no = "",
                        email = "",
                        password = "",
                        password_confirmation = "",
                        country = "",
                        state = "",
                        city = "",
                        address = "",
                        pin = ""
                    )

                    val ownerInfo = OwnerInfo(
                        first_name = tieOwnerName.text.toString(),
                        last_name = "",
                        email = "",
                        mobile = ""

                    )

                    authViewModel.registerUser(this@SignUpActivity,companyInfo,ownerInfo)


                }
            }


            tieCompanyType.setOnClickListener { companyTypeDialog.show() }
            tieBusinessType.setOnClickListener { businessTypeDialog.show() }

        }
    }

    private fun validateBasicInfo(): Boolean {
        return listOf(
            binding.tieCompanyName to "Please enter company name",
            binding.tieOwnerName to "Please enter owner name",
            binding.tieCompanyType to "Please enter company type",
            binding.tieBusinessType to "Please enter business type"
        ).all { validateField(it.first, it.second) }
    }

    private fun validateField(view: TextInputEditText?, errorMsg: String): Boolean {
        return if (view?.text.isNullOrEmpty()) {
            view?.error = errorMsg
            view?.requestFocus()
            false
        } else {
            true
        }
    }

    private fun observeViewModel() {
        authViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        authViewModel.getCompanyType(this)
        authViewModel.mCompanyTypeResponse.observe(this) {
            if (it.success) {
                mCompanyTypeList = it.data
                binding?.let { it1 ->
                    setupSearchableDialog(
                        mCompanyTypeList,
                        "Company Type",
                        it1.tieCompanyType
                    )
                }
            }
        }


        authViewModel.getBusinessType(this)

        authViewModel.mBusinessTypeResponse.observe(this) {
            if (it.success) {
                mBusinessTypeList = it.data
                binding?.let { it1 ->
                    setupSearchableDialog(
                        mBusinessTypeList,
                        "Business Type",
                        it1.tieBusinessType
                    )
                }
            }
        }
        authViewModel.mRegisterResponse.observe(this) {
            if (it.success) {
                CustomToast(this, it.message)
                startActivity(Intent(this@SignUpActivity, EmployerDashboard::class.java))
            }else{

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

    private fun setupSearchableDialog(
        dataList: List<Any>?,
        title: String,
        field: TextInputEditText
    ) {
        val items = dataList?.map {
            val name = when (it) {
                is DataCompanyType -> it.company_name
                is DataBusinessType -> it.business_name
                else -> "Unknown"
            }

            val id = when (it) {
                is DataCompanyType -> it.id
                is DataBusinessType -> it.id
                else -> -1
            }

            SearchListItem(id, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)
                if (title == "Company Type") {
                    userSelectCTypeId = searchListItem.id.toString()
                } else {
                    userSelectBTypeId = searchListItem.id.toString()
                }

                dialog.dismiss()
            }
        })

        when (title) {
            "Company Type" -> companyTypeDialog = dialog
            "Business Type" -> businessTypeDialog = dialog
        }
    }
}