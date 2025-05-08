package com.stafo.app.screens.billpayment

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.stafo.app.R
import com.stafo.app.databinding.ActivityOperatorDetailsBinding
import com.stafo.app.screens.billpayment.Adapter.DynamicOperatorAdapter
import com.stafo.app.screens.billpayment.dataClass.BbpsOperatorDetailsResponse
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class OperatorDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityOperatorDetailsBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()
    private lateinit var rvAdapter: DynamicOperatorAdapter

    private var mOperatorCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityOperatorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        mOperatorCode = intent.getStringExtra("operatorCode").toString()

        setOnClickEvents()
        observeViewModel()

    }

    private fun setOnClickEvents() {

        billPaymentsViewModel.getOperatorDetails(this@OperatorDetailsActivity, mOperatorCode)

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun observeViewModel() {
        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mOperatorDetailsResponse.observe(this) { response ->

            if (response.status == 1) {

                val rawParams = response.mdmRequestNew.biller.billerInputParams
                val normalizedParams = rawParams.toNormalized() // ✅ Converts to List

                if (normalizedParams.isNotEmpty()) {
                    val layoutManager = LinearLayoutManager(this)
                    binding.rvOperatorFieldsList.layoutManager = layoutManager
                    rvAdapter = DynamicOperatorAdapter(normalizedParams)
                    binding.rvOperatorFieldsList.adapter = rvAdapter
                } else {
                    Toast.makeText(this, "No input fields found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }




    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }



}