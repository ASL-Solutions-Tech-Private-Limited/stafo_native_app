package com.stafo.app.screens.billpayment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.EmpListAdapter
import com.stafo.app.databinding.ActivityElectricityBillerBinding
import com.stafo.app.screens.billpayment.Adapter.AdapterElectricityItemList
import com.stafo.app.screens.billpayment.dataClass.ElectricityOperator
import com.stafo.app.screens.billpayment.dataClass.ElectricityOperatorRequest
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class ElectricityBillerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityElectricityBillerBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()
    private lateinit var rvAdapter: AdapterElectricityItemList

    private var operatorList: List<ElectricityOperator> = listOf()
    private var filteredList: List<ElectricityOperator> = listOf()

    private var mCategory = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityElectricityBillerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        mCategory = intent.getStringExtra("type").toString()

        setOnClickEvents()
        observeViewModel()
        setupSearchListener()


    }

    private fun setOnClickEvents() {


        if (mCategory == "DTH") {
            binding.edtSearch.hint = "Search by operator"

        } else {
            binding.edtSearch.hint = "Search by biller"
        }


        val request = ElectricityOperatorRequest(
            category = mCategory
        )
        billPaymentsViewModel.getElectricityOperator(this@ElectricityBillerActivity, request)

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun observeViewModel() {
        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mElectricityOperatorResponse.observe(this) {

            if (it.success) {
                if (!it.data.isNullOrEmpty()) {

                    binding.edtSearch.isFocusable = true
                    binding.edtSearch.isFocusableInTouchMode = true

                    operatorList = it.data
                    filteredList = operatorList

                    val layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvListOperator.setLayoutManager(layoutManager)
                    rvAdapter = AdapterElectricityItemList(
                        operatorList,
                        this,
                        object : AdapterElectricityItemList.onOperatorClick {
                            override fun onElOperatorClick(operatorCode: String) {
                                Log.d("res", "get code $operatorCode")

                                startActivity(
                                    Intent(
                                        this@ElectricityBillerActivity,
                                        OperatorDetailsActivity::class.java
                                    ).apply {
                                        putExtra("operatorCode", operatorCode)
                                    })
                            }

                        })
                    binding.rvListOperator.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()
                } else {
                    binding.edtSearch.isFocusable = true
                    binding.edtSearch.isFocusableInTouchMode = true
                }

            } else CustomToast(this, it.message)


        }


    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun setupSearchListener() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            operatorList
        } else {
            operatorList.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }
}