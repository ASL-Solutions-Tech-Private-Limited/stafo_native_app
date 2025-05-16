package com.stafo.app.screens.crm

import android.content.Intent
import android.os.Bundle
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

    private fun setOnClickListener(){
       binding.apply {

           imgBack.setOnClickListener {
               onBackPressedDispatcher.onBackPressed()
               finish()
           }

           swipeRefresh.setOnRefreshListener {
               swipeRefresh.isRefreshing = false

               getEmployeeDetails()?.let {
                   crmViewModel.getAllLeadList(this@LeadListActivity,it.id)
               }
           }

           getEmployeeDetails()?.let {
               crmViewModel.getAllLeadList(this@LeadListActivity,it.id)
           }







        }
    }

    private fun observeViewModel() {
        crmViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        crmViewModel.mLeadListResponse.observe(this) {
            if (it.success) {
                it.data?.let{it1 ->
                    binding.recyclerLeads.layoutManager =
                        LinearLayoutManager(this@LeadListActivity, LinearLayoutManager.VERTICAL, false)
                    binding.recyclerLeads.adapter= LeadAdapter(it1) { selectedLead ->
                        val intent = Intent(this@LeadListActivity, TakeFollowUpActivity::class.java)
                        intent.putExtra("lead_id", selectedLead.id.toString())
                        intent.putExtra("company_name", selectedLead.company?.company_name ?: "")
                        intent.putExtra("lead_employee", selectedLead.name ?: "")
                        intent.putExtra("phone", selectedLead.phone.toString())
                        intent.putExtra("empId", selectedLead.employee_id.toString())
                        startActivity(intent)
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
}