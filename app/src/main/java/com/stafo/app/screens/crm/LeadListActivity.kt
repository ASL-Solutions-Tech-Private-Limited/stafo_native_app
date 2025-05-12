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
import com.stafo.app.databinding.ActivityLeadListBinding
import com.stafo.app.screens.crm.adapters.LeadAdapter

class LeadListActivity : AppCompatActivity() {
    lateinit var binding: ActivityLeadListBinding
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

        binding?.apply {
            recyclerLeads.layoutManager =
                LinearLayoutManager(this@LeadListActivity, LinearLayoutManager.VERTICAL, false)
            recyclerLeads.adapter = LeadAdapter(listener = {
                startActivity(Intent(this@LeadListActivity, TakeFollowUpActivity::class.java))
            })
        }
    }
}