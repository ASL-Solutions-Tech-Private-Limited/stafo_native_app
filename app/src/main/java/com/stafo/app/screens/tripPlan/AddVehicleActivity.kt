package com.stafo.app.screens.tripPlan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.stafo.app.databinding.ActivityAddVehicleBinding
import com.stafo.app.utils.CustomLoader
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddVehicleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddVehicleBinding
    private val RC_FILE_REQUEST_CODE = 101
    private var rcFileUri: Uri? = null

    private val vehicleTypes = arrayListOf(
        SearchListItem(1, "Truck"), SearchListItem(2, "Tempo"),
        SearchListItem(3, "Van"), SearchListItem(4, "Bike"),
        SearchListItem(5, "Car"), SearchListItem(6, "Tractor"),
        SearchListItem(7, "Trailer")
    )

    private val fuelTypes = arrayListOf(
        SearchListItem(1, "Petrol"), SearchListItem(2, "Diesel"),
        SearchListItem(3, "CNG"), SearchListItem(4, "Electric"),
        SearchListItem(5, "Hybrid")
    )

    private val mTripViewModel: TripViewModel by lazy {
        TripViewModel()
    }
    private val mCustomLoader: CustomLoader by lazy {
        CustomLoader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            ivBack.setOnClickListener { finish() }

            etVehicleType.setOnClickListener {
                showSearchDialog(vehicleTypes, "Vehicle Type") {
                    etVehicleType.setText(it.title)
                }
            }

            etFuelType.setOnClickListener {
                showSearchDialog(fuelTypes, "Fuel Type") {
                    etFuelType.setText(it.title)
                }
            }

            etVehicleRC.setOnClickListener {
                openFileChooser()
            }

            btnAddVehicle.setOnClickListener {
                if (isValidInput()) {
                    uploadVehicleData()
                }
            }
        }

        observeViewModel()
    }

    private fun showSearchDialog(
        list: List<SearchListItem>,
        title: String,
        onSelected: (SearchListItem) -> Unit
    ) {
        val dialog = SearchableDialog(this, list as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, item: SearchListItem) {
                onSelected(item)
                dialog.dismiss()
            }
        })
        dialog.show()
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(intent, "Select RC Image"),
            RC_FILE_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            rcFileUri = data?.data
            rcFileUri?.let {
                binding.etVehicleRC.setText(FileUtils.getFileName(this, it))
            }
        }
    }

    private fun isValidInput(): Boolean = binding.run {
        when {
            etVehicleNumber.text.isNullOrEmpty() -> {
                etVehicleNumber.error = "Enter vehicle number"; false
            }

            etVehicleType.text.isNullOrEmpty() -> {
                etVehicleType.error = "Select vehicle type"; false
            }

            etFuelType.text.isNullOrEmpty() -> {
                etFuelType.error = "Select fuel type"; false
            }

            rcFileUri == null -> {
                etVehicleRC.error = "Upload RC image"; false
            }

            etRCNumber.text.isNullOrEmpty() -> {
                etRCNumber.error = "Enter RC number"; false
            }

            etSpeedometer.text.isNullOrEmpty() -> {
                etSpeedometer.error = "Enter speedometer"; false
            }

            etLoadCapacity.text.isNullOrEmpty() -> {
                etLoadCapacity.error = "Enter load capacity"; false
            }

            etTotalKm.text.isNullOrEmpty() -> {
                etTotalKm.error = "Enter total KM"; false
            }

            else -> true
        }
    }


    private fun observeViewModel() {
        mTripViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") mCustomLoader.show() else mCustomLoader.dismiss()
        }

        mTripViewModel.mAddVehicleResponse.observe(this) {
            if (it.status == true) {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadVehicleData() {
        val vehicleNumber = binding.etVehicleNumber.text.toString()
        val vehicleType = binding.etVehicleType.text.toString()
        val fuelType = binding.etFuelType.text.toString()
        val rcNumber = binding.etRCNumber.text.toString()
        val speedometer = binding.etSpeedometer.text.toString()
        val loadCapacity = binding.etLoadCapacity.text.toString()
        val totalKm = binding.etTotalKm.text.toString()

        val filePart = rcFileUri?.let {
            val file = FileUtils.getFile(this, it)
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("rc_upload_path", file.name, requestBody)
        }

        val map = hashMapOf(
            "vehicle_no" to vehicleNumber.toRequestBody(),
            "vehicle_type" to vehicleType.toRequestBody(),
            "fuel" to fuelType.toRequestBody(),
            "rc_number" to rcNumber.toRequestBody(),
            "speedometer" to speedometer.toRequestBody(),
            "load_capacity" to loadCapacity.toRequestBody(),
            "km_travelled" to totalKm.toRequestBody(),
            "status" to "active".toRequestBody()
        )

        mTripViewModel.createVehicle(this, map, filePart)

    }

    private fun String.toPlainText(): RequestBody =
        this.toRequestBody("text/plain".toMediaTypeOrNull())

    object FileUtils {
        fun getFile(context: Context, uri: Uri): File {
            val inputStream = context.contentResolver.openInputStream(uri)!!
            val file = File(context.cacheDir, getFileName(context, uri))
            file.outputStream().use { inputStream.copyTo(it) }
            return file
        }

        fun getFileName(context: Context, uri: Uri): String {
            var name = ""
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst()) name = it.getString(nameIndex)
            }
            return name
        }
    }
}
