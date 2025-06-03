package com.stafo.app.screens.tripPlan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.textfield.TextInputEditText
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCreateTripBinding
import com.stafo.app.utils.CustomLoader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTripBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val mCustomLoader: CustomLoader by lazy { CustomLoader(this) }
    private var mSelectedVehicle: String? = ""
    private var mSelectedDriver: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.etJourneyStart.setOnClickListener {
            showDateTimePicker(binding.etJourneyStart)
        }

        binding.etEstimatedEnd.setOnClickListener {
            showDateTimePicker(binding.etEstimatedEnd)
        }

        binding.etStartLocation.setOnClickListener {
            openMapForLocation(binding.etStartLocation)
        }

        binding.etDestinationLocation.setOnClickListener {
            openMapForLocation(binding.etDestinationLocation)
        }

        binding.btnCreateTrip.setOnClickListener {
            if (validateFields()) {
                createTrip()
            }
        }

        mTripViewModel.getVehicleList(this)
        mTripViewModel.getDriverList(this)
        observeViewModel()
    }

    private fun observeViewModel() {
        mTripViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") {
                mCustomLoader.show()
            } else {
                mCustomLoader.hide()
            }
        }

        mTripViewModel.mVehicleListResponse.observe(this) { response ->
            if (response.status == true) {
                if (!response.vehiclesList.isNullOrEmpty()) {
                    val empList = ArrayList<SearchListItem>().apply {
                        response.vehiclesList?.forEach { employee ->
                            add(
                                SearchListItem(
                                    id = employee.id ?: 0, title = employee.vehicleNo ?: "No Name"
                                )
                            )
                        }
                    }

                    binding.etVehicle.setOnClickListener {
                        val dialog =
                            SearchableDialog(this@CreateTripActivity, empList, "Vehicle List")
                        dialog.setOnItemSelected(object : OnSearchItemSelected {
                            override fun onClick(position: Int, searchListItem: SearchListItem) {
                                for (item in response.vehiclesList!!) {
                                    if (item.vehicleNo == searchListItem.title) mSelectedVehicle =
                                        item.id.toString()
                                }
                                mTripViewModel.checkVehicleAvailability(
                                    this@CreateTripActivity, mSelectedVehicle ?: ""
                                )
                                binding.etVehicle.setText(searchListItem.title)
                                dialog.dismiss()
                            }
                        })
                        dialog.show()
                    }
                }
            }
        }

        mTripViewModel.mDriverListResponse.observe(this) { response ->
            if (response.status == true) {
                if (!response.driversList.isNullOrEmpty()) {
                    val empList = ArrayList<SearchListItem>().apply {
                        response.driversList?.forEach { employee ->
                            add(
                                SearchListItem(
                                    id = employee.id ?: 0, title = employee.name ?: "No Name"
                                )
                            )
                        }
                    }

                    binding.etDriver.setOnClickListener {
                        val dialog =
                            SearchableDialog(this@CreateTripActivity, empList, "Driver List")
                        dialog.setOnItemSelected(object : OnSearchItemSelected {
                            override fun onClick(position: Int, searchListItem: SearchListItem) {
                                for (item in response.driversList!!) {
                                    if (item.name == searchListItem.title) mSelectedDriver =
                                        item.id.toString()
                                }
                                mTripViewModel.checkDriverAvailability(
                                    this@CreateTripActivity, mSelectedDriver ?: ""
                                )
                                binding.etDriver.setText(searchListItem.title)
                                dialog.dismiss()
                            }
                        })
                        dialog.show()
                    }
                }
            }
        }


        mTripViewModel.mCreateTripResponse.observe(this) { response ->
            if (response.status == true) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun showDateTimePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val pickedDate = Calendar.getInstance()
                pickedDate.set(Calendar.YEAR, year)
                pickedDate.set(Calendar.MONTH, month)
                pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val timePicker = TimePickerDialog(
                    this, { _, hourOfDay, minute ->
                        pickedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        pickedDate.set(Calendar.MINUTE, minute)

                        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        editText.setText(format.format(pickedDate.time))
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun openMapForLocation(editText: TextInputEditText) {
        // Replace with real location picker logic if needed
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=Pick+Location")
        }
        startActivity(intent)

        // For demo purposes, we simulate location name
        editText.setText("Sample Location") // Replace with actual selected location
    }

    private fun validateFields(): Boolean {
        val requiredFields = listOf(
            binding.etTripName,
            binding.etTripDescription,
            binding.etTripClientName,
            binding.etTripClientNumber,
            binding.etStartLocation,
            binding.etDestinationLocation,
            binding.etJourneyStart,
            binding.etEstimatedEnd,
            binding.etDriver,
            binding.etVehicle
        )

        var isValid = true

        requiredFields.forEach { field ->
            if (field.text.isNullOrBlank()) {
                field.error = "Required"
                isValid = false
            } else {
                field.error = null
            }
        }

        // Validate phone number
        val phone = binding.etTripClientNumber.text.toString()
        if (phone.length != 10 || !phone.all { it.isDigit() }) {
            binding.etTripClientNumber.error = "Enter valid 10-digit number"
            isValid = false
        }

        return isValid
    }


    private fun createTrip() {
        val tripRequest = HashMap<String, Any>()
        tripRequest["customer_name"] = binding.etTripClientName.text.toString()
        tripRequest["customer_email"] = " "
        tripRequest["customer_phone"] = binding.etTripClientNumber.text.toString()
        tripRequest["customer_address"] = " "
        tripRequest["title"] = binding.etTripName.text.toString()
        tripRequest["start_time"] = binding.etJourneyStart.text.toString()
        tripRequest["end_time"] = binding.etEstimatedEnd.text.toString()
        tripRequest["notes"] = binding.etTripDescription.text.toString()
        tripRequest["status"] = "pending"
        tripRequest["start_latitude"] = "40.712776"
        tripRequest["start_longitude"] = "-74.005974"
        tripRequest["from_address"] = binding.etStartLocation.text.toString()
        tripRequest["end_latitude"] = "34.052235"
        tripRequest["end_longitude"] = "-118.243683"
        tripRequest["to_address"] = binding.etDestinationLocation.text.toString()
        tripRequest["driver_id"] = mSelectedDriver ?: ""
        tripRequest["vehicle_id"] = mSelectedVehicle ?: ""

        mTripViewModel.createTrip(this, tripRequest)
    }

}
