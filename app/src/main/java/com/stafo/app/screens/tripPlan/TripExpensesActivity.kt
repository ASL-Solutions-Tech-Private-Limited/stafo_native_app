package com.stafo.app.screens.tripPlan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTripExpensesBinding
import com.stafo.app.utils.CustomLoader

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

        mTripViewModel.fetchTripExpenses(this, mTripId)
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