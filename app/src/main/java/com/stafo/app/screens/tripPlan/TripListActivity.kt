package com.stafo.app.screens.tripPlan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTripListBinding
import com.stafo.app.screens.tripPlan.adapters.TripListAdapter
import com.stafo.app.utils.CommonDialogListener
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.showCommonAlertDialog

class TripListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripListBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val mCustomLoader: CustomLoader by lazy { CustomLoader(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //  setContentView(R.layout.activity_trip_list)
        binding = ActivityTripListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.rvTripList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mTripViewModel.getTripList(this)
        observeData()
    }

    private fun observeData() {
        mTripViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") mCustomLoader.show() else mCustomLoader.dismiss()
        }

        binding.btnAddEmp.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java).apply {
                putExtra("isEdit", false)
            })
        }

        mTripViewModel.mTripListResponse.observe(this) {
            if (!it.tripsList.isNullOrEmpty()) {
                binding.rvTripList.adapter =
                    TripListAdapter(this, it.tripsList ?: emptyList(), { trip, type ->
                        when (type) {
                            "All" -> {
                                startActivity(
                                    Intent(
                                        this@TripListActivity,
                                        TripDetailsActivity::class.java
                                    ).apply {
                                        putExtra("tripId", trip.id.toString())
                                    })
                            }

                            "Edit" -> {
                                startActivity(
                                    Intent(
                                        this@TripListActivity,
                                        CreateTripActivity::class.java
                                    ).apply {
                                        putExtra("isEdit", true)
                                        putExtra("tripId", trip.id.toString())
                                        putExtra("tripData", Gson().toJson(trip))
                                    })
                            }

                            "Delete" -> {
                                showCommonAlertDialog(
                                    this@TripListActivity,
                                    "Are you sure you want to delete this trip?",
                                    object :
                                        CommonDialogListener {
                                        override fun onClickEvent(isYes: Boolean) {
                                            if (isYes) {
                                                mTripViewModel.deleteTrip(
                                                    this@TripListActivity,
                                                    trip.id.toString()
                                                )
                                            }
                                        }
                                    },
                                    btn_name = "Yes"
                                )
                            }
                        }
                    })
            }
        }


        mTripViewModel.mTripActionResponse.observe(this) {
            if (it.status == true) {
                CustomToast(this, it.message ?: "")
                mTripViewModel.getTripList(this)
            } else CustomToast(this, it.message ?: "")

        }
    }
}