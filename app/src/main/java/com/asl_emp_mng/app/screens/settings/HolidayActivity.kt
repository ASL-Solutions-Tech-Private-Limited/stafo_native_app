package com.asl_emp_mng.app.screens.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.AdapterHoliday
import com.asl_emp_mng.app.base.adapter.AdapterRequestLeaveHistory
import com.asl_emp_mng.app.databinding.ActivityHolidayBinding
import com.asl_emp_mng.app.databinding.ActivityLeaveRequestHistoryBinding

class HolidayActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHolidayBinding
    private lateinit var rvAdapter: AdapterHoliday


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHolidayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onClickListener()


    }

    private fun onClickListener() {
        binding?.apply {

            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@HolidayActivity,
                LinearLayoutManager.VERTICAL,false)
            binding.rvHolidayList.setLayoutManager(layoutManager)
            rvAdapter = AdapterHoliday(this@HolidayActivity)
            binding.rvHolidayList.adapter = rvAdapter



            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false

            }

            imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            binding.llcAddHoliday.setOnClickListener {

                startActivity(Intent(this@HolidayActivity, AddHolidayActivity::class.java))
            }




        }
    }
}