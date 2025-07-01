package com.stafo.app.screens.tripPlan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityDriverListBinding
import com.stafo.app.screens.settings.AddEmployeeActivity
import com.stafo.app.screens.tripPlan.adapters.DriverListAdapter
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getIsCOMPANYLogin

class DriverListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverListBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val mCustomLoader: CustomLoader by lazy { CustomLoader(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContentView(R.layout.activity_driver_list)
        binding = ActivityDriverListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.ivBack.setOnClickListener { finish() }
        if (getIsCOMPANYLogin(this)) binding.btnAddEmp.visibility = android.view.View.VISIBLE
        else binding.btnAddEmp.visibility = android.view.View.GONE
        binding.btnAddEmp.setOnClickListener {
            startActivity(Intent(this, AddEmployeeActivity::class.java).apply {
                putExtra("isEdit", false)
            })
        }

        binding.rvDriverList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mTripViewModel.getDriverList(this)
        observeData()
    }

    private fun observeData() {
        mTripViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") mCustomLoader.show() else mCustomLoader.dismiss()
        }
        mTripViewModel.mDriverListResponse.observe(this) {
            if (!it.driversList.isNullOrEmpty()) {
                binding.rvDriverList.visibility = android.view.View.VISIBLE
                binding.llNoData.visibility = android.view.View.GONE
                binding.rvDriverList.adapter =
                    DriverListAdapter(this, it.driversList ?: emptyList(), {})
            } else {
                binding.rvDriverList.visibility = android.view.View.GONE
                binding.llNoData.visibility = android.view.View.VISIBLE
            }
        }
    }
}