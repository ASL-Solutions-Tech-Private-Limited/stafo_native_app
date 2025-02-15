package com.asl_emp_mng.app.screens.settings

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.BranchAdapter
import com.asl_emp_mng.app.databinding.ActivityCompanyProfileBinding
import com.asl_emp_mng.app.screens.auth.AuthViewModel
import com.asl_emp_mng.app.screens.auth.dataClass.DataBusinessType
import com.asl_emp_mng.app.screens.auth.dataClass.DataCity
import com.asl_emp_mng.app.screens.auth.dataClass.DataCompanyType
import com.asl_emp_mng.app.screens.auth.dataClass.DataCountry
import com.asl_emp_mng.app.screens.auth.dataClass.DataStates
import com.asl_emp_mng.app.screens.settings.dataClass.OwnerInfo
import com.asl_emp_mng.app.screens.settings.dataClass.UpdateCompanyProfile
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.google.android.material.textfield.TextInputEditText
import java.util.Collections
import java.util.Random

class CompanyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompanyProfileBinding


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    // for update company type , business type ,country,state, city,

    private val viewModel: AuthViewModel by viewModels()

    private var mCompanyTypeList: ArrayList<DataCompanyType>? = ArrayList()
    private var mBusinessTypeList: ArrayList<DataBusinessType>? = ArrayList()
    private var mCountryList: ArrayList<DataCountry>? = ArrayList()
    private var mStateList: ArrayList<DataStates>? = ArrayList()
    private var mCityList: ArrayList<DataCity>? = ArrayList()

    private lateinit var companyTypeDialog: SearchableDialog
    private lateinit var businessTypeDialog: SearchableDialog
    private lateinit var countryDialog: SearchableDialog
    private lateinit var stateDialog: SearchableDialog
    private lateinit var cityDialog: SearchableDialog
    private var selectedCountry: Int=0
    private var selectedState: Int=0
    private var selectedCity: Int=0
    private var selectedCompanyType: Int=0
    private var selectedBusinessType: Int=0

    private var profileType: String="company_basic"




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

        observeAuthViewModel()

    }

    private fun onClickListener() {
        binding?.apply {

            settingsViewModel.getCompanyDetails(this@CompanyProfileActivity)




            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            binding.rdgpProfile.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radio_basic -> {
                        profileType="company_basic"
                        binding.llBasicInfo.visibility = View.VISIBLE
                        binding.llOwnerInfo.visibility = View.GONE
                        binding.llDocumentinfo.visibility = View.GONE
                    }

                    R.id.radio_owner -> {
                        profileType="company_owner"
                        binding.llBasicInfo.visibility = View.GONE
                        binding.llOwnerInfo.visibility = View.VISIBLE
                        binding.llDocumentinfo.visibility = View.GONE
                    }

                    R.id.radio_document -> {
                        profileType="company_document"
                        binding.llBasicInfo.visibility = View.GONE
                        binding.llOwnerInfo.visibility = View.GONE
                        binding.llDocumentinfo.visibility = View.VISIBLE
                    }
                }


            }


            tieCompanyType.setOnClickListener { companyTypeDialog.show() }
            tieBusinessType.setOnClickListener { businessTypeDialog.show() }
            tieSelectCountry.setOnClickListener { countryDialog.show() }
            tieSelectState.setOnClickListener { validateAndShowStateDialog() }
            tieSelectCity.setOnClickListener { validateAndShowCityDialog() }


            btnCompanyProfile.setOnClickListener {
                if (profileType=="company_basic"){

                    if (validateBasicInfo()){

                        val ownerInfo = OwnerInfo(
                            firstName = "",
                            lastName = "",
                            mobile = "",
                            email = ""
                        )
                        val companyInfo = UpdateCompanyProfile(
                            companyName = binding.tieCompanyName.text.toString(),
                            companyType = selectedCompanyType.toString(),
                            businessTypeId = selectedBusinessType,
                            registrationNumber = binding.tieCompanyRegNo.text.toString(),
                            gstNumber = binding.tieCompanyGstNo.text.toString(),
                            panNumber = binding.tieCompanyPanNo.text.toString(),
                            address = binding.tieCompanyAddress.toString(),
                            cityId = selectedCity,
                            stateId = selectedState,
                            countryId = selectedCountry,
                            pin = "",
                            bankName = "",
                            accountNumber = "",
                            ifscCode = "",
                            noOfEmployee = 0,
                            status = "",
                            email = "",
                            mobileNo = "",
                            ownerInfo = ownerInfo
                        )



                        settingsViewModel.updateCompanyProfile(this@CompanyProfileActivity,companyInfo)


                    }


                }else if (profileType=="company_owner"){
                    if (isValidOwnerInfo()){
              /*          val ownerInfo = OwnerInfo(
                            firstName = binding.tieOwnerName.text.toString(),
                            lastName = "",
                            mobile = binding.tieOwnerMobileNo.text.toString(),
                            email = binding.tieOwnerEmail.text.toString()
                        )
                        val companyInfo = UpdateCompanyProfile(
                            companyName = "",
                            companyType ="",
                            businessTypeId = 0,
                            registrationNumber = "",
                            gstNumber = "",
                            panNumber = "",
                            address ="",
                            cityId = selectedCity,
                            stateId = selectedState,
                            countryId = selectedCountry,
                            pin = "",
                            bankName = "",
                            accountNumber = "",
                            ifscCode = "",
                            noOfEmployee = 0,
                            status = "",
                            email = "",
                            mobileNo = "",
                            ownerInfo = ownerInfo
                        )



                        settingsViewModel.updateCompanyProfile(this@CompanyProfileActivity,companyInfo)
*/
                        CustomToast(this@CompanyProfileActivity,"Working is progress")

                    }
                }else if (profileType=="company_document"){
                    CustomToast(this@CompanyProfileActivity,"Working is progress")
                }
            }


        }
    }




    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mCompanyProfileResponse.observe(this) {
            if (it.status) {

                it.data?.let { data ->

                    binding.tieCompanyName.setText(data.company?.companyName ?: "")
                    isFocusableField(binding.tieCompanyName)


                    binding.tieCompanyAddress.setText(data.company?.address ?: "")
                    isFocusableField(binding.tieCompanyAddress)


                    binding.tieCompanyRegNo.setText(data.company?.registrationNumber ?: "")
                    isFocusableField(binding.tieCompanyRegNo)


                    binding.tieCompanyGstNo.setText(data.company?.gstNumber ?: "")
                    isFocusableField(binding.tieCompanyGstNo)


                    binding.tieCompanyPanNo.setText(data.company?.panNumber ?: "")
                    isFocusableField(binding.tieCompanyPanNo)

                }
            }else{
                CustomToast(this,it.message)
            }

        }

        settingsViewModel.mUpdateCompanyResponse.observe(this) {
          if (it.status){
              CustomToast(this,it.message)
              onBackPressedDispatcher.onBackPressed()
          }else{
              CustomToast(this,it.message)
          }

        }


    }

    private fun isFocusableField(view: TextInputEditText?) {
        view?.isFocusable = false
        view?.isFocusableInTouchMode = false
    }
    private fun observeAuthViewModel() {
        viewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        viewModel.getCompanyType(this)
        viewModel.mCompanyTypeResponse.observe(this) {
            if (it.success) {


                mCompanyTypeList = it.data
                binding.let { it1 ->
                    setupSearchableDialog(
                        mCompanyTypeList,
                        "Company Type",
                        it1.tieCompanyType
                    )
                }
            }
        }


        viewModel.getBusinessType(this)

        viewModel.mBusinessTypeResponse.observe(this) {
            if (it.success) {
                mBusinessTypeList = it.data
                binding.let { it1 ->
                    setupSearchableDialog(
                        mBusinessTypeList,
                        "Business Type",
                        it1.tieBusinessType
                    )
                }
            }
        }





        viewModel.getCountryList(this)
        viewModel.mCountryResponse.observe(this) {
            if (it.success) {
                mCountryList = it.data

                Log.d("API_",mCountryList.toString())
                binding.let { it1 ->
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
                binding.let { it1 ->
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
                binding.let { it1 ->
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


    private fun validateAndShowStateDialog() {
        if (binding.tieSelectCountry.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please select country first", Toast.LENGTH_SHORT).show()
        } else {
            stateDialog.show()
        }
    }

    private fun validateAndShowCityDialog() {
        if (binding.tieSelectState.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please select state first", Toast.LENGTH_SHORT).show()
        } else {
            cityDialog.show()
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
                is DataCountry -> it.name
                is DataStates -> it.name
                is DataCity -> it.name
                else -> "Unknown"
            }

            val id = when (it) {
                is DataCompanyType -> it.id
                is DataBusinessType -> it.id
                is DataCountry -> it.id
                is DataStates -> it.id
                is DataCity -> it.id
                else -> -1
            }

            SearchListItem(id, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)

                if (title=="Company Type"){
                    selectedCompanyType=searchListItem.id
                    Log.d("res","get : $selectedCompanyType $searchListItem.title")

                }else if (title=="Business Type"){
                    selectedBusinessType=searchListItem.id
                    Log.d("res","get : $selectedBusinessType $searchListItem.title")
                }else if (title=="Country"){
                    selectedCountry=searchListItem.id
                    Log.d("res","get : $selectedCountry $searchListItem.title")
                }
                else if (title=="State"){
                    selectedState=searchListItem.id
                    Log.d("res","get : $selectedState $searchListItem.title")

                }else if (title=="City"){
                    selectedCity=searchListItem.id
                    Log.d("res","get : $selectedCity $searchListItem.title")

                }


                dialog.dismiss()


                when (field) {

                    binding.tieSelectCountry -> viewModel.getStateList(
                        this@CompanyProfileActivity,
                        searchListItem.id.toString()
                    )

                    binding.tieSelectState -> viewModel.getCityList(
                        this@CompanyProfileActivity,
                        searchListItem.id.toString()


                    )
                }
            }
        })
        when (title) {
            "Company Type" -> companyTypeDialog = dialog
            "Business Type" -> businessTypeDialog = dialog
            "Country" -> countryDialog = dialog
            "State" -> stateDialog = dialog
            "City" -> cityDialog = dialog
        }
    }
    fun getCountryName(code: Int): String {
        for (country in mCountryList!!) {
            if (country.id == code) {
                return country.name
            }
        }
        return "Unknown Country"
    }


    private fun validateBasicInfo(): Boolean {
        return listOf(
            binding.tieCompanyName to "Please enter company name",
            binding.tieCompanyType to "Please enter company type",
            binding.tieBusinessType to "Please enter company type",
            binding.tieSelectCountry to "Please enter company type",
            binding.tieSelectState to "Please enter company type",
            binding.tieSelectCity to "Please enter company type",
            binding.tieCompanyPanNo to "Please enter pan number",
            binding.tieCompanyAddress to "Please enter address"
        ).all { validateField(it.first, it.second) }
    }

    private fun isValidOwnerInfo(): Boolean {
        return listOf(
            binding.tieOwnerName to "Please enter owner name",
            binding.tieOwnerMobileNo to "Please enter mobile",
            binding.tieOwnerEmail to "Please enter email",
            binding.tieOwnerAddress to "Please enter address"
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

}