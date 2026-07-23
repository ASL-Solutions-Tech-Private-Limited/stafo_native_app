package com.stafo.app.screens.tripPlan

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.gson.Gson
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTripListBinding
import com.stafo.app.screens.tripPlan.adapters.TripListAdapter
import com.stafo.app.utils.CommonDialogListener
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.showCommonAlertDialog
import java.util.Locale
import java.util.Locale.getDefault

class TripListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripListBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val mCustomLoader: CustomLoader by lazy { CustomLoader(this) }
    private var mFlag = "all"

    private val filterType = listOf(
        SearchListItem(1, "All"), SearchListItem(2, "Pending"), SearchListItem(3, "On Going"),
        SearchListItem(4, "Pause"), SearchListItem(5, "Completed"),

        )
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

        mFlag = intent.getStringExtra("flag") ?: "all"
        binding.ivBack.setOnClickListener { finish() }
        binding.rvTripList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.btnFilter.setOnClickListener {
            showSearchDialog(filterType, "Select Filter Type") {
                mFlag = it.title.lowercase(getDefault()).replace(" ", "").trim()
                mTripViewModel.getTripList(this)
            }
        }

        observeData()
    }

    override fun onResume() {
        super.onResume()
        mTripViewModel.getTripList(this)
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
                binding.rvTripList.visibility = View.VISIBLE
                binding.llNoData.visibility = View.GONE
                val filterData =
                    if (mFlag == "all") it.tripsList else it.tripsList?.filter { it.status == mFlag }
                if (filterData.isNullOrEmpty()) {
                    binding.llNoData.visibility = View.VISIBLE
                    binding.rvTripList.visibility = View.GONE
                } else {
                    binding.llNoData.visibility = View.GONE
                    binding.rvTripList.visibility = View.VISIBLE
                }
                binding.rvTripList.adapter =
                    TripListAdapter(this, filterData ?: emptyList(), { trip, type ->
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
            } else {
                binding.rvTripList.visibility = View.GONE
                binding.llNoData.visibility = View.VISIBLE
            }
        }


        mTripViewModel.mTripActionResponse.observe(this) {
            if (it.status == true) {
                CustomToast(this, it.message ?: "")
                mTripViewModel.getTripList(this)
            } else CustomToast(this, it.message ?: "")

        }
    }

    private fun showSearchDialog(
        list: List<SearchListItem>,
        title: String,
        onSelected: (SearchListItem) -> Unit
    ) {
        val dialog = SearchableDialog(this, ArrayList(list), title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, item: SearchListItem) {
                onSelected(item)
                dialog.dismiss()
            }
        })
        dialog.show()
    }
}