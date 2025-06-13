package com.stafo.app.screens.tripPlan

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTripExpensesBinding
import com.stafo.app.screens.tripPlan.adapters.ExpensesListAdapter
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.formatAmount
import java.io.File

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

        if (intent.getIntExtra("isEnd", 0) == 4)
            binding.btnAddExp.visibility = View.GONE
        else
            binding.btnAddExp.visibility = View.VISIBLE


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
            onAssignSuccess = {
                mTripViewModel.fetchTripExpenses(this, tripId)
            },
            onCameraRequest = {
                openPicker(1101)
            })
        tripExpensesBottomSheet.show()
    }

    private fun openPicker(req: Int) {
        ImagePicker.with(this).crop().cameraOnly().compress(1024).maxResultSize(
            1080, 1080
        ).start(req)
    }

    private fun getFileFromUri(uri: Uri): File? {
        val fileName = getFileName(uri) ?: return null
        val file = File(cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null

        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex)
                    }
                }
            }
        }

        if (name.isNullOrEmpty()) {
            name = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) {
                    path.substring(cut + 1)
                } else {
                    path
                }
            }
        }

        return name ?: "unknown_file"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri: Uri = data.data!!

            val file = getFileFromUri(uri)

            if (file != null) {
                when (requestCode) {
                    1101 -> {
                        tripExpensesBottomSheet.setCapturedImagePath(file.path)
                    }
                }
            } else {
                CustomToast(this, "File selection failed")

            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            CustomToast(this, ImagePicker.getError(data))

        } else {
            // CustomToast(this, "Task Cancelled")

        }
    }
}