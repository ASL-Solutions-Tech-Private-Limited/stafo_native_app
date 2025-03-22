package com.stafo.app.screens.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.stafo.app.databinding.ActivityCompanyProfileBinding
import com.stafo.app.screens.auth.AuthViewModel
import com.stafo.app.screens.auth.dataClass.DataBusinessType
import com.stafo.app.screens.auth.dataClass.DataCity
import com.stafo.app.screens.auth.dataClass.DataCompanyType
import com.stafo.app.screens.auth.dataClass.DataCountry
import com.stafo.app.screens.auth.dataClass.DataStates
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.Company
import com.stafo.app.screens.settings.dataClass.OwnerInfo
import com.stafo.app.screens.settings.dataClass.UpdateCompanyProfile
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream


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
    private var selectedCountry: Int = 0
    private var selectedState: Int = 0
    private var selectedCity: Int = 0
    private var selectedCompanyType: Int = 0
    private var selectedBusinessType: Int = 0

    private var isBusinessTypeDialogShowing = false

    private var profileType: String = "company_basic"
    private var mCompany: Company? = null
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()


    }

    private fun onClickListener() {
        binding?.apply {


            tieCompanyCertificate.setOnClickListener {
                openPicker(1101)
            }
            tieCompanyGstCertificate.setOnClickListener {
                openPicker(1102)
            }
            tieCompanyPanCertificate.setOnClickListener {
                openPicker(1103)
            }
            tieCompanyAadhaarCertificate.setOnClickListener {
                openPicker(1104)
            }
            tieCompanyBankStatement.setOnClickListener {
                openPicker(1105)
            }




            settingsViewModel.getCompanyDetails(this@CompanyProfileActivity)

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            binding.rdgpProfile.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radio_basic -> {
                        profileType = "company_basic"
                        binding.llBasicInfo.visibility = View.VISIBLE
                        binding.llOwnerInfo.visibility = View.GONE
                        binding.llDocumentinfo.visibility = View.GONE
                    }

                    R.id.radio_owner -> {
                        profileType = "company_owner"
                        binding.llBasicInfo.visibility = View.GONE
                        binding.llOwnerInfo.visibility = View.VISIBLE
                        binding.llDocumentinfo.visibility = View.GONE
                    }

                    R.id.radio_document -> {
                        profileType = "company_document"
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
                if (profileType == "company_basic") {
                    if (validateBasicInfo()) {
                        val ownerInfo = OwnerInfo(
                            firstName = "",
                            lastName = "",
                            mobile = "",
                            email = ""
                        )
                        val companyInfo = UpdateCompanyProfile(
                            companyName = binding.tieCompanyName.text.toString(),
                            companyType = selectedCompanyType.toString().trim(),
                            businessTypeId = selectedBusinessType,
                            registrationNumber = binding.tieCompanyRegNo.text.toString().trim(),
                            gstNumber = binding.tieCompanyGstNo.text.toString().trim(),
                            panNumber = binding.tieCompanyPanNo.text.toString().trim(),
                            address = binding.tieCompanyAddress.text.toString(),
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



                        settingsViewModel.updateCompanyProfile(
                            this@CompanyProfileActivity,
                            companyInfo
                        )


                    }


                } else if (profileType == "company_owner") {
                    if (isValidOwnerInfo()) {
                        val ownerInfo = OwnerInfo(
                            firstName = binding.tieOwnerName.text.toString().trim(),
                            lastName = "",
                            mobile = binding.tieOwnerMobileNo.text.toString().trim(),
                            email = binding.tieOwnerEmail.text.toString().trim()
                        )
                        val companyInfo = UpdateCompanyProfile(
                            companyName = tieCompanyName.text.toString(),
                            companyType = selectedCompanyType.toString(),
                            businessTypeId = selectedBusinessType,
                            registrationNumber = tieCompanyRegNo.text.toString().trim(),
                            gstNumber = tieCompanyGstNo.text.toString().trim(),
                            panNumber = tieCompanyPanNo.text.toString().trim(),
                            address = tieCompanyAddress.text.toString().trim(),
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


                        Log.d("res","post data: $companyInfo")
                        settingsViewModel.updateCompanyProfile(
                            this@CompanyProfileActivity,
                            companyInfo
                        )


                    }
                } else if (profileType == "company_document") {

                    if (documentInfo()) {

                        val imageUris = listOf(
                            Uri.parse(binding.tieCompanyCertificate.text.toString()),
                            Uri.parse(binding.tieCompanyGstCertificate.text.toString()),
                            Uri.parse(binding.tieCompanyPanCertificate.text.toString()),
                            Uri.parse(binding.tieCompanyAadhaarCertificate.text.toString()),
                            Uri.parse(binding.tieCompanyBankStatement.text.toString())
                        )

                        val documentTypeIds = listOf(1, 2, 3, 4, 5)

                        settingsViewModel.postCompanyUpdateDocument(
                            this@CompanyProfileActivity,
                            imageUris,
                            documentTypeIds
                        )


                    }


                }
            }
        }


    }

    private fun openPicker(req: Int) {

        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(
                1080,
                1080
            )
            .start(req)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mCompanyProfileResponse.observe(this) {
            if (it.status) {
                it.data?.let { data ->
                    mCompany = data.company
                    binding.tieCompanyName.setText(data.company?.company_name ?: "")
                    isFocusableField(binding.tieCompanyName)
                    binding.tieCompanyAddress.setText(data.company?.address ?: "")
                    binding.tieCompanyRegNo.setText(data.company?.registration_number ?: "")
                    binding.tieCompanyGstNo.setText(data.company?.gst_number ?: "")
                    binding.tieCompanyPanNo.setText(data.company?.pan_number ?: "")


                    //

                    if (!data.countryName.isNullOrEmpty()){
                        enableDisableView(binding.tieSelectCountry,false)
                        binding.tieSelectCountry.setText(data.countryName ?: "")
                    }

                    if (!data.stateName.isNullOrEmpty()){
                        enableDisableView(binding.tieSelectState,false)
                        binding.tieSelectState.setText(data.stateName ?: "")
                    }

                    if (!data.cityName.isNullOrEmpty()){
                        enableDisableView(binding.tieSelectCity,false)
                        binding.tieSelectCity.setText(data.cityName ?: "")
                    }

                    binding.tieOwnerEmail.setText(data.company?.email ?: "")


                    if (!data.company?.mobile_no.isNullOrEmpty()){
                        binding.tieOwnerMobileNo.setText(data.company?.mobile_no ?: "")
                        isFocusableField(binding.tieOwnerMobileNo)
                    }


                    if (!data.proprietor?.firstName.isNullOrEmpty()){
                        binding.tieOwnerName.setText(data.proprietor?.firstName ?: "")
                        isFocusableField(binding.tieOwnerName)
                    }










                    val companyTypeId = data.company?.company_type?.trim()?.toIntOrNull() ?: 0
                    selectedCompanyType = companyTypeId

                    val companyTypeName =
                        mCompanyTypeList?.find { it.id == companyTypeId }?.company_name ?: ""
                    binding.tieCompanyType.setText(companyTypeName)


                    val businessTypeId = data.company?.business_type_id ?: 0
                    selectedBusinessType = businessTypeId

                    val businessTypeName = mBusinessTypeList?.find { it.id == businessTypeId }?.business_name ?: ""
                    binding.tieBusinessType.post {
                        binding.tieBusinessType.setText(businessTypeName)
                    }





                    selectedCountry = data.company?.country?.toInt() ?: 0
                    selectedState = data.company?.state?.toInt() ?: 0
                    selectedCity = data.company?.city?.toInt() ?: 0


                }
            } else {
                CustomToast(this, it.message)
            }

            observeAuthViewModel()

        }

        settingsViewModel.mUpdateCompanyResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
            } else {
                CustomToast(this, it.message)
            }

        }

        settingsViewModel.mCompanyUpdateDocumentResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
            } else {
                CustomToast(this, it.message)
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
                    for (comType in mCompanyTypeList?.indices!!) {
                        if (mCompany?.company_type == mCompanyTypeList?.get(comType)?.id.toString()) {
                            binding.tieCompanyType.setText(mCompanyTypeList?.get(comType)?.company_name)
                        }
                    }
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

                    for (businessType in mCompanyTypeList?.indices!!) {
                        if (mCompany?.business_type_id.toString() == mBusinessTypeList?.get(
                                businessType
                            )?.id.toString()
                        ) {
                            binding.tieBusinessType.setText(mBusinessTypeList?.get(businessType)?.business_name)
                        }
                    }
                }
            }
        }





        viewModel.getCountryList(this)
        viewModel.mCountryResponse.observe(this) {
            if (it.success) {
                mCountryList = it.data

                Log.d("API_", mCountryList.toString())
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

                if (title == "Company Type") {
                    selectedCompanyType = searchListItem.id
                    Log.d("res", "get : $selectedCompanyType $searchListItem.title")

                } else if (title == "Business Type") {
                    selectedBusinessType = searchListItem.id
                    Log.d("res", "get : $selectedBusinessType $searchListItem.title")
                } else if (title == "Country") {
                    selectedCountry = searchListItem.id
                    Log.d("res", "get : $selectedCountry $searchListItem.title")
                } else if (title == "State") {
                    selectedState = searchListItem.id
                    Log.d("res", "get : $selectedState $searchListItem.title")

                } else if (title == "City") {
                    selectedCity = searchListItem.id
                    Log.d("res", "get : $selectedCity $searchListItem.title")

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            if (requestCode == 1101) {
                binding.tieCompanyCertificate.setText(uri.toString())
            } else if (requestCode == 1102) {
                binding.tieCompanyGstCertificate.setText(uri.toString())
            } else if (requestCode == 1103) {
                binding.tieCompanyPanCertificate.setText(uri.toString())
            } else if (requestCode == 1104) {
                binding.tieCompanyAadhaarCertificate.setText(uri.toString())
            } else if (requestCode == 1105) {
                binding.tieCompanyBankStatement.setText(uri.toString())
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            // Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun documentInfo(): Boolean {
        return listOf(
            binding.tieCompanyCertificate to "Please select company registration certificate",
            binding.tieCompanyGstCertificate to "Please select company gst certificate",
            binding.tieCompanyPanCertificate to "Please select company pan card",
            binding.tieCompanyAadhaarCertificate to "Please select company aadhaar or voter id",
            binding.tieCompanyBankStatement to "Please select company bank statement",
        ).all { validateField(it.first, it.second) }
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            if (uri.scheme == "content") {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "temp_file_${System.currentTimeMillis()}")
                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                file
            } else if (uri.scheme == "file") {
                File(uri.path ?: return null)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun enableDisableView(view: View, enabled: Boolean) {
        view.setEnabled(enabled)
        if (view is ViewGroup) {
            val group = view
            for (idx in 0 until group.childCount) {
                enableDisableView(group.getChildAt(idx), enabled)
            }
        }
    }
}