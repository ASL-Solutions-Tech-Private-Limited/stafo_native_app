package com.stafo.app.screens.crm

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityLeadListBinding
import com.stafo.app.screens.crm.adapters.LeadAdapter
import com.stafo.app.screens.crm.dataClass.LeadListRequest
import com.stafo.app.screens.settings.dataClass.LeaveRequestBody
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin

class LeadListActivity : AppCompatActivity() {
    lateinit var binding: ActivityLeadListBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val crmViewModel: CRMViewModel by viewModels()

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

        setOnClickListener()
        observeViewModel()


    }

    private fun setOnClickListener() {
        binding.apply {

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
                    binding.recyclerLeads.visibility = View.VISIBLE
                    binding.tvMsg.visibility = View.GONE

                    binding.recyclerLeads.layoutManager = LinearLayoutManager(
                        this@LeadListActivity, LinearLayoutManager.VERTICAL, false
                    )
                    binding.recyclerLeads.adapter = LeadAdapter(
                        leads,
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
}