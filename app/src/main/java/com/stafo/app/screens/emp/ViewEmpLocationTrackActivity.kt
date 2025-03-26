package com.stafo.app.screens.emp

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
import com.stafo.app.base.adapter.ViewEmpListAdapter
import com.stafo.app.databinding.ActivityViewEmpLocationTrackBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.CustomLoader

class ViewEmpLocationTrackActivity : AppCompatActivity() {
    private lateinit var binding:ActivityViewEmpLocationTrackBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: ViewEmpListAdapter

    private var empList: List<GetEmployee> = listOf()
    private var filteredList: List<GetEmployee> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityViewEmpLocationTrackBinding.inflate(layoutInflater)
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


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) {

            if (it.status) {


                if (it.data.isNotEmpty()){

                    binding.etDirSearch.isFocusable = true
                    binding.etDirSearch.isFocusableInTouchMode = true

                    empList=it.data
                    filteredList=empList

                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,false)
                    binding.rvViewEmpList.setLayoutManager(layoutManager)
                    rvAdapter = ViewEmpListAdapter(empList, this)
                    binding.rvViewEmpList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()


                }else{
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE
                }




            } else {
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
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
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        finish()
    }
    private fun setOnClickEvents() {



        settingsViewModel.getAllEmployeeList(this@ViewEmpLocationTrackActivity)


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            settingsViewModel.getAllEmployeeList(this@ViewEmpLocationTrackActivity)

        }

        binding.imageBack.setOnClickListener {
            onBackPressed()
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
            empList
        } else {
            empList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true) ||
                        it.branch_name.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }
}