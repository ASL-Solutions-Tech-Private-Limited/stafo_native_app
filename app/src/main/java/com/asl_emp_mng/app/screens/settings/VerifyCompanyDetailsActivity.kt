package com.asl_emp_mng.app.screens.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityVerifyCompanyDetailsBinding
import com.asl_emp_mng.app.databinding.PanVerifyBottomSheetLayoutBinding
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.screens.settings.dataClass.PanVerifyRequestBody
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeComId
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class VerifyCompanyDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyCompanyDetailsBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var panStatus: Boolean = false
    private var gstStatus: Boolean = false
    private var companyStatus: Boolean = false

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetDialogBinding: PanVerifyBottomSheetLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerifyCompanyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    panStatus && gstStatus && companyStatus -> {
                        val intent = Intent(this@VerifyCompanyDetailsActivity, EmployerDashboard::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    panStatus || gstStatus || companyStatus -> {
                        showExitConfirmationDialog()
                    }
                    else -> {
                        val intent = Intent(this@VerifyCompanyDetailsActivity, EmployerDashboard::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        })




        onClickListener()
        observeViewModel()
    }

    private fun onClickListener() {
        binding.apply {
            settingsViewModel.getCompanyDetails(this@VerifyCompanyDetailsActivity)


            llcPanVerify.setOnClickListener {
                if (panStatus) {
                    CustomToast(this@VerifyCompanyDetailsActivity, "Already verified")
                } else {
                    showCustomBottomSheet("Pan Card Verify")
                }

            }

            llcCompanyVerify.setOnClickListener {
                if (companyStatus) {
                    CustomToast(this@VerifyCompanyDetailsActivity, "Already verified")
                } else {
                    showCustomBottomSheet("Register Number Verify")
                }
            }

            llcGstVerify.setOnClickListener {
                if (gstStatus) {
                    CustomToast(this@VerifyCompanyDetailsActivity, "Already verified")
                } else {
                    showCustomBottomSheet("Gst Number Verify")
                }
            }
        }
    }

    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mCompanyProfileResponse.observe(this) {
            if (it.status) {
                // PAN Verification
                panStatus = it.data?.company?.pan_verify == "Yes"
                binding.txtPanVerify.text = if (panStatus) "Verified" else "Verify"
                binding.txtPanVerify.setTextColor(
                    getColor(if (panStatus) R.color.primaryColorDark else R.color.black)
                )

                // GST Verification
                gstStatus = it.data?.company?.gstn_verify == "Yes"
                binding.txtGstVerify.text = if (gstStatus) "Verified" else "Verify"
                binding.txtGstVerify.setTextColor(
                    getColor(if (gstStatus) R.color.primaryColorDark else R.color.black)
                )

                // Company Verification
                companyStatus = it.data?.company?.registration_verify == "Yes"
                binding.txtCompanyVerify.text = if (companyStatus) "Verified" else "Verify"
                binding.txtCompanyVerify.setTextColor(
                    getColor(if (companyStatus) R.color.primaryColorDark else R.color.black)
                )
            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mPanVerifyResponse.observe(this) {
            if (it.status=="success") {
                settingsViewModel.getCompanyDetails(this@VerifyCompanyDetailsActivity)
                it.status?.let { it1 -> CustomToast(this, it1) }
                bottomSheetDialog.dismiss()
            } else {
                it.status?.let { it1 -> CustomToast(this, it1) }
            }
        }

        settingsViewModel.mVerifyGSTNumberResponse.observe(this) {
            if (it.status=="success") {
                settingsViewModel.getCompanyDetails(this@VerifyCompanyDetailsActivity)
                it.status?.let { it1 -> CustomToast(this, it1) }
                bottomSheetDialog.dismiss()
            } else {
                it.status?.let { it1 -> CustomToast(this, it1) }
            }
        }

        settingsViewModel.mVerifyRegisterNumberResponse.observe(this) {
            if (it.status=="success") {
                settingsViewModel.getCompanyDetails(this@VerifyCompanyDetailsActivity)
                it.status?.let { it1 -> CustomToast(this, it1) }
                bottomSheetDialog.dismiss()
            } else {
                it.status?.let { it1 -> CustomToast(this, it1) }
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

    private fun showCustomBottomSheet(type: String) {
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialogBinding = PanVerifyBottomSheetLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)

        }

        bottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        bottomSheetDialog.setCancelable(false)

        bottomSheetDialogBinding.textView.text = type

        when (type) {
            "Pan Card Verify" -> {
                bottomSheetDialogBinding.tilPanNo.visibility = View.VISIBLE
                bottomSheetDialogBinding.tilGstNo.visibility = View.GONE
                bottomSheetDialogBinding.tilRegisterNo.visibility = View.GONE
            }

            "Register Number Verify" -> {
                bottomSheetDialogBinding.tilPanNo.visibility = View.GONE
                bottomSheetDialogBinding.tilGstNo.visibility = View.GONE
                bottomSheetDialogBinding.tilRegisterNo.visibility = View.VISIBLE
            }

            "Gst Number Verify" -> {
                bottomSheetDialogBinding.tilPanNo.visibility = View.GONE
                bottomSheetDialogBinding.tilGstNo.visibility = View.VISIBLE
                bottomSheetDialogBinding.tilRegisterNo.visibility = View.GONE
            }
        }




        bottomSheetDialogBinding.tiePanNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val pan = s.toString().uppercase()
                if (pan != s.toString()) {
                    bottomSheetDialogBinding.tiePanNo.setText(pan)
                    bottomSheetDialogBinding.tiePanNo.setSelection(pan.length)
                }

                if (pan.length == 10) {
                    if (!isValidPAN(pan)) {
                        bottomSheetDialogBinding.tiePanNo.error = "Invalid PAN format (eg. ABCDE1234F)"
                    }
                }
            }
        })



        bottomSheetDialogBinding.btnSubmit.setOnClickListener {
            when (type) {
                "Pan Card Verify" -> {
                    val panNumber = bottomSheetDialogBinding.tiePanNo.text.toString()
                    if (panNumber.isEmpty()) {
                        bottomSheetDialogBinding.tiePanNo.error = "Please enter PAN number"
                        bottomSheetDialogBinding.tiePanNo.requestFocus()
                    } else {
                        val request = PanVerifyRequestBody(
                            company_id = getEmployeeComId().toString(),
                            type = "pan",
                            number = panNumber
                        )

                        settingsViewModel.companyPanVerify(this@VerifyCompanyDetailsActivity, request)
                    }
                }

                "Register Number Verify" -> {
                    val registerNumber = bottomSheetDialogBinding.tieRegisterNo.text.toString()
                    if (registerNumber.isEmpty()) {
                        bottomSheetDialogBinding.tieRegisterNo.error = "Please enter register number"
                        bottomSheetDialogBinding.tieRegisterNo.requestFocus()
                    }else{
                        val request = PanVerifyRequestBody(
                            company_id = getEmployeeComId().toString(),
                            type = "company",
                            number = registerNumber
                        )

                        settingsViewModel.companyRGSVerify(this@VerifyCompanyDetailsActivity, request)

                    }
                }

                "Gst Number Verify" -> {
                    val gstNumber = bottomSheetDialogBinding.tieGstNo.text.toString()
                    if (gstNumber.isEmpty()) {
                        bottomSheetDialogBinding.tieGstNo.error = "Please enter GST number"
                        bottomSheetDialogBinding.tieGstNo.requestFocus()
                    }else{
                        val request = PanVerifyRequestBody(
                            company_id = getEmployeeComId().toString(),
                            type = "gstin",
                            number = gstNumber
                        )
                        settingsViewModel.companyGSTVerify(this@VerifyCompanyDetailsActivity, request)

                    }
                }
            }
        }

        bottomSheetDialogBinding.bottomSheetCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetDialogBinding.root)
        bottomSheetDialog.show()
    }



    private fun isValidPAN(pan: String): Boolean {
        val panRegex = Regex("^[A-Z]{5}[0-9]{4}[A-Z]$")
        return panRegex.matches(pan)
    }


    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this@VerifyCompanyDetailsActivity)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Your company is not verified. Please complete verification.")

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
