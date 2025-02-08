package com.asl_emp_mng.app.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityRegistrationBinding
import com.github.dhaval2404.imagepicker.ImagePicker


class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    private var mSteps = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_registration)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)

        // Set the content view to the root of the binding
        setContentView(binding.root)


        binding?.apply {
            rpbBasicInfo.setProgress(100f)

            // Customize colors
            rpbBasicInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
            rpbBasicInfo.setFilledColor(resources.getColor(R.color.primaryColor))
        }

        onClickListener()

    }


    private fun onClickListener() {
        binding?.apply {
            btnNext.setOnClickListener {
                if (mSteps == 1) {
                    if (validateBasicInfo()) {
                        mSteps++
                        switchScreen(1)
                    }
                } else if (mSteps == 2) {
                    if (isValidOwnerInfo()) {
                        mSteps++
                        switchScreen(2)
                    }
                }
            }

            tieCompanyType.setOnClickListener {
                showSearchDialog()
            }

            tieBusinessType.setOnClickListener {
                showBusinessSearchDialog()
            }
            tieSelectCountry.setOnClickListener {
                showCountrySearchDialog()
            }
            tieSelectState.setOnClickListener {
                showStateSearchDialog()
            }
            tieSelectCity.setOnClickListener {
                showCitySearchDialog()
            }


            tieOwnerSelectCountry.setOnClickListener {
                showOwnerCountrySearchDialog()
            }
            tieOwnerSelectState.setOnClickListener {
                showOwnerStateSearchDialog()
            }
            tieOwnerSelectCity.setOnClickListener {
                showOwnerCitySearchDialog()
            }


            tieCompanyCertificate.setOnClickListener {
                openPicker(1101)
            }
            tieCompanyGstCertificate.setOnClickListener {
                openPicker(1102)
            }
            tieCompanyPanCertificate.setOnClickListener {
                openPicker(1103)
            }
            tieCompanyAadharCertificate.setOnClickListener {
                openPicker(1104)
            }
            tieCompanyBankStatement.setOnClickListener {
                openPicker(1105)
            }
        }
    }


    private fun openPicker(req: Int) {
        Log.e("TAG", "openPicker: $req")
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start(req)
    }

    private fun switchScreen(flag: Int) {
        when (flag) {
            1 -> {
                binding?.llBasicInfo?.visibility = View.GONE
                binding?.llOwnerInfo?.visibility = View.VISIBLE
                binding?.llDocumentinfo?.visibility = View.GONE

                binding.rpbOwnerInfo.setProgress(100f)
                binding.rpbOwnerInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
                binding.rpbOwnerInfo.setFilledColor(resources.getColor(R.color.primaryColor))

            }

            2 -> {
                binding?.llBasicInfo?.visibility = View.GONE
                binding?.llOwnerInfo?.visibility = View.GONE
                binding?.llDocumentinfo?.visibility = View.VISIBLE

                binding.rpbDocumentInfo.setProgress(100f)
                binding.rpbDocumentInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
                binding.rpbDocumentInfo.setFilledColor(resources.getColor(R.color.primaryColor))

            }
        }
    }

    private fun validateBasicInfo(): Boolean {
        binding?.apply {
            if (tieCompanyName.text.isNullOrEmpty()) {
                tieCompanyName.error = "Please enter company name"
                tieCompanyName.requestFocus()
                return false
            } else if (tieCompanyType.text.isNullOrEmpty()) {
                tieCompanyType.error = "Please enter company type"
                return false
            } else if (tieBusinessType.text.isNullOrEmpty()) {
                tieBusinessType.error = "Please enter business type"
                return false
            } else if (tieSelectCountry.text.isNullOrEmpty()) {
                tieSelectCountry.error = "Please select country"
                return false
            } else if (tieSelectState.text.isNullOrEmpty()) {
                tieSelectState.error = "Please select state"
                return false
            } else if (tieSelectCity.text.isNullOrEmpty()) {
                tieSelectCity.error = "Please select city"
                return false
            } else if (tieCompanyRegNo.text.isNullOrEmpty()) {
                tieCompanyRegNo.error = "Please enter registration number"
                tieCompanyRegNo.requestFocus()
                return false
            } else if (tieCompanyGstNo.text.isNullOrEmpty()) {
                tieCompanyGstNo.error = "Please enter gst number"
                tieCompanyGstNo.requestFocus()
                return false
            } else if (tieCompanyPanNo.text.isNullOrEmpty()) {
                tieCompanyPanNo.error = "Please enter pan number"
                tieCompanyPanNo.requestFocus()
                return false
            } else if (tieCompanyAddress.text.isNullOrEmpty()) {
                tieCompanyAddress.error = "Please enter address"
                tieCompanyAddress.requestFocus()
                return false
            }
        }
        return true
    }

    private fun isValidOwnerInfo(): Boolean {
        binding?.apply {
            if (tieOwnerName.text.isNullOrEmpty()) {
                tieOwnerName.error = "Please enter owner name"
                tieOwnerName.requestFocus()
                return false
            } else if (tieOwnerMobileNo.text.isNullOrEmpty()) {
                tieOwnerMobileNo.error = "Please enter mobile"
                tieOwnerMobileNo.requestFocus()
                return false
            } else if (tieOwnerEmail.text.isNullOrEmpty()) {
                tieOwnerEmail.error = "Please enter email"
                tieOwnerEmail.requestFocus()
                return false
            } else if (tieOwnerSelectCountry.text.isNullOrEmpty()) {
                tieOwnerSelectCountry.error = "Please select country"
                return false
            } else if (tieOwnerSelectState.text.isNullOrEmpty()) {
                tieOwnerSelectState.error = "Please select state"
                return false
            } else if (tieOwnerSelectCity.text.isNullOrEmpty()) {
                tieOwnerSelectCity.error = "Please select city"
                return false
            } else if (tieOwnerAadhar.text.isNullOrEmpty()) {
                tieOwnerAadhar.error = "Please enter aadhaar no"
                tieOwnerAadhar.requestFocus()
                return false
            } else if (tieOwnerPan.text.isNullOrEmpty()) {
                tieOwnerPan.error = "Please enter pan no"
                tieOwnerPan.requestFocus()
                return false
            } else if (tieOwnerAddress.text.isNullOrEmpty()) {
                tieOwnerAddress.error = "Please enter address"
                tieOwnerAddress.requestFocus()
                return false
            }
        }
        return true
    }

    private fun showSearchDialog() {
        var itemList = ArrayList<String>()
        var companyType = ArrayList<SearchListItem>()
        itemList =
            resources.getStringArray(R.array.company_type_items).toCollection(ArrayList())
        for (i in itemList.indices) {
            companyType.add(SearchListItem(i, itemList[i]))
        }
        val searchableDialog = SearchableDialog(this, companyType, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                binding.tieCompanyType.setText(searchListItem.title)
                searchableDialog.dismiss()
            }

        })
        searchableDialog.show()
    }

    private fun showBusinessSearchDialog() {
        var itemList = ArrayList<String>()
        var businessType = ArrayList<SearchListItem>()
        itemList =
            resources.getStringArray(R.array.company_type_items).toCollection(ArrayList())
        for (i in itemList.indices) {
            businessType.add(SearchListItem(i, itemList[i]))
        }
        val searchableDialog = SearchableDialog(this, businessType, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                binding.tieBusinessType.setText(searchListItem.title)
                searchableDialog.dismiss()
            }

        })
        searchableDialog.show()
    }

    private fun showCountrySearchDialog() {
        var itemList = ArrayList<String>()
        var countryList = ArrayList<SearchListItem>()
        itemList =
            resources.getStringArray(R.array.company_type_items).toCollection(ArrayList())
        for (i in itemList.indices) {
            countryList.add(SearchListItem(i, itemList[i]))
        }
        val searchableDialog = SearchableDialog(this, countryList, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                binding.tieSelectCountry.setText(searchListItem.title)
                searchableDialog.dismiss()
            }

        })
        searchableDialog.show()
    }

    private fun showOwnerCountrySearchDialog() {
        var itemList = ArrayList<String>()
        var countryList = ArrayList<SearchListItem>()
        itemList =
            resources.getStringArray(R.array.company_type_items).toCollection(ArrayList())
        for (i in itemList.indices) {
            countryList.add(SearchListItem(i, itemList[i]))
        }
        val searchableDialog = SearchableDialog(this, countryList, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                binding.tieOwnerSelectCountry.setText(searchListItem.title)
                searchableDialog.dismiss()
            }

        })
        searchableDialog.show()
    }

    private fun showStateSearchDialog() {
        var itemList = ArrayList<String>()
        var stateList = ArrayList<SearchListItem>()
        itemList =
            resources.getStringArray(R.array.company_type_items).toCollection(ArrayList())
        for (i in itemList.indices) {
            stateList.add(SearchListItem(i, itemList[i]))
        }
        val searchableDialog = SearchableDialog(this, stateList, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                binding.tieSelectState.setText(searchListItem.title)
                searchableDialog.dismiss()
            }

        })
        searchableDialog.show()
    }


    private fun showOwnerStateSearchDialog() {
        var itemList = ArrayList<String>()
        var stateList = ArrayList<SearchListItem>()
        itemList =
            resources.getStringArray(R.array.company_type_items).toCollection(ArrayList())
        for (i in itemList.indices) {
            stateList.add(SearchListItem(i, itemList[i]))
        }
        val searchableDialog = SearchableDialog(this, stateList, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                binding.tieOwnerSelectState.setText(searchListItem.title)
                searchableDialog.dismiss()
            }

        })
        searchableDialog.show()
    }

    private fun showCitySearchDialog() {
        var itemList = ArrayList<String>()
        var cityList = ArrayList<SearchListItem>()
        itemList =
            resources.getStringArray(R.array.company_type_items).toCollection(ArrayList())
        for (i in itemList.indices) {
            cityList.add(SearchListItem(i, itemList[i]))
        }
        val searchableDialog = SearchableDialog(this, cityList, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                binding.tieSelectCity.setText(searchListItem.title)
                searchableDialog.dismiss()
            }

        })
        searchableDialog.show()
    }

    private fun showOwnerCitySearchDialog() {
        var itemList = ArrayList<String>()
        var cityList = ArrayList<SearchListItem>()
        itemList =
            resources.getStringArray(R.array.company_type_items).toCollection(ArrayList())
        for (i in itemList.indices) {
            cityList.add(SearchListItem(i, itemList[i]))
        }
        val searchableDialog = SearchableDialog(this, cityList, "Search")
        searchableDialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                binding.tieOwnerSelectCity.setText(searchListItem.title)
                searchableDialog.dismiss()
            }

        })
        searchableDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            if (requestCode == 1101) {
                binding.tilCompanyCertificate.editText?.setText(uri.toString())
            } else if (requestCode == 1102) {
                binding.tilCompanyGstCertificate.editText?.setText(uri.toString())
            } else if (requestCode == 1103) {
                binding.tilCompanyPanCertificate.editText?.setText(uri.toString())
            } else if (requestCode == 1104) {
                binding.tilCompanyAddharCertificate.editText?.setText(uri.toString())
            } else if (requestCode == 1105) {
                binding.tilCompanyBankStatement.editText?.setText(uri.toString())
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

}