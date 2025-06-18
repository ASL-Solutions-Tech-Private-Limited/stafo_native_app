package com.stafo.app.screens.tripPlan

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCreateTripBinding
import com.stafo.app.screens.tripPlan.dataClass.dashboard.Trips
import com.stafo.app.screens.ui.PlaceSearchActivity
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTripBinding
    private val mTripViewModel: TripViewModel by lazy { TripViewModel() }
    private val mCustomLoader: CustomLoader by lazy { CustomLoader(this) }
    private var mSelectedVehicle: String? = ""
    private var mSelectedDriver: String? = ""
    private val PLACE_SEARCH_REQUEST_CODE = 101
    private var mLocationType = "Source"
    private var mSourceLat = 0.0
    private var mSourceLong = 0.0
    private var mDestinationLat = 0.0
    private var mDestinationLong = 0.0
    private var mSourceAddress = ""
    private var mDestinationAddress = ""

    private var isEdit = false
    private var mTripID = ""
    private var mTrip: Trips? = null
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

        isEdit = intent.getBooleanExtra("isEdit", false)

        if (isEdit) {
            mTripID = intent.getStringExtra("tripId") ?: ""
            val tripData = intent.getStringExtra("tripData") ?: ""
            mTrip = Gson().fromJson(tripData, Trips::class.java)
            setupEditData(mTrip!!)
            binding.btnCreateTrip.text = "Update Trip"
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
            mLocationType = "Source"
            val intent = Intent(this@CreateTripActivity, PlaceSearchActivity::class.java)
            startActivityForResult(intent, PLACE_SEARCH_REQUEST_CODE)
        }

        binding.etDestinationLocation.setOnClickListener {
            mLocationType = "Destination"
            val intent = Intent(this@CreateTripActivity, PlaceSearchActivity::class.java)
            startActivityForResult(intent, PLACE_SEARCH_REQUEST_CODE)
        }

        binding.btnCreateTrip.setOnClickListener {
            if (validateFields()) {
                if (isEdit)
                    updateTrip()
                else createTrip()
            }
        }

        if (getIsCOMPANYLogin(this)) {
            binding.tilDriver.visibility = TextInputEditText.VISIBLE
            mTripViewModel.getVehicleList(this)
            mTripViewModel.getDriverList(this)
        } else {
            mTripViewModel.getVehicleList(this)
            binding.tilDriver.visibility = TextInputEditText.GONE
            mSelectedDriver = "${getEmployeeDetails()?.id ?: ""}"
        }
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

                            if (isEdit) {
                                if (employee.id == mTrip?.vehicleId) {
                                    mSelectedVehicle = employee.id.toString()
                                    binding.etVehicle.setText(employee.vehicleNo)
                                }
                            }

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
                            if (isEdit) {
                                if (employee.id == mTrip?.driverId) {
                                    mSelectedDriver = employee.id.toString()
                                    binding.etDriver.setText(employee.name)
                                }
                            }
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

        mTripViewModel.mDriverAvailableResponse.observe(this) { response ->
            if (response.status == true && response.available == false) {
                binding.tilDriver.error =
                    "Selected Driver is not available.Please select another driver"
            } else {
                binding.tilDriver.error = null
            }
        }

        mTripViewModel.mVehicleAvailableResponse.observe(this) { response ->
            if (response.status == true && response.available == false) {
                binding.tilVehicle.error =
                    "Selected Vehicle is not available.Please select another vehicle"
            } else {
                binding.tilVehicle.error = null
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

    private fun validateFields(): Boolean {
        val requiredFields =
            if (getIsCOMPANYLogin(this)) listOf(
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
            else listOf(
                binding.etTripName,
                binding.etTripDescription,
                binding.etTripClientName,
                binding.etTripClientNumber,
                binding.etStartLocation,
                binding.etDestinationLocation,
                binding.etJourneyStart,
                binding.etEstimatedEnd,
              //  binding.etDriver,
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


    private fun setupEditData(trip: Trips) {
        binding.apply {
            etTripName.setText(trip.title)
            etTripDescription.setText(trip.notes)
            etTripClientName.setText(trip.customerInfo?.customerName)
            etTripClientNumber.setText(trip.customerInfo?.phone)
            etStartLocation.setText(trip.fromAddress)
            etDestinationLocation.setText(trip.toAddress)
            etJourneyStart.setText(trip.startTime)
            etEstimatedEnd.setText(trip.endTime)
        }
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
        tripRequest["start_latitude"] = "$mSourceLat"
        tripRequest["start_longitude"] = "$mSourceLong"
        tripRequest["from_address"] = binding.etStartLocation.text.toString()
        tripRequest["end_latitude"] = "$mDestinationLat"
        tripRequest["end_longitude"] = "$mDestinationLong"
        tripRequest["to_address"] = binding.etDestinationLocation.text.toString()
        tripRequest["driver_id"] = mSelectedDriver ?: ""
        tripRequest["vehicle_id"] = mSelectedVehicle ?: ""

        mTripViewModel.createTrip(this, tripRequest)
    }

    private fun updateTrip() {
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
        tripRequest["start_latitude"] = "$mSourceLat"
        tripRequest["start_longitude"] = "$mSourceLong"
        tripRequest["from_address"] = binding.etStartLocation.text.toString()
        tripRequest["end_latitude"] = "$mDestinationLat"
        tripRequest["end_longitude"] = "$mDestinationLong"
        tripRequest["to_address"] = binding.etDestinationLocation.text.toString()
        tripRequest["driver_id"] = mSelectedDriver ?: ""
        tripRequest["vehicle_id"] = mSelectedVehicle ?: ""

        mTripViewModel.updateTrip(this, tripRequest, mTripID)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            when (data?.getStringExtra("type")) {
                "map" -> {
                    val lat = data.getDoubleExtra("latitude", 0.0)
                    val lng = data.getDoubleExtra("longitude", 0.0)
                    val fullAddress = data.getStringExtra("fullAddress")

                    if (mLocationType == "Source") {
                        binding.etStartLocation.setText(fullAddress)
                        mSourceLat = lat
                        mSourceLong = lng
                        mSourceAddress = fullAddress ?: ""
                    } else {
                        binding.etDestinationLocation.setText(fullAddress)
                        mDestinationLat = lat
                        mDestinationLong = lng
                        mDestinationAddress = fullAddress ?: ""
                    }
                    Log.d("MapTap", "main Location: Lat=${lat}, Lng=${lng},Address=${fullAddress}")
                }
            }
        }
    }

}
