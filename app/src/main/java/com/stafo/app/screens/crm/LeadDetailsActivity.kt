package com.stafo.app.screens.crm

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityLeadDetailsBinding
import com.stafo.app.screens.crm.adapters.FollowUpListAdapter
import com.stafo.app.screens.crm.dataClass.LeadCreateRequest
import com.stafo.app.screens.crm.dataClass.LeadData
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getFormatDate
import com.stafo.app.utils.getIsCOMPANYLogin

class LeadDetailsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLeadDetailsBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val crmViewModel: CRMViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLeadDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        observeViewModel()


    }
    private fun initView() {
        binding.apply {
            binding.imgBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            val lead = intent.getSerializableExtra("lead_data") as? LeadData

            lead?.let {
                binding.tvLeadName.text = lead.name
                binding.tvCompany.text = lead.company?.company_name
                binding.tvPhone.text = lead.phone.toString()
            }
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false

                crmViewModel.getFollowUpList(this@LeadDetailsActivity,lead?.id.toString())
            }

            crmViewModel.getFollowUpList(this@LeadDetailsActivity,lead?.id.toString())
        }
    }

    private fun observeViewModel() {
        crmViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        crmViewModel.mFollowUpListResponse.observe(this) {
            if (it.success) {

                val followUpList = it.data
                if (!followUpList.isNullOrEmpty()) {
                    binding.tvMsg.visibility= View.GONE
                    binding.recyclerList.layoutManager =
                        LinearLayoutManager(this@LeadDetailsActivity, LinearLayoutManager.VERTICAL, false)
                    binding.recyclerList.adapter = FollowUpListAdapter(followUpList)
                } else  {
                    binding.tvMsg.visibility= View.VISIBLE
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