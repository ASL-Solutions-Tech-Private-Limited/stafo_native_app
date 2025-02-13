package com.asl_emp_mng.app.screens.settings

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.BranchAdapter
import com.asl_emp_mng.app.databinding.ActivityCompanyProfileBinding
import com.asl_emp_mng.app.utils.CustomLoader
import java.util.Collections
import java.util.Random

class CompanyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompanyProfileBinding
    private var token: String? = null

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCompanyProfileBinding.inflate(layoutInflater)
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

            token = getToken(this@CompanyProfileActivity, "token")

            token?.let {
                settingsViewModel.getCompanyDetails(this@CompanyProfileActivity, it)

            }




            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            binding.rdgpProfile.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radio_basic -> {
                        binding.llBasicInfo.visibility = View.VISIBLE
                        binding.llOwnerInfo.visibility = View.GONE
                        binding.llDocumentinfo.visibility = View.GONE
                    }

                    R.id.radio_owner -> {
                        binding.llBasicInfo.visibility = View.GONE
                        binding.llOwnerInfo.visibility = View.VISIBLE
                        binding.llDocumentinfo.visibility = View.GONE
                    }

                    R.id.radio_document -> {
                        binding.llBasicInfo.visibility = View.GONE
                        binding.llOwnerInfo.visibility = View.GONE
                        binding.llDocumentinfo.visibility = View.VISIBLE
                    }
                }


            }


        }
    }


    private fun getToken(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mCompanyProfileResponse.observe(this) {
            if (it.status) {
                val companyName = it.data?.company?.companyName

                if (!companyName.isNullOrEmpty()) {
                    binding.tieCompanyName.setText(companyName)
                    binding.tieCompanyName.isFocusable = false
                    binding.tieCompanyName.isFocusableInTouchMode = false
                }

                val companyType = it.data?.company?.companyType

                if (!companyType.isNullOrEmpty()) {
                    binding.tieCompanyType.setText(companyType)
                    binding.tieCompanyType.isFocusable = false
                    binding.tieCompanyType.isFocusableInTouchMode = false
                }

                val panNo = it.data?.company?.panNumber
                if (!panNo.isNullOrEmpty()) {
                    binding.tieCompanyPanNo.setText(panNo)
                    binding.tieCompanyPanNo.isFocusable = false
                    binding.tieCompanyPanNo.isFocusableInTouchMode = false
                }

                val gstNo = it.data?.company?.gstNumber

                if (!gstNo.isNullOrEmpty()) {
                    binding.tieCompanyGstNo.setText(gstNo)
                    binding.tieCompanyGstNo.isFocusable = false
                    binding.tieCompanyGstNo.isFocusableInTouchMode = false
                }
                val rgsNo = it.data?.company?.registrationNumber


                if (!rgsNo.isNullOrEmpty()) {
                    binding.tieCompanyRegNo.setText(rgsNo)
                    binding.tieCompanyRegNo.isFocusable = false
                    binding.tieCompanyRegNo.isFocusableInTouchMode = false
                }

                val address = it.data?.company?.address


                if (!address.isNullOrEmpty()) {
                    binding.tieCompanyAddress.setText(address)
                    binding.tieCompanyAddress.isFocusable = false
                    binding.tieCompanyAddress.isFocusableInTouchMode = false
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


}