package com.stafo.app.screens.payroll

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.BranchAdapter
import com.stafo.app.databinding.ActivitySalaryTypeBinding
import com.stafo.app.screens.settings.AddBranchActivity
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.BranchItem
import com.stafo.app.screens.settings.dataClass.SalaryType
import com.stafo.app.screens.settings.dataClass.SalaryTypeDeleteRequest
import com.stafo.app.screens.settings.dataClass.SalaryTypeListRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class SalaryTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalaryTypeBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()


    private lateinit var rvAdapter: SalaryTypeListAdapter

    private var dataList: List<SalaryType> = listOf()

    private var filteredList: List<SalaryType> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySalaryTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        setOnClickEvents()
        observeViewModel()
        setupSearchListener()
    }


    override fun onResume() {
        super.onResume()

        getEmployeeComId()?.let {
            val request = SalaryTypeListRequest(
                company_id = it.toInt()
            )
            settingsViewModel.salaryTypeList(this@SalaryTypeActivity, request)
        }
    }

    private fun setOnClickEvents() {
        getEmployeeComId()?.let {
            val request = SalaryTypeListRequest(
                company_id = it.toInt()
            )
            settingsViewModel.salaryTypeList(this@SalaryTypeActivity, request)
        }

        binding.imageBack.setOnClickListener {
            onBackPressed()

        }


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            getEmployeeComId()?.let {
                val request = SalaryTypeListRequest(
                    company_id = it.toInt()
                )
                settingsViewModel.salaryTypeList(this@SalaryTypeActivity, request)
            }

        }

        binding.llcAddSalaryType.setOnClickListener {
            startActivity(Intent(this, CreateSalaryTypeActivity::class.java))
        }

    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mSalaryTypeListResponse.observe(this) {

            if (it.success){
                if (it.data.isNotEmpty()) {
                    binding.txtMsg.visibility = View.GONE
                    binding.rvShowBranchList.visibility = View.VISIBLE
                    dataList = it.data
                    filteredList = dataList

                    binding.etDirSearch.isFocusable = true
                    binding.etDirSearch.isFocusableInTouchMode = true

                    val layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvShowBranchList.setLayoutManager(layoutManager)
                    rvAdapter = SalaryTypeListAdapter(dataList, this)
                    binding.rvShowBranchList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()
                } else {
                    binding.rvShowBranchList.visibility = View.GONE
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE
                }
            }else {
                binding.rvShowBranchList.visibility = View.GONE
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
            }




        }


        settingsViewModel.mSalaryTypeDeleteResponse.observe(this) {

            if (it.success){
                getEmployeeComId()?.let {
                    val request = SalaryTypeListRequest(
                        company_id = it.toInt()
                    )
                    settingsViewModel.salaryTypeList(this@SalaryTypeActivity, request)
                }
                CustomToast(this,it.message)
            } else {
                CustomToast(this,it.message)
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


    private fun setupSearchListener() {
        binding.etDirSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            dataList
        } else {
            dataList.filter {
                it.salary_type.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }

    fun deleteSalaryType(id:Int){

        getEmployeeComId()?.let {
            val request = SalaryTypeDeleteRequest(
                salary_type_id=id,
                company_id = it.toInt()
            )
            settingsViewModel.deleteSalaryType(this@SalaryTypeActivity, request)
        }



    }
}