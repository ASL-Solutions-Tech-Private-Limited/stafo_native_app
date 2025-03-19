package com.stafo.app.screens.settings

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.stafo.app.R
import com.stafo.app.databinding.ActivityAddEmployeeBinding
import com.stafo.app.screens.settings.dataClass.AddEmpRequestBody
import com.stafo.app.screens.settings.dataClass.DataBranch
import com.stafo.app.screens.settings.dataClass.DataDepartment
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEmployeeBinding
    private val calendar = Calendar.getInstance()
    private var mSteps = 1
    private var selectGender: String="male"
    private var selectJobTitle: String=""
    private var selectBranch: Int = 1
    private var selectDepartment: Int = 1

    private var mDateOfJoining: String = ""


    private lateinit var branchDialog: SearchableDialog
    private lateinit var departmentDialog: SearchableDialog

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var mDepartmentList: ArrayList<DataDepartment>? = ArrayList()
    private var mBranchList: ArrayList<DataBranch>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)



        binding?.apply {
            rpbBasicInfo.setProgress(100f)
            rpbBasicInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
            rpbBasicInfo.setFilledColor(resources.getColor(R.color.colorTextPrimary))
        }




        onClickListener()
        observeViewModel()

    }



    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        getEmployeeComId()?.let { settingsViewModel.getBranchList(this, it) }



        settingsViewModel.mBranchListResponse.observe(this) {
            mBranchList = it.data
            binding?.let { it1 ->
                setupSearchableDialog(
                    mBranchList,
                    "Branch",
                    it1.tieBranch
                )
            }
        }

        getEmployeeComId()?.let {
            settingsViewModel.getDepartmentList(this@AddEmployeeActivity,
                it
            )
        }


        settingsViewModel.mDepartmentListResponse.observe(this) {
            mDepartmentList = it.data
            binding?.let { it1 ->
                setupSearchableDialog(
                    mDepartmentList,
                    "Department",
                    it1.tieDepartment
                )
            }
        }

        settingsViewModel.mAddEmpResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
            } else {
                CustomToast(this, it.message)
            }
        }


      /*  settingsViewModel.getJobTitleList(this)


        settingsViewModel.mJobTitleResponse.observe(this) {
          if (it.status){

             val jobTitles = it.data.map { it.name }
             // val jobTitles = mutableListOf("Select Job Title") + it.data.map { it.name }

              val adapterTitle =
                  ArrayAdapter(this@AddEmployeeActivity, R.layout.custom_spinner_item, jobTitles)
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



          }else{
              CustomToast(this,it.message)
          }

        }*/


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

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun onClickListener() {


        binding.apply {

         /*   val options = resources.getStringArray(R.array.position_type)
            val adapterTitle =
                ArrayAdapter(this@AddEmployeeActivity, R.layout.custom_spinner_item, options)
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
                }*/


            val text = "Basic\nDetails*"
            val spannable = SpannableString(text)
            spannable.setSpan(
                ForegroundColorSpan(Color.RED),
                text.length - 1,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.tvBasicDetails.text = spannable



            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }




            /*binding.genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
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


            }*/

            binding.genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radioButton = group.findViewById<RadioButton>(R.id.male)
                val radioButton1 = group.findViewById<RadioButton>(R.id.female)
                val radioButton3 = group.findViewById<RadioButton>(R.id.other)
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

                        radioButton3.setTextColor(resources.getColor(R.color.black))
                        val drawable3 = radioButton3.compoundDrawables[0]
                        drawable3.setColorFilter(
                            resources.getColor(R.color.black),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton3.setCompoundDrawables(drawable3, null, null, null)
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

                        radioButton3.setTextColor(resources.getColor(R.color.black))
                        val drawable3 = radioButton3.compoundDrawables[0]
                        drawable3.setColorFilter(
                            resources.getColor(R.color.black),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton3.setCompoundDrawables(drawable3, null, null, null)
                    }

                    R.id.other -> {
                        selectGender = "other"
                        radioButton3.setTextColor(resources.getColor(R.color.white))
                        val drawable3 = radioButton3.compoundDrawables[0]
                        drawable3.setColorFilter(
                            resources.getColor(R.color.white),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton3.setCompoundDrawables(drawable3, null, null, null)

                        radioButton.setTextColor(resources.getColor(R.color.black))
                        val drawable = radioButton.compoundDrawables[0]
                        drawable.setColorFilter(
                            resources.getColor(R.color.black),
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
                }


            }

            btnNext.setOnClickListener { it ->
                if (mSteps == 1) {
                    if (validateBasicInfo()) {
                        mSteps++
                        btnSkip.visibility = View.VISIBLE
                        switchScreen(1)
                    }
                } else if (mSteps == 2) {

                    mSteps++
                    switchScreen(2)

                } else if (mSteps == 3) {
                    mSteps++
                    btnSkip.visibility = View.GONE
                    btnNext.text = "Submit"
                    switchScreen(3)

                } else {
                    val requestBody = AddEmpRequestBody(

                        name = tieStaffName.text.toString().trim(),
                        email = tieEmailId.text.toString().trim(),
                        position = selectJobTitle,
                        phone = tieMobileNo.text.toString(),
                        branch_id = selectBranch,
                        department_id = selectDepartment,
                        date_of_joining = mDateOfJoining,
                        gender = selectGender,
                        address = tieAddress.text.toString()
                    )

                    settingsViewModel.addEmployee(this@AddEmployeeActivity, requestBody)

                }
            }

            btnSkip.setOnClickListener {
                if (mSteps == 1) {
                    if (validateBasicInfo()) {
                        mSteps++
                        switchScreen(1)
                    }
                } else if (mSteps == 2) {

                    mSteps++
                    switchScreen(2)

                } else if (mSteps == 3) {
                    mSteps++
                    btnSkip.visibility = View.GONE
                    btnNext.text = "Submit"
                    switchScreen(3)

                }
            }

            binding.tieDateJoining.setOnClickListener {
                showDatePicker()
            }

            tieBranch.setOnClickListener { branchDialog.show() }
            tieDepartment.setOnClickListener { departmentDialog.show() }


        }
    }


    private fun validateBasicInfo(): Boolean {
        binding?.apply {
            if (tieStaffName.text.isNullOrEmpty()) {
                tieStaffName.error = "Please enter staff name"
                tieStaffName.requestFocus()
                return false
            }  else if (tieBranch.text.isNullOrEmpty()) {
                CustomToast(this@AddEmployeeActivity,"Please enter branch")
                return false
            } else if (tieDepartment.text.isNullOrEmpty()) {
                CustomToast(this@AddEmployeeActivity,"Please enter department")
                return false
            }  else if (tieMobileNo.text.isNullOrEmpty()) {
                tieMobileNo.error = "Please enter mobile number"
                tieMobileNo.requestFocus()
                return false
            } else if (tieEmailId.text.isNullOrEmpty()) {
                tieEmailId.error = "Please enter email id"
                tieEmailId.requestFocus()
                return false
            } else if (tieDateJoining.text.isNullOrEmpty()) {
               CustomToast(this@AddEmployeeActivity,"Please enter date of joining")
                return false
            } else if (tieAddress.text.isNullOrEmpty()) {
                tieAddress.error = "Please enter address"
                tieAddress.requestFocus()
                return false
            }
        }
        return true
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                mDateOfJoining=formattedDate

                val displayFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                val formattedDisplayDate = displayFormat.format(selectedDate.time)

                binding.tieDateJoining.setText("$formattedDisplayDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    private fun switchScreen(flag: Int) {
        when (flag) {
            1 -> {
                binding?.llBasicInfo?.visibility = View.GONE
                binding?.llPersonalInfo?.visibility = View.VISIBLE
                binding?.llDocumentInfo?.visibility = View.GONE
                binding?.llEmploymentDetails?.visibility = View.GONE

                binding.rpbPersonalInfo.setProgress(100f)
                binding.rpbPersonalInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
                binding.rpbPersonalInfo.setFilledColor(resources.getColor(R.color.colorTextPrimary))

            }

            2 -> {
                binding?.llBasicInfo?.visibility = View.GONE
                binding?.llPersonalInfo?.visibility = View.GONE
                binding?.llDocumentInfo?.visibility = View.VISIBLE
                binding?.llEmploymentDetails?.visibility = View.GONE

                binding.rpbDocumentInfo.setProgress(100f)
                binding.rpbDocumentInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
                binding.rpbDocumentInfo.setFilledColor(resources.getColor(R.color.colorTextPrimary))

            }

            3 -> {
                binding?.llBasicInfo?.visibility = View.GONE
                binding?.llPersonalInfo?.visibility = View.GONE
                binding?.llDocumentInfo?.visibility = View.GONE
                binding?.llEmploymentDetails?.visibility = View.VISIBLE

                binding.rpbEmploymentDetails.setProgress(100f)
                binding.rpbEmploymentDetails.setUnfilledColor(resources.getColor(R.color.tea_green))
                binding.rpbEmploymentDetails.setFilledColor(resources.getColor(R.color.colorTextPrimary))

            }
        }
    }
}