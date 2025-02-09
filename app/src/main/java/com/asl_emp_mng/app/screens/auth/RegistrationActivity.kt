package com.asl_emp_mng.app.screens.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.BaseActivity
import com.asl_emp_mng.app.databinding.ActivityLoginBinding
import com.asl_emp_mng.app.databinding.ActivityRegistrationBinding
import com.asl_emp_mng.app.screens.auth.dataClass.CompanyTypeResponse
import com.asl_emp_mng.app.screens.auth.dataClass.DataBusinessType
import com.asl_emp_mng.app.screens.auth.dataClass.DataCity
import com.asl_emp_mng.app.screens.auth.dataClass.DataCompanyType
import com.asl_emp_mng.app.screens.auth.dataClass.DataCountry
import com.asl_emp_mng.app.screens.auth.dataClass.DataStates
import com.asl_emp_mng.app.utils.CustomLoader
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText


class RegistrationActivity : BaseActivity<ActivityRegistrationBinding, AuthViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_registration
    override val viewModel: AuthViewModel by lazy { AuthViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    private var mSteps = 1
    private var mCompanyTypeList: ArrayList<DataCompanyType>? = ArrayList()
    private var mCountryList: ArrayList<DataCountry>? = ArrayList()
    private var mStateList: ArrayList<DataStates>? = ArrayList()
    private var mCityList: ArrayList<DataCity>? = ArrayList()

    private lateinit var companyTypeDialog: SearchableDialog
    private lateinit var countryDialog: SearchableDialog
    private lateinit var stateDialog: SearchableDialog
    private lateinit var cityDialog: SearchableDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding?.lifecycleOwner = this
        observeViewModel()
        setupProgressBar()
        setupOnClickListener()
    }

    private fun observeViewModel() {
        viewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        viewModel.getCompanyType(this)
        viewModel.mCompanyTypeResponse.observe(this) {
            if (it.success) {
                mCompanyTypeList = it.data
                viewDataBinding?.let { it1 ->
                    setupSearchableDialog(
                        mCompanyTypeList,
                        "Company Type",
                        it1.tieCompanyType
                    )
                }
            }
        }

        viewModel.getCountryList(this)
        viewModel.mCountryResponse.observe(this) {
            if (it.success) {
                mCountryList = it.data
                viewDataBinding?.let { it1 ->
                    setupSearchableDialog(
                        mCountryList,
                        "Country",
                        it1.tieSelectCountry
                    )
                }
            }
        }

        viewModel.mStateResponse.observe(this) {
            if (it.success) {
                mStateList = it.data
                viewDataBinding?.let { it1 ->
                    setupSearchableDialog(
                        mStateList,
                        "State",
                        it1.tieSelectState
                    )
                }
            }
        }

        viewModel.mCityResponse.observe(this) {
            if (it.success) {
                mCityList = it.data
                viewDataBinding?.let { it1 ->
                    setupSearchableDialog(
                        mCityList,
                        "City",
                        it1.tieSelectCity
                    )
                }
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

    private fun setupProgressBar() {
        viewDataBinding?.rpbBasicInfo?.apply {
            setProgress(100f)
            setUnfilledColor(ContextCompat.getColor(this@RegistrationActivity, R.color.tea_green))
            setFilledColor(ContextCompat.getColor(this@RegistrationActivity, R.color.primaryColor))
        }
    }

    private fun setupOnClickListener() {
        viewDataBinding?.apply {
            btnNext.setOnClickListener { handleNextButtonClick() }
            listOf(
                tieCompanyCertificate to 1101,
                tieCompanyGstCertificate to 1102,
                tieCompanyPanCertificate to 1103,
                tieCompanyAadharCertificate to 1104,
                tieCompanyBankStatement to 1105
            ).forEach { (view, requestCode) ->
                view.setOnClickListener { openPicker(requestCode) }
            }

            tieCompanyType.setOnClickListener { companyTypeDialog.show() }
            tieSelectCountry.setOnClickListener { countryDialog.show() }
            tieSelectState.setOnClickListener { validateAndShowStateDialog() }
            tieSelectCity.setOnClickListener { validateAndShowCityDialog() }
        }
    }

    private fun handleNextButtonClick() {
        when (mSteps) {
            1 -> if (validateBasicInfo()) switchScreen(1)
            2 -> if (isValidOwnerInfo()) switchScreen(2)
        }
    }

    private fun openPicker(req: Int) {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(req)
    }

    private fun switchScreen(flag: Int) {
        val basicInfoVisibility = View.GONE
        val ownerInfoVisibility = View.GONE
        val documentInfoVisibility = View.GONE
        val unfilledColor = ContextCompat.getColor(this, R.color.tea_green)
        val filledColor = ContextCompat.getColor(this, R.color.primaryColor)

        when (flag) {
            1 -> {
                viewDataBinding?.apply {
                    llBasicInfo.visibility = basicInfoVisibility
                    llOwnerInfo.visibility = View.VISIBLE
                    llDocumentinfo.visibility = documentInfoVisibility
                    rpbOwnerInfo.apply {
                        setProgress(100f)
                        setUnfilledColor(unfilledColor)
                        setFilledColor(filledColor)
                    }
                }
            }

            2 -> {
                viewDataBinding?.apply {
                    llBasicInfo.visibility = basicInfoVisibility
                    llOwnerInfo.visibility = ownerInfoVisibility
                    llDocumentinfo.visibility = View.VISIBLE
                    rpbDocumentInfo.apply {
                        setProgress(100f)
                        setUnfilledColor(unfilledColor)
                        setFilledColor(filledColor)
                    }
                }
            }
        }
    }

    private fun validateBasicInfo(): Boolean {
        return listOf(
            viewDataBinding?.tieCompanyName to "Please enter company name",
            viewDataBinding?.tieCompanyType to "Please enter company type",
            viewDataBinding?.tieCompanyRegNo to "Please enter registration number",
            viewDataBinding?.tieCompanyGstNo to "Please enter gst number",
            viewDataBinding?.tieCompanyPanNo to "Please enter pan number",
            viewDataBinding?.tieCompanyAddress to "Please enter address"
        ).all { validateField(it.first, it.second) }
    }

    private fun isValidOwnerInfo(): Boolean {
        return listOf(
            viewDataBinding?.tieOwnerName to "Please enter owner name",
            viewDataBinding?.tieOwnerMobileNo to "Please enter mobile",
            viewDataBinding?.tieOwnerEmail to "Please enter email",
            viewDataBinding?.tieOwnerAadhar to "Please enter aadhaar no",
            viewDataBinding?.tieOwnerPan to "Please enter pan no",
            viewDataBinding?.tieOwnerAddress to "Please enter address"
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

    private fun setupSearchableDialog(
        dataList: List<Any>?,
        title: String,
        field: TextInputEditText
    ) {
        val items = dataList?.map {
            val name = when (it) {
                is DataCompanyType -> it.company_name
                is DataCountry -> it.name
                is DataStates -> it.name
                is DataCity -> it.name
                else -> 0
            }
            SearchListItem(it, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)
                dialog.dismiss()

                // Handle special cases for country and state selection
                when (field) {
                    viewDataBinding?.tieSelectCountry -> viewModel.getStateList(
                        this@RegistrationActivity,
                        searchListItem.id.toString()
                    )

                    viewDataBinding?.tieSelectState -> viewModel.getCityList(
                        this@RegistrationActivity,
                        searchListItem.id.toString()
                    )
                }
            }
        })
        when (title) {
            "Company Type" -> companyTypeDialog = dialog
            "Country" -> countryDialog = dialog
            "State" -> stateDialog = dialog
            "City" -> cityDialog = dialog
        }
    }

    private fun validateAndShowStateDialog() {
        if (viewDataBinding?.tieSelectCountry?.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please select country first", Toast.LENGTH_SHORT).show()
        } else {
            stateDialog.show()
        }
    }

    private fun validateAndShowCityDialog() {
        if (viewDataBinding?.tieSelectState?.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please select state first", Toast.LENGTH_SHORT).show()
        } else {
            cityDialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val targetField = when (requestCode) {
                    1101 -> viewDataBinding?.tilCompanyCertificate
                    1102 -> viewDataBinding?.tilCompanyGstCertificate
                    1103 -> viewDataBinding?.tilCompanyPanCertificate
                    1104 -> viewDataBinding?.tilCompanyAddharCertificate
                    1105 -> viewDataBinding?.tilCompanyBankStatement
                    else -> null
                }
                targetField?.editText?.setText(uri.toString())
            }
        } else {
            Toast.makeText(
                this,
                if (resultCode == ImagePicker.RESULT_ERROR) ImagePicker.getError(data) else "Task Cancelled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

