package com.stafo.app.screens.tripPlan

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.stafo.app.databinding.ActivityVehicleListBinding
import com.stafo.app.screens.tripPlan.adapters.VehicleListAdapter
import com.stafo.app.utils.CommonDialogListener
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getIsCOMPANYLogin
import com.stafo.app.utils.showCommonAlertDialog

class VehicleListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleListBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val mCustomLoader: CustomLoader by lazy { CustomLoader(this) }
    private val filterType = listOf(
        SearchListItem(1, "All"), SearchListItem(2, "Active"), SearchListItem(3, "Inactive")

    )
    private var mFlag = "all"
    private lateinit var mAdapter: VehicleListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //  setContentView(R.layout.activity_vehicle_list)
        binding = ActivityVehicleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.rvVehicleList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (getIsCOMPANYLogin(this)) binding.btnAddEmp.visibility = android.view.View.VISIBLE
        else binding.btnAddEmp.visibility = android.view.View.GONE
        binding.btnAddEmp.setOnClickListener {
              startActivity(Intent(this, AddVehicleActivity::class.java))
        }

        binding.btnFilter.setOnClickListener {
            showSearchDialog(filterType, "Select Filter Type") {
                mFlag = it.title.toLowerCase().replace(" ", "").trim()
                mTripViewModel.getVehicleList(this)
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    if (::mAdapter.isInitialized) {
                        mAdapter.filter.filter(s)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        observeData()
    }


    override fun onResume() {
        super.onResume()
        mTripViewModel.getVehicleList(this)
    }

    private fun observeData() {
        mTripViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") mCustomLoader.show() else mCustomLoader.dismiss()
        }
        mTripViewModel.mVehicleListResponse.observe(this) {
            if (!it.vehiclesList.isNullOrEmpty()) {
                val filterVehicleList =
                    if (mFlag == "all") it.vehiclesList else it.vehiclesList?.filter { it.status == mFlag }
                if (filterVehicleList.isNullOrEmpty()) {
                    binding.llNoData.visibility = android.view.View.VISIBLE
                    binding.rvVehicleList.visibility = android.view.View.GONE
                } else {
                    binding.rvVehicleList.visibility = android.view.View.VISIBLE
                    binding.llNoData.visibility = android.view.View.GONE
                }
                mAdapter =
                    VehicleListAdapter(this, filterVehicleList ?: emptyList(), { vehicle, type ->
                        when (type) {
                            "Edit" -> {
                                startActivity(Intent(this, AddVehicleActivity::class.java).apply {
                                    putExtra("isEdit", true)
                                    putExtra("vehicleId", vehicle.id.toString())
                                    putExtra("vehicleData", Gson().toJson(vehicle))
                                })
                            }

                            "Delete" -> {
                                showCommonAlertDialog(
                                    this@VehicleListActivity,
                                    "Are you sure you want to delete this trip?",
                                    object :
                                        CommonDialogListener {
                                        override fun onClickEvent(isYes: Boolean) {
                                            if (isYes) {
                                                mTripViewModel.deleteVehicle(
                                                    this@VehicleListActivity,
                                                    vehicle.id.toString()
                                                )
                                            }
                                        }
                                    },
                                    btn_name = "Yes"
                                )
                            }
                        }
                    })
                binding.rvVehicleList.adapter = mAdapter
            } else {
                binding.rvVehicleList.visibility = android.view.View.GONE
                binding.llNoData.visibility = android.view.View.VISIBLE
            }
        }

        mTripViewModel.mTripActionResponse.observe(this) {
            if (it.status == true) {
                CustomToast(this, it.message ?: "")
                mTripViewModel.getVehicleList(this)
            } else {
                CustomToast(this, it.message ?: "")
            }
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