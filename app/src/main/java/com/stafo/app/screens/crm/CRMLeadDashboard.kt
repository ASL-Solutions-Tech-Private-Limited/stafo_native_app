package com.stafo.app.screens.crm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCrmleaddashboardBinding
import com.stafo.app.screens.crm.adapters.FollowUpAdapter
import com.stafo.app.screens.crm.dataClass.LeadDashboardRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin

class CRMLeadDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityCrmleaddashboardBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val crmViewModel: CRMViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCrmleaddashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intiView()
        observeViewModel()
    }

    private fun intiView() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }

            btnAddLead.setOnClickListener {
                startActivity(Intent(this@CRMLeadDashboard, AddLeadsActivity::class.java))

            }

            btnViewlead.setOnClickListener {
                startActivity(Intent(this@CRMLeadDashboard, LeadListActivity::class.java))
            }

        }


        if (getIsCOMPANYLogin(this)){

            Log.d("crm","post data :5678")
            getEmployeeComId()?.let {
                crmViewModel.getLeadDashboard(this, true,it)
            }
        } else  crmViewModel.getLeadDashboard(this, true, getEmployeeDetails()?.id.toString())





    }


    private fun observeViewModel() {
        crmViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        crmViewModel.mCRMDashboardResponse.observe(this) { it ->
            if (it.success) {
                binding.tvTotalLeads.text = it.data?.total_leads.toString()
                binding.tvTodayFollowUps.text = it.data?.total_followups_today.toString()

                val todayList = it.data?.total_followups_today_list
                if (!todayList.isNullOrEmpty()) {
                    binding.tvMsg.visibility=View.GONE
                    binding.recyclerTodayFollowUps.layoutManager =
                        LinearLayoutManager(this@CRMLeadDashboard, LinearLayoutManager.VERTICAL, false)
                    binding.recyclerTodayFollowUps.adapter = FollowUpAdapter(todayList)
                } else{
                    binding.tvMsg.visibility=View.VISIBLE
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