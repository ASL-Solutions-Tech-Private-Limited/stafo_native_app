package com.stafo.app.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.stafo.app.R
import com.stafo.app.base.model.CompanyInfo
import com.stafo.app.base.model.OwnerInfo
import com.stafo.app.databinding.ActivitySignUpBinding
import com.stafo.app.screens.auth.dataClass.DataBusinessType
import com.stafo.app.screens.auth.dataClass.DataCompanyType
import com.stafo.app.screens.dashboard.EmployerDashboard
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.setCompanyDetails
import com.stafo.app.utils.setIsCOMPANYLogin
import com.stafo.app.utils.setUserAccessToken
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
    private lateinit var mobile: String
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        mobile = intent.extras?.getString("mobile") ?: ""

        binding.tieCompanyMobile.setText(mobile)

        observeViewModel()
        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        binding.apply {
            btnNext.setOnClickListener {
                if (validateBasicInfo()) {
                    val companyInfo = CompanyInfo(
                        company_name = tieCompanyName.text.toString().trim(),
                        company_type = userSelectCTypeId,
                        business_type = userSelectBTypeId,
                        registration_number = "",
                        gst_number = "",
                        pan_number = "",
                        mobile_no = tieCompanyMobile.text.toString().trim(),
                        email = tieCompanyEmail.text.toString().trim(),
                        password = "",
                        password_confirmation = "",
                        country = "",
                        state = "",
                        city = "",
                        address = "",
                        pin = ""
                    )
                    val ownerInfo = OwnerInfo(
                        first_name = tieOwnerName.text.toString().trim(),
                        last_name = "",
                        email = "",
                        mobile = tieCompanyMobile.text.toString().trim()

                    )
                    authViewModel.registerUser(this@SignUpActivity,companyInfo,ownerInfo)
                }
            }


            tieCompanyType.setOnClickListener { companyTypeDialog.show() }
            tieBusinessType.setOnClickListener { businessTypeDialog.show() }

           /* tieCompanyEmail.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) { // When user clicks outside
                    if (tieCompanyEmail.text.toString().trim().isNotEmpty()) {
                        tieCompanyEmail.clearFocus()
                    }
                }
            }*/

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

                setUserAccessToken(it.token)
                setIsCOMPANYLogin(true)
                it.company?.let { it1 -> setCompanyDetails(it1) }
               // saveToken(this, "token", it?.token ?: "")
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
                    binding.tieCompanyEmail.clearFocus()
                } else if (title == "Business Type"){
                    userSelectBTypeId = searchListItem.id.toString()
                    binding.tieCompanyEmail.clearFocus()
                }

                dialog.dismiss()
            }
        })

        when (title) {
            "Company Type" -> companyTypeDialog = dialog
            "Business Type" -> businessTypeDialog = dialog
        }
    }

    private fun saveToken(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }
}