package com.stafo.app.screens.crm

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.stafo.app.R
import com.stafo.app.base.adapter.EmpListAdapter
import com.stafo.app.base.adapter.RadioShiftAdapter
import com.stafo.app.databinding.ActivityAddLeadsBinding
import com.stafo.app.screens.crm.adapters.LeadAdapter
import com.stafo.app.screens.crm.dataClass.LeadCreateRequest
import com.stafo.app.screens.crm.dataClass.LeadData
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getFormatDate
import com.stafo.app.utils.getIsCOMPANYLogin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddLeadsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLeadsBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val crmViewModel: CRMViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()


    private var postFollowUpDate: String = ""
    private var selectedEmpId: String = ""
    private var editLeadId: String = ""

    private val leadSources = arrayListOf(
        SearchListItem(1, "Facebook"),
        SearchListItem(2, "Instagram"),
        SearchListItem(3, "LinkedIn"),
        SearchListItem(4, "Google Ads"),
        SearchListItem(5, "Referral")
    )

    private val leadStatuses = arrayListOf(
        SearchListItem(1, "New"),
        SearchListItem(2, "Contacted"),
        SearchListItem(3, "Qualified"),
        SearchListItem(4, "Lost"),
        SearchListItem(5, "Customer"),
        SearchListItem(6, "OnBoard")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContentView(R.layout.activity_add_leads)
        binding = ActivityAddLeadsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (getIsCOMPANYLogin(this)){
            binding.etEmployee.visibility=View.VISIBLE
            settingsViewModel.getAllEmployeeList(this)
            observeViewModel2()
        }else  binding.etEmployee.visibility=View.GONE


        initView()
        observeViewModel()
    }

    private fun initView() {
      binding.apply {
          binding.imgBack.setOnClickListener {
              finish()
          }

          val lead = intent.getSerializableExtra("lead_data") as? LeadData
          val isEdit = intent.getBooleanExtra("is_edit", false)

          editLeadId= lead?.id.toString()

          if (isEdit){
              tvPageTitle.text="Edit Lead"
          }else tvPageTitle.text="Add Lead"


          lead?.let {
              binding.etName.setText(it.name)
              binding.etPhone.setText(it.phone.toString())
              binding.etEmail.setText(it.email.toString())
              binding.etCompany.setText(it.company_name)
              binding.etAddress.setText(it.company_address)
              binding.etfrom.setText(it.lead_from)
              binding.etStatus.setText(it.status)
              binding.etNotes.setText(it.notes.toString())
              binding.etEmployee.setText(it.employee?.name)
              binding.etnextfollowup.setText(getFormatDate(it.next_date.toString()))

          }



          binding.etfrom.setOnClickListener {

              val dialog = SearchableDialog(this@AddLeadsActivity, leadSources, "Lead Source")
              dialog.setOnItemSelected(object : OnSearchItemSelected {
                  override fun onClick(position: Int, searchListItem: SearchListItem) {
                      binding.etfrom.setText(searchListItem.title)
                      dialog.dismiss()
                  }
              })
              dialog.show()
          }

          // Set up searchable dialog for Lead Status
          binding.etStatus.setOnClickListener {
              val dialog = SearchableDialog(this@AddLeadsActivity, leadStatuses, "Lead Status")
              dialog.setOnItemSelected(object : OnSearchItemSelected {
                  override fun onClick(position: Int, searchListItem: SearchListItem) {
                      binding.etStatus.setText(searchListItem.title)
                      dialog.dismiss()
                  }
              })
              dialog.show()
          }

          binding.etnextfollowup.setOnClickListener {
              showCalendarAndSetDate()
          }

          // Save button click
          binding.btnSaveLead.setOnClickListener {
              if (validateInputs()) {


                  if (isEdit){

                      if (getIsCOMPANYLogin(this@AddLeadsActivity)){

                          val request = LeadCreateRequest(
                              companyId = getEmployeeComId().toString(),
                              employeeId = selectedEmpId,
                              name =etName.text.toString() ,
                              company_name =etCompany.text.toString() ,
                              company_address =etAddress.text.toString() ,
                              email = etEmail.text.toString().trim(),
                              phone = etPhone.text.toString().trim(),
                              notes = etNotes.text.toString().trim(),
                              status = etStatus.text.toString().trim(),
                              leadFrom = etfrom.text.toString().trim(),
                              nextDate = postFollowUpDate
                          )

                          crmViewModel.updateLead(this@AddLeadsActivity,editLeadId.toInt(),request)

                      }else{
                          val request = LeadCreateRequest(
                              companyId = getEmployeeComId().toString(),
                              employeeId = getEmployeeDetails()?.id.toString(),
                              name =etName.text.toString() ,
                              company_name =etCompany.text.toString() ,
                              company_address =etAddress.text.toString() ,
                              email = etEmail.text.toString().trim(),
                              phone = etPhone.text.toString().trim(),
                              notes = etNotes.text.toString().trim(),
                              status = etStatus.text.toString().trim(),
                              leadFrom = etfrom.text.toString().trim(),
                              nextDate = postFollowUpDate
                          )

                          crmViewModel.updateLead(this@AddLeadsActivity,editLeadId.toInt(),request)

                      }



                  }else{

                      if (getIsCOMPANYLogin(this@AddLeadsActivity)){

                          val request = LeadCreateRequest(
                              companyId = getEmployeeComId().toString(),
                              employeeId = selectedEmpId,
                              name =etName.text.toString() ,
                              company_name =etCompany.text.toString() ,
                              company_address =etAddress.text.toString() ,
                              email = etEmail.text.toString().trim(),
                              phone = etPhone.text.toString().trim(),
                              notes = etNotes.text.toString().trim(),
                              status = etStatus.text.toString().trim(),
                              leadFrom = etfrom.text.toString().trim(),
                              nextDate = postFollowUpDate
                          )

                          crmViewModel.createNewLead(this@AddLeadsActivity,request)

                      }else{
                          val request = LeadCreateRequest(
                              companyId = getEmployeeComId().toString(),
                              employeeId = getEmployeeDetails()?.id.toString(),
                              name =etName.text.toString() ,
                              company_name =etCompany.text.toString() ,
                              company_address =etAddress.text.toString() ,
                              email = etEmail.text.toString().trim(),
                              phone = etPhone.text.toString().trim(),
                              notes = etNotes.text.toString().trim(),
                              status = etStatus.text.toString().trim(),
                              leadFrom = etfrom.text.toString().trim(),
                              nextDate = postFollowUpDate
                          )

                          crmViewModel.createNewLead(this@AddLeadsActivity,request)

                      }

                  }








              }
          }
      }
    }

    private fun validateInputs(): Boolean {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val company = binding.etCompany.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val from = binding.etfrom.text.toString().trim()
        val status = binding.etStatus.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        return when {
            name.isEmpty() -> {
                binding.etName.error = "Name is required"
                false
            }

            phone.isEmpty() -> {
                binding.etPhone.error = "Phone number is required"
                false
            }

            !android.util.Patterns.PHONE.matcher(phone).matches() -> {
                binding.etPhone.error = "Invalid phone number"
                false
            }

            email.isEmpty() -> {
                binding.etEmail.error = "Email is required"
                false
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Invalid email address"
                false
            }

            company.isEmpty() -> {
                binding.etCompany.error = "Company name is required"
                false
            }


            address.isEmpty() -> {
                binding.etAddress.error = "Company address is required"
                false
            }

            from.isEmpty() -> {
                binding.etfrom.error = "Select lead source"
                false
            }

            status.isEmpty() -> {
                binding.etStatus.error = "Select lead status"
                false
            }

            else -> true
        }
    }

    private fun showCalendarAndSetDate() {
        val postFormat = "yyyy-MM-dd"
        val displayFormat = "dd MMM yy"

        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                val postDateFormatter = SimpleDateFormat(postFormat, Locale.US)
                val displayDateFormatter = SimpleDateFormat(displayFormat, Locale.US)

                postFollowUpDate = postDateFormatter.format(calendar.time)
                Log.d("crm", "date $postFollowUpDate")

                val formattedDisplayDate = displayDateFormatter.format(calendar.time)
                binding.etnextfollowup.setText(formattedDisplayDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun observeViewModel2() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) { response ->
            if (response.status) {
                if (!response.data.isNullOrEmpty()) {



                    val empList = ArrayList<SearchListItem>().apply {
                        response.data.forEach { employee ->
                            add(
                                SearchListItem(
                                    id = employee.id ?: 0,
                                    title = employee.name ?: "No Name"
                                )
                            )
                        }
                    }




                    binding.etEmployee.setOnClickListener {
                        val dialog = SearchableDialog(this@AddLeadsActivity, empList, "Employee List")
                        dialog.setOnItemSelected(object : OnSearchItemSelected {
                            override fun onClick(position: Int, searchListItem: SearchListItem) {
                                selectedEmpId=position.toString()
                                Log.d("crm","employee id: $selectedEmpId")
                                binding.etEmployee.setText(searchListItem.title)
                                dialog.dismiss()
                            }
                        })
                        dialog.show()
                    }


                }
            }
        }




    }

    private fun observeViewModel() {
        crmViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        crmViewModel.mLeadCreateResponse.observe(this) {
            if (it.success){
                CustomToast(this,"New lead add successfully")
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else  it.message?.let { it1 -> CustomToast(this, it1) }


        }
        crmViewModel.mUpdateLeadResponse.observe(this) {
            if (it.success){
                CustomToast(this,"Lead information updated successfully.")
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else  it.message?.let { it1 -> CustomToast(this, it1) }


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