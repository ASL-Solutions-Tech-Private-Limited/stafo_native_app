package com.stafo.app.screens.crm

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCrmleaddashboardBinding
import com.stafo.app.screens.crm.adapters.FollowUpAdapter

class CRMLeadDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityCrmleaddashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContentView(R.layout.activity_crmleaddashboard)
        binding = ActivityCrmleaddashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intiView()
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
            recyclerTodayFollowUps.layoutManager =
                LinearLayoutManager(this@CRMLeadDashboard, LinearLayoutManager.VERTICAL, false)
            recyclerTodayFollowUps.adapter = FollowUpAdapter()
        }
    }
}