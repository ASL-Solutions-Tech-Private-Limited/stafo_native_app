package com.stafo.app.screens.reports

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterDownloadReports
import com.stafo.app.databinding.ActivityReportsBinding
import com.stafo.app.screens.settings.dataClass.OwnerInfo
import com.stafo.app.screens.settings.dataClass.UpdateCompanyProfile

class ReportsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportsBinding
    private lateinit var rvAdapter: AdapterDownloadReports
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
    }

    private fun onClickListener() {
        binding.apply {


            imageBack.setOnClickListener {
                onBackPressed()
            }

            binding.rdgpCompanyReports.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radio_report -> {
                        binding.llcReports.visibility = View.VISIBLE
                        binding.llcDownload.visibility = View.GONE

                    }

                    R.id.radio_download -> {
                        binding.llcReports.visibility = View.GONE
                        binding.llcDownload.visibility = View.VISIBLE
                    }

                }


            }



            rvDownloadList.layoutManager = LinearLayoutManager(this@ReportsActivity, LinearLayoutManager.VERTICAL, false)
            rvAdapter=AdapterDownloadReports(this@ReportsActivity)
            rvDownloadList.adapter = rvAdapter


            rtlAttendanceReport.setOnClickListener {
                startActivity(Intent(this@ReportsActivity,AttendanceReportActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            }


        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
        finish()
    }
}