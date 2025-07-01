package com.stafo.app.screens.tripPlan

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTripDashboardBinding
import com.stafo.app.screens.tripPlan.adapters.DashboardTripListAdapter
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getIsCOMPANYLogin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripDashboardBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val mCustomLoader: CustomLoader by lazy { CustomLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTripDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        setupRecyclerView()


        if (getIsCOMPANYLogin(this)) {
            binding.btnDrivers.visibility = View.VISIBLE
            binding.btnVehicles.visibility = View.VISIBLE
        } else {
            binding.btnDrivers.visibility = View.GONE
            binding.btnVehicles.visibility = View.GONE
        }



        observeTripDashboardData()
    }


    override fun onResume() {
        super.onResume()
        mTripViewModel.getTripDashboardData(this)
    }
    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }
        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, TripListActivity::class.java).apply {
                putExtra("flag", "ongoing")
            })
        }
        binding.btnCreateTrip.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java).apply {
                putExtra("isEdit", false)
            })
        }

        binding.btnDrivers.setOnClickListener {
            startActivity(Intent(this@TripDashboardActivity, DriverListActivity::class.java))
        }
        binding.btnVehicles.setOnClickListener {
            startActivity(Intent(this@TripDashboardActivity, VehicleListActivity::class.java))
        }
        binding.btnViewTrips.setOnClickListener {
            startActivity(Intent(this@TripDashboardActivity, TripListActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        binding.rvTripList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun observeTripDashboardData() {
        mTripViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") mCustomLoader.show() else mCustomLoader.hide()
        }

        mTripViewModel.mTripDashboardResponse.observe(this) { response ->
            if (response.status == true && !response.tripsListData.isNullOrEmpty()) {
                val allTrips = response.tripsListData

                // 1. Set total ongoing and completed counts
                binding.tvTotalonGoingTrip.text =
                    allTrips?.count { it.status == "ongoing" }.toString()
                binding.tvTotalCompletedTrip.text =
                    allTrips?.count { it.status == "completed" }.toString()

                // 2. Filter today's trips only for RecyclerView
                val todayTrips = allTrips?.filter { isToday(it.startTime) }
                if (!todayTrips.isNullOrEmpty()) {
                    binding.rvTripList.adapter = DashboardTripListAdapter(mContext = this,
                        tripData = todayTrips,
                        onItemClickListener = {
                            startActivity(
                                Intent(
                                    this@TripDashboardActivity,
                                    TripDetailsActivity::class.java
                                ).apply {
                                    putExtra("tripId", it.id.toString())
                                })
                        })
                    binding.rvTripList.visibility = View.VISIBLE
                    binding.tvMsg.visibility = View.GONE
                } else {
                    binding.rvTripList.visibility = View.GONE
                    binding.tvMsg.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun isToday(dateStr: String?): Boolean {
        if (dateStr.isNullOrBlank()) return false
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val tripDate = format.parse(dateStr)
            val today = format.format(Date())
            format.format(tripDate!!) == today
        } catch (e: Exception) {
            false
        }
    }
}
