package com.stafo.app.screens.crm

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTakeFollowUpBinding
import com.stafo.app.screens.crm.adapters.FollowUpAdapter
import com.stafo.app.screens.crm.adapters.LeadAdapter
import com.stafo.app.screens.crm.dataClass.CreateFollowUpRequest
import com.stafo.app.screens.crm.dataClass.LeadCreateRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TakeFollowUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTakeFollowUpBinding
    private lateinit var followUpAdapter: FollowUpAdapter
    private var leadId: String = ""
    private var companyName: String = ""
    private var leadEmployee: String = ""
    private var phone: String = ""

    private var postFollowUpDate: String = ""
    private var selectedEmployeeId: String = ""

    private val followUpTypes = arrayListOf(
        SearchListItem(1, "By Visiting"), SearchListItem(2, "By Call")
    )

    private val leadStatuses = arrayListOf(
        SearchListItem(1, "New"),
        SearchListItem(2, "Contacted"),
        SearchListItem(3, "Qualified"),
        SearchListItem(4, "Lost"),
        SearchListItem(5, "Onboarding"),
        SearchListItem(6, "Customer")
    )

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val crmViewModel: CRMViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //  setContentView(R.layout.activity_take_follow_up)
        binding = ActivityTakeFollowUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        leadId = intent.getStringExtra("lead_id") ?: ""
        companyName = intent.getStringExtra("company_name") ?: ""
        leadEmployee = intent.getStringExtra("lead_employee") ?: ""
        phone = intent.getStringExtra("phone") ?: ""
        selectedEmployeeId = intent.getStringExtra("empId") ?: ""




        setupLeadDetails()
        setupListener()
        observeViewModel()
    }

    private fun setupListener() {

        binding.apply {

            if (getIsCOMPANYLogin(this@TakeFollowUpActivity)) {
                binding.edtType.isEnabled = false
                binding.edtNextFollowUpDate.isEnabled = false
                binding.edtStatus.isEnabled = false
                binding.edtRemark.isEnabled = false
                binding.btnTakeFollowUp.isEnabled = false
                binding.btnTakeFollowUp.alpha = 0.5f

            } else {
                binding.edtType.isEnabled = true
                binding.edtNextFollowUpDate.isEnabled = true
                binding.edtStatus.isEnabled = true
                binding.edtRemark.isEnabled = true
                binding.btnTakeFollowUp.isEnabled = true
                binding.btnTakeFollowUp.alpha = 1f
            }




            imgBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


            btnTakeFollowUp.setOnClickListener {
                if (validateInputs()) {

                    val request = CreateFollowUpRequest(
                        company_id = getEmployeeComId().toString(),
                        employee_id = selectedEmployeeId,
                        lead_id = leadId,
                        type = edtType.text.toString().trim(),
                        next_date = postFollowUpDate,
                        status = edtStatus.text.toString().trim(),
                        remarks = edtRemark.text.toString().trim()
                    )

                    crmViewModel.createNewFollowUp(this@TakeFollowUpActivity, request)


                }
            }

            tvLastFollowUp.setOnClickListener {
                val intent = Intent(this@TakeFollowUpActivity, FollowUpListActivity::class.java)
                intent.putExtra("lead_id", leadId)
                startActivity(intent)
            }




            edtNextFollowUpDate.setOnClickListener {
                showCalendarAndSetDate()
            }


        }
        binding.edtType.setOnClickListener {

            val dialog = SearchableDialog(this, followUpTypes, "Follow-up Type")
            dialog.setOnItemSelected(object : OnSearchItemSelected {
                override fun onClick(position: Int, searchListItem: SearchListItem) {
                    binding.edtType.setText(searchListItem.title)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }

        binding.edtStatus.setOnClickListener {
            val dialog = SearchableDialog(this, leadStatuses, "Follow-up Status")
            dialog.setOnItemSelected(object : OnSearchItemSelected {
                override fun onClick(position: Int, searchListItem: SearchListItem) {
                    binding.edtStatus.setText(searchListItem.title)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }

    }



    private fun observeViewModel() {
        crmViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        crmViewModel.mCreateFollowUpResponse.observe(this) {
           if (it.success){
               CustomToast(this,"Follow-up taken successfully.")
               onBackPressedDispatcher.onBackPressed()
               finish()
           }else CustomToast(this,it.message)

        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    private fun setupLeadDetails() {
        // Set lead basic data (This would usually come via Intent or ViewModel)
        binding.tvLeadName.text = leadEmployee
        binding.tvCompany.text = companyName
        binding.tvPhone.text = phone
    }

    private fun validateInputs(): Boolean {
        val type = binding.edtType.text.toString().trim()
        val nextDate = binding.edtNextFollowUpDate.text.toString().trim()
        val status = binding.edtStatus.text.toString().trim()
        val remarks = binding.edtRemark.text.toString().trim()

        return when {
            type.isEmpty() -> {
                CustomToast(this, "Select new follow-up")
                false
            }

            nextDate.isEmpty() -> {
                CustomToast(this, "Select next follow-up")
                false
            }

            status.isEmpty() -> {
                CustomToast(this, "Select lead status")
                false
            }

            remarks.isEmpty() -> {
                CustomToast(this, "Enter lead remarks")
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
                binding.edtNextFollowUpDate.setText(formattedDisplayDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}