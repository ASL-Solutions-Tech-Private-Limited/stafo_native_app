package com.stafo.app.screens.crm

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityFollowUpListBinding
import com.stafo.app.screens.crm.adapters.FollowUpAdapter
import com.stafo.app.screens.crm.adapters.FollowUpListAdapter
import com.stafo.app.screens.crm.adapters.LeadAdapter
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeDetails

class FollowUpListActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFollowUpListBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val crmViewModel: CRMViewModel by viewModels()

    private var leadId:String=""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityFollowUpListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        leadId = intent.getStringExtra("lead_id") ?: ""
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

                crmViewModel.getFollowUpList(this@FollowUpListActivity,leadId)
            }

            crmViewModel.getFollowUpList(this@FollowUpListActivity,leadId)







        }
    }

    private fun observeViewModel() {
        crmViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }
        crmViewModel.mFollowUpListResponse.observe(this) {
            if (it.success) {

                val followUpList = it.data
                if (!followUpList.isNullOrEmpty()) {
                    binding.tvMsg.visibility=View.GONE
                    binding.recyclerList.layoutManager =
                        LinearLayoutManager(this@FollowUpListActivity, LinearLayoutManager.VERTICAL, false)
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