package com.asl_emp_mng.app.screens.emp

import android.app.DatePickerDialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityEmployeeProfileDetailsBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.dataClass.DataBranch
import com.asl_emp_mng.app.screens.settings.dataClass.DataDepartment
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeDataFetch
import com.asl_emp_mng.app.screens.settings.dataClass.UpdateEmployeeProfile
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmployeeProfileDetails : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeProfileDetailsBinding

    private val calendar = Calendar.getInstance()

    private  var selectGender: String="male"
    private lateinit var selectJobTitle: String
    private var selectBranch: Int = 1
    private var selectDepartment: Int = 1
    private lateinit var branchDialog: SearchableDialog
    private lateinit var departmentDialog: SearchableDialog

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var mDepartmentList: ArrayList<DataDepartment>? = ArrayList()
    private var mBranchList: ArrayList<DataBranch>? = ArrayList()

    private var profileType: String = "basic_details"
    private var selectMarital: String = "Single"
    private var mEmpID = ""
    private var mEMPDetails: EmployeeDataFetch? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeeProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)
        mEmpID = intent.getStringExtra("EMP_ID").toString()
        onClickListener()
        observeViewModel()


    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.fetchEmployeeDetails(this@EmployeeProfileDetails,mEmpID )

    }

    /*private fun masterData() {
        settingsViewModel.getBranchList(this)
        settingsViewModel.getDepartmentList(this@EmployeeProfileDetails)


    }*/
    private fun observeViewModel() {

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mFetchEmployeeDetailsResponse.observe(this) {
            if (it.status) {
                it.data?.let { data ->
                    mEMPDetails = data
                    binding.tieStaffName.setText(data.name ?: "")
                    if (!data.name.isNullOrEmpty()) {
                        isFocusableField(binding.tieStaffName)
                    }

                    binding.tieMobileNo.setText(data.phone ?: "")
                    if (!data.phone.isNullOrEmpty()) {
                        isFocusableField(binding.tieMobileNo)
                    }
                    binding.tieEmailId.setText(data.email ?: "")
                    if (!data.email.isNullOrEmpty()) {
                        isFocusableField(binding.tieEmailId)
                    }
                    binding.tieDateJoining.setText(data.dateOfJoining ?: "")
                    if (!data.dateOfJoining.isNullOrEmpty()) {
                        isFocusableField(binding.tieDateJoining)
                    }

                    binding.tieAddress.setText(data.address ?: "")
                    if (!data.address.isNullOrEmpty()) {
                        isFocusableField(binding.tieAddress)
                    }

                    binding.tieDateOfBirth.setText(data.dateOfBirth ?: "")
                    if (!data.dateOfBirth.isNullOrEmpty()) {
                        isFocusableField(binding.tieDateOfBirth)
                    }


                    val maritalStatusFromApi = data.maritalStatus ?: ""

                    if (maritalStatusFromApi.isNotEmpty()) {
                        val mOptions = resources.getStringArray(R.array.marital_status)
                        for (item in mOptions) {
                            if (item == maritalStatusFromApi)
                                binding.spinnerJobTitle.setSelection(mOptions.indexOf(item))
                        }
                    }





                    binding.tieBloodGroup.setText(data.bloodGroup ?: "")
                    if (!data.bloodGroup.isNullOrEmpty()) {
                        isFocusableField(binding.tieBloodGroup)
                    }

                    binding.tieGurdianName.setText(data.guardianName ?: "")
                    if (!data.guardianName.isNullOrEmpty()) {
                        isFocusableField(binding.tieGurdianName)
                    }

                    if (data.position != null && data.position.isNotEmpty()) {
                        val options = resources.getStringArray(R.array.position_type)
                        for (item in options) {
                            if (item == data.position)
                                binding.spinnerJobTitle.setSelection(options.indexOf(item))
                        }
                    }
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



        settingsViewModel.mBranchListResponse.observe(this) {



            mBranchList = it.data
            binding?.let { it1 ->
                setupSearchableDialog(
                    mBranchList,
                    "Branch",
                    it1.tieBranch
                )

                for (brnach in mBranchList!!) {
                    if (mEMPDetails?.branchId == brnach.id) {
                        selectBranch = brnach.id
                        binding.tieBranch.setText(brnach.branch_name)
                    }
                }
            }

        }

        settingsViewModel.mDepartmentListResponse.observe(this) {
            mDepartmentList = it.data
            binding?.let { it1 ->
                setupSearchableDialog(
                    mDepartmentList,
                    "Department",
                    it1.tieDepartment
                )
                for (department in mDepartmentList!!) {
                    if (mEMPDetails?.departmentId == department.id) {
                        selectDepartment = department.id
                        binding.tieDepartment.setText(department.name)
                    }
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

    private fun setupSearchableDialog(
        dataList: List<Any>?,
        title: String,
        field: TextInputEditText
    ) {
        val items = dataList?.map {
            val name = when (it) {
                is DataBranch -> it.branch_name
                is DataDepartment -> it.name
                else -> "Unknown"
            }

            val id = when (it) {
                is DataBranch -> it.id
                is DataDepartment -> it.id
                else -> -1
            }

            SearchListItem(id, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)
                if (title == "Branch") {
                    selectBranch = searchListItem.id
                } else if (title == "Department") {
                    selectDepartment = searchListItem.id
                }

                dialog.dismiss()


            }
        })
        when (title) {
            "Branch" -> branchDialog = dialog
            "Department" -> departmentDialog = dialog
        }
    }



    private fun onClickListener() {
        binding?.apply {


            settingsViewModel.getBranchList(this@EmployeeProfileDetails)
            settingsViewModel.getDepartmentList(this@EmployeeProfileDetails)







            val options = resources.getStringArray(R.array.position_type)
            val adapterTitle = ArrayAdapter(this@EmployeeProfileDetails, R.layout.custom_spinner_item, options)
            binding.spinnerJobTitle.setAdapter(adapterTitle)

            binding.spinnerJobTitle.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        selectJobTitle = selectedItem
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }





            binding.imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
            binding.genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radioButton = group.findViewById<RadioButton>(R.id.male)
                val radioButton1 = group.findViewById<RadioButton>(R.id.female)
                when (checkedId) {
                    R.id.male -> {
                        selectGender = "male"
                        radioButton.setTextColor(resources.getColor(R.color.white))
                        val drawable = radioButton.compoundDrawables[0]
                        drawable.setColorFilter(
                            resources.getColor(R.color.white),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton.setCompoundDrawables(drawable, null, null, null)

                        radioButton1.setTextColor(resources.getColor(R.color.black))
                        val drawable1 = radioButton1.compoundDrawables[0]
                        drawable1.setColorFilter(
                            resources.getColor(R.color.black),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton1.setCompoundDrawables(drawable1, null, null, null)
                    }

                    R.id.female -> {
                        selectGender = "female"
                        radioButton1.setTextColor(resources.getColor(R.color.white))
                        val drawable = radioButton1.compoundDrawables[0]
                        drawable.setColorFilter(
                            resources.getColor(R.color.white),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton1.setCompoundDrawables(drawable, null, null, null)

                        radioButton.setTextColor(resources.getColor(R.color.black))
                        val drawable1 = radioButton.compoundDrawables[0]
                        drawable1.setColorFilter(
                            resources.getColor(R.color.black),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton.setCompoundDrawables(drawable1, null, null, null)
                    }
                }


            }


            binding.rdgpProfile.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radio_basic -> {
                        profileType = "basic_details"
                        binding.llBasicInfo.visibility = View.VISIBLE
                        binding.llPersonalInfo.visibility = View.GONE
                        binding.llDocumentInfo.visibility = View.GONE
                        binding.llEmploymentDetails.visibility = View.GONE
                    }

                    R.id.radio_personal_details -> {
                        profileType = "personal_details"
                        binding.llBasicInfo.visibility = View.GONE
                        binding.llPersonalInfo.visibility = View.VISIBLE
                        binding.llDocumentInfo.visibility = View.GONE
                        binding.llEmploymentDetails.visibility = View.GONE
                    }

                    R.id.radio_document_details -> {
                        profileType = "document_details"
                        binding.llBasicInfo.visibility = View.GONE
                        binding.llPersonalInfo.visibility = View.GONE
                        binding.llDocumentInfo.visibility = View.VISIBLE
                        binding.llEmploymentDetails.visibility = View.GONE
                    }

                    R.id.radio_emp_details -> {
                        profileType = "employee_details"
                        binding.llBasicInfo.visibility = View.GONE
                        binding.llPersonalInfo.visibility = View.GONE
                        binding.llDocumentInfo.visibility = View.GONE
                        binding.llEmploymentDetails.visibility = View.VISIBLE
                    }
                }


            }



            binding.tieDateJoining.setOnClickListener {
                showDatePicker(binding.tieDateJoining)
            }

            binding.tieDateOfBirth.setOnClickListener {
                showDatePicker(binding.tieDateOfBirth)
            }
            initMarital()

            tieBranch.setOnClickListener { branchDialog.show() }
            tieDepartment.setOnClickListener { departmentDialog.show() }


            btnUpdateProfile.setOnClickListener {
                if (profileType == "basic_details") {
                    if (validateBasicInfo()) {
                        Log.d("res","post: ")
                        val request = UpdateEmployeeProfile(
                            name = tieStaffName.text.toString(),
                            email = tieEmailId.text.toString(),
                            phone = tieMobileNo.text.toString(),
                            position = selectJobTitle,
                            salary = 0,
                            branchId = selectBranch,
                            departmentId = selectDepartment,
                            maritalStatus = selectMarital,
                            guardianName = "",
                            bloodGroup = " ",
                            dateOfJoining = tieDateJoining.text.toString(),
                            dateOfBirth = " ",
                            gender = selectGender,
                            address = tieAddress.text.toString()
                        )



                        Log.d("res","post: $request")


                        settingsViewModel.updateEmployeeDetails(
                            this@EmployeeProfileDetails,
                            getEmployeeDetails()?.id.toString(),
                            request
                        )

                    }
                } else if (profileType == "personal_details") {
                    val request = UpdateEmployeeProfile(
                        name = tieStaffName.text.toString(),
                        email = tieEmailId.text.toString(),
                        phone = tieMobileNo.text.toString(),
                        position = selectJobTitle,
                        salary = 0,
                        branchId = selectBranch,
                        departmentId = selectDepartment,
                        maritalStatus = selectMarital,
                        guardianName = tieGurdianName.text.toString(),
                        bloodGroup = tieBloodGroup.text.toString(),
                        dateOfJoining = tieDateJoining.text.toString(),
                        dateOfBirth = tieDateOfBirth.text.toString(),
                        gender = selectGender,
                        address = tieAddress.text.toString()
                    )


                    settingsViewModel.updateEmployeeDetails(
                        this@EmployeeProfileDetails,
                        getEmployeeDetails()?.id.toString(),
                        request
                    )
                } else if (profileType == "document_details") {
                    CustomToast(this@EmployeeProfileDetails, "Working is progress")
                } else if (profileType == "employee_details") {
                    CustomToast(this@EmployeeProfileDetails, "Working is progress")
                }
            }


        }
    }


    private fun isFocusableField(view: TextInputEditText?) {
        view?.isFocusable = false
        view?.isFocusableInTouchMode = false
    }


    private fun validateBasicInfo(): Boolean {
        return listOf(
            binding.tieStaffName to "Please enter  name",
            binding.tieBranch to "Please enter branch ",
            binding.tieDepartment to "Please enter department",
            binding.tieMobileNo to "Please enter mobile no ",
            binding.tieEmailId to "Please enter email",
            binding.tieDateJoining to "Please enter date of joining",
            binding.tieAddress to "Please enter address",
        ).all { validateField(it.first, it.second) }
    }

    private fun validatePersonalInfo(): Boolean {
        return listOf(
            binding.tieDateOfBirth to "Please enter  date of birth",
            binding.tieBloodGroup to "Please enter blood group ",
            binding.tieGurdianName to "Please enter guardian name",
        ).all { validateField(it.first, it.second) }
    }


    private fun showDatePicker(view: TextInputEditText?) {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                view?.setText("$formattedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
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

    private fun initMarital(){
        val marital = resources.getStringArray(R.array.marital_status)
        val adapterMarital = ArrayAdapter(this, R.layout.custom_spinner_item, marital)
        binding.spinnerMaritalSts.setAdapter(adapterMarital)

        binding.spinnerMaritalSts.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    selectMarital = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
    }
}