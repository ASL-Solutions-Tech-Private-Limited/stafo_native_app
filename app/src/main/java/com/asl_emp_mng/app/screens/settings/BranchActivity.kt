package com.asl_emp_mng.app.screens.settings

import android.content.Context
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
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.BranchAdapter
import com.asl_emp_mng.app.databinding.ActivityBranchBinding
import com.asl_emp_mng.app.screens.settings.dataClass.BranchItem
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveData
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.getEmployeeComId

class BranchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBranchBinding



    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: BranchAdapter

    private var branchList: List<BranchItem> = listOf()
    private var filteredList: List<BranchItem> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)
        setOnClickEvents()
        observeViewModel()
        setupSearchListener()
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
            branchList
        } else {
            branchList.filter {
                it.branch_name.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mViewBranchResponse.observe(this) {

            if (it.data.isNotEmpty()){
                branchList=it.data
                filteredList=branchList

                binding.etDirSearch.isFocusable = true
                binding.etDirSearch.isFocusableInTouchMode = true

                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvShowBranchList.setLayoutManager(layoutManager)
                rvAdapter = BranchAdapter(it.data, this)
                binding.rvShowBranchList.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()
            } else {
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
            }



        }


    }

    override fun onResume() {
        super.onResume()
        getEmployeeComId()?.let { settingsViewModel.getViewBranchList(this@BranchActivity, it) }
    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun setOnClickEvents() {
        getEmployeeComId()?.let { settingsViewModel.getViewBranchList(this@BranchActivity, it) }

        binding.imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            getEmployeeComId()?.let { settingsViewModel.getViewBranchList(this@BranchActivity, it) }

        }

        binding.llcAddBranch.setOnClickListener {
            startActivity(Intent(this, AddBranchActivity::class.java))
        }

    }
}