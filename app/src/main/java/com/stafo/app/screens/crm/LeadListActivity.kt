package com.stafo.app.screens.crm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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
import com.stafo.app.databinding.ActivityLeadListBinding
import com.stafo.app.screens.crm.adapters.LeadAdapter
import com.stafo.app.screens.crm.dataClass.LeadData
import com.stafo.app.screens.crm.dataClass.LeadListRequest
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.LeaveRequestBody
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin

class LeadListActivity : AppCompatActivity() {
    lateinit var binding: ActivityLeadListBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val crmViewModel: CRMViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var leadList: List<LeadData> = listOf()
    private var filteredList: List<LeadData> = listOf()

    private lateinit var rvAdapter:LeadAdapter
    private  var isLogin: Boolean=false

    private var empList: List<SearchListItem> = listOf()
    private var selectedEmpId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //   setContentView(R.layout.activity_lead_list)

        binding = ActivityLeadListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (getIsCOMPANYLogin(this)){
            binding.ivFilter.visibility=View.VISIBLE
            settingsViewModel.getAllEmployeeList(this)
            observeViewModel2()
        }else  binding.ivFilter.visibility=View.GONE

        setOnClickListener()
        observeViewModel()
        setupSearchListener()


    }

    private fun setOnClickListener() {
        binding.apply {


            if (getIsCOMPANYLogin(this@LeadListActivity)) isLogin=true else isLogin=false


            ivFilter.setOnClickListener {
                if (empList.isNotEmpty()) {
                    val dialog = SearchableDialog(this@LeadListActivity, ArrayList(empList), "Employee List")
                    dialog.setOnItemSelected(object : OnSearchItemSelected {
                        override fun onClick(position: Int, searchListItem: SearchListItem) {
                            selectedEmpId = searchListItem.title
                            dialog.dismiss()
                            filterList(selectedEmpId!!)
                        }
                    })
                    dialog.show()
                }
            }



            imgBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false

                if (getIsCOMPANYLogin(this@LeadListActivity)) {

                    getEmployeeComId()?.let {
                        crmViewModel.getAllLeadList(
                            this@LeadListActivity, true, it
                        )
                    }


                } else {
                    getEmployeeDetails()?.let {
                        crmViewModel.getAllLeadList(this@LeadListActivity, false, it.id.toString())
                    }
                }
            }

            if (getIsCOMPANYLogin(this@LeadListActivity)) {
                getEmployeeComId()?.let {
                    crmViewModel.getAllLeadList(
                        this@LeadListActivity, true, it
                    )
                }

            } else {
                getEmployeeDetails()?.let {
                    crmViewModel.getAllLeadList(this@LeadListActivity, false, it.id.toString())
                }
            }


        }
    }

    private fun observeViewModel() {
        crmViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        crmViewModel.mLeadListResponse.observe(this) {
            if (it.success) {
                val leads = it.data
                if (!leads.isNullOrEmpty()) {

                    leadList=leads
                    filteredList=leadList



                    binding.recyclerLeads.visibility = View.VISIBLE
                    binding.tvMsg.visibility = View.GONE

                    binding.recyclerLeads.layoutManager = LinearLayoutManager(
                        this@LeadListActivity, LinearLayoutManager.VERTICAL, false
                    )

                    rvAdapter=LeadAdapter(
                        this,
                        leadList,
                        isLogin,
                        onFollowUpClick = { selectedLead ->
                            val intent = Intent(this@LeadListActivity, TakeFollowUpActivity::class.java)
                            intent.putExtra("lead_id", selectedLead.id.toString())
                            intent.putExtra("company_name", selectedLead.company?.company_name ?: "")
                            intent.putExtra("lead_employee", selectedLead.name ?: "")
                            intent.putExtra("phone", selectedLead.phone.toString())
                            intent.putExtra("empId", selectedLead.employee_id.toString())
                            startActivity(intent)
                        },
                        onEditClick = { selectedLead ->
                            val intent = Intent(this@LeadListActivity, AddLeadsActivity::class.java)
                            intent.putExtra("lead_data", selectedLead)
                            intent.putExtra("is_edit", true)
                            startActivity(intent)
                        }
                    )


                    binding.recyclerLeads.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()
                } else {
                    binding.recyclerLeads.visibility = View.GONE
                    binding.tvMsg.visibility = View.VISIBLE
                    binding.tvMsg.text = "No leads found"
                }
            } else {

                binding.recyclerLeads.visibility = View.GONE
                binding.tvMsg.visibility = View.VISIBLE
                binding.tvMsg.text = "Failed to load leads"
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


    private fun setupSearchListener() {
        binding.etDirSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (leadList.isNotEmpty()) {
                    filterList(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            leadList
        } else {
            leadList.filter { lead ->
                lead.name?.contains(query, ignoreCase = true) == true ||
                        lead.phone?.toString()?.contains(query, ignoreCase = true) == true ||
                        lead.status?.contains(query, ignoreCase = true) == true ||
                        lead.employee?.name?.contains(query, ignoreCase = true) == true
            }
        }

        rvAdapter.updateList(filteredList)
    }



    private fun observeViewModel2() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) { response ->
            if (response.status) {
                if (!response.data.isNullOrEmpty()) {

                    empList = response.data.map { employee ->
                        SearchListItem(
                            id = employee.id ?: 0,
                            title = employee.name ?: "No Name"
                        )
                    }







                }
            }
        }




    }
}