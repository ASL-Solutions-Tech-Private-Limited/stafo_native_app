package com.stafo.app.screens.tripPlan

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTripExpensesBinding
import com.stafo.app.screens.tripPlan.adapters.ExpensesListAdapter
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.formatAmount

class TripExpensesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTripExpensesBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private lateinit var tripExpensesBottomSheet: TripExpensesBottomSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContentView(R.layout.activity_trip_expenses)
        binding = ActivityTripExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mTripId = intent.getStringExtra("tripId") ?: ""
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvTripHeader.text = "Trip Expenses"


        binding.btnAddExp.setOnClickListener {
            showTripActionBottomSheet(mTripId, mTripViewModel)
        }

        binding.rvExpenses.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mTripViewModel.fetchTripExpenses(this, mTripId)
        observeTripExpenses()
    }


    private fun observeTripExpenses() {
        mTripViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") customLoader.show() else customLoader.dismiss()
        }

        mTripViewModel.mTripExpensesListResponse.observe(this) {
            if (!it.dataExpensesList.isNullOrEmpty()) {
                binding.rvExpenses.visibility = View.VISIBLE
                //binding.tvNoData.visibility = View.VISIBLE
                val totalAmount = it.dataExpensesList?.filter { it.amount?.toDouble() != null }
                    ?.sumOf { it.amount?.toDouble() ?: 0.0 } ?: 0.0
                binding.tvAmount.text = "${formatAmount(totalAmount)}"
                binding.llTotal.visibility = View.VISIBLE
                binding.rvExpenses.adapter = ExpensesListAdapter(
                    this,
                    it.dataExpensesList ?: emptyList(),
                    { tripId, tripStatus ->

                    })
            } else {
                binding.llTotal.visibility = View.GONE
                binding.rvExpenses.visibility = View.GONE
                //  binding.tvNoData.visibility = View.GONE
            }
        }
    }

    private fun showTripActionBottomSheet(tripId: String, tripViewModel: TripViewModel) {
        tripExpensesBottomSheet = TripExpensesBottomSheet(context = this,
            tripID = tripId,
            viewModel = tripViewModel,
            onAssignSuccess = { },
            onCameraRequest = { })
        tripExpensesBottomSheet.show()
    }
}