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
import com.asl_emp_mng.app.utils.CustomLoader
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText


class RegistrationActivity : BaseActivity<ActivityRegistrationBinding, AuthViewModel>() {

    override val bindingVariable: Int = 1
    override val layoutId: Int = R.layout.activity_registration
    override val viewModel: AuthViewModel by lazy { AuthViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    private var mSteps = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding?.lifecycleOwner = this
        viewModel.getLoaderLiveData().observe(this) {
            if (it.equals("load", ignoreCase = true)) {
                if (!customLoader.isShowing)
                    customLoader.show()
            } else if (it.equals("stop", ignoreCase = true)) {
                if (customLoader.isShowing)
                    customLoader.dismiss()
            }
        }
        observeViewModel()
        setupProgressBar()
        setupOnClickListener()
    }

    private fun observeViewModel() {
        viewModel.getCompanyType(this)
        viewModel.mCompanyTypeResponse.observe(this, Observer {
            if (it.success) {


            }
        })
    }

    private fun setupProgressBar() {
        viewDataBinding?.apply {
            rpbBasicInfo.setProgress(100f)
            rpbBasicInfo.setUnfilledColor(
                ContextCompat.getColor(
                    this@RegistrationActivity,
                    R.color.tea_green
                )
            )
            rpbBasicInfo.setFilledColor(
                ContextCompat.getColor(
                    this@RegistrationActivity,
                    R.color.primaryColor
                )
            )
        }
    }

    private fun setupOnClickListener() {
        viewDataBinding?.apply {
            btnNext.setOnClickListener { handleNextButtonClick() }

            // Set up the pickers for documents
            listOf(
                tieCompanyCertificate to 1101,
                tieCompanyGstCertificate to 1102,
                tieCompanyPanCertificate to 1103,
                tieCompanyAadharCertificate to 1104,
                tieCompanyBankStatement to 1105
            ).forEach { (view, requestCode) ->
                view.setOnClickListener { openPicker(requestCode) }
            }

            tieCompanyType.setOnClickListener { showSearchDialog() }
        }
    }

    private fun handleNextButtonClick() {
        when (mSteps) {
            1 -> if (validateBasicInfo()) switchScreen(1)
            2 -> if (isValidOwnerInfo()) switchScreen(2)
        }
    }

    private fun openPicker(req: Int) {
        Log.e("TAG", "openPicker: $req")
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
        return validateField(viewDataBinding?.tieCompanyName, "Please enter company name")
                && validateField(viewDataBinding?.tieCompanyType, "Please enter company type")
                && validateField(
            viewDataBinding?.tieCompanyRegNo,
            "Please enter registration number"
        )
                && validateField(viewDataBinding?.tieCompanyGstNo, "Please enter gst number")
                && validateField(viewDataBinding?.tieCompanyPanNo, "Please enter pan number")
                && validateField(viewDataBinding?.tieCompanyAddress, "Please enter address")
    }

    private fun isValidOwnerInfo(): Boolean {
        return validateField(viewDataBinding?.tieOwnerName, "Please enter owner name")
                && validateField(viewDataBinding?.tieOwnerMobileNo, "Please enter mobile")
                && validateField(viewDataBinding?.tieOwnerEmail, "Please enter email")
                && validateField(viewDataBinding?.tieOwnerAadhar, "Please enter aadhaar no")
                && validateField(viewDataBinding?.tieOwnerPan, "Please enter pan no")
                && validateField(viewDataBinding?.tieOwnerAddress, "Please enter address")
    }

    private fun validateField(view: TextInputEditText?, errorMsg: String): Boolean {
        if (view?.text.isNullOrEmpty()) {
            view?.error = errorMsg
            view?.requestFocus()
            return false
        }
        return true
    }

    private fun showSearchDialog() {
        val companyTypes =
            resources.getStringArray(R.array.company_type_items).mapIndexed { index, title ->
                SearchListItem(index, title)
            }
        val searchableDialog =
            SearchableDialog(this, companyTypes as ArrayList<SearchListItem>, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                viewDataBinding?.tieCompanyType?.setText(searchListItem.title)
                searchableDialog.dismiss()
            }
        })
        searchableDialog.show()
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
