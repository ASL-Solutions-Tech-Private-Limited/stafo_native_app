package com.asl_emp_mng.app.screens.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.asl_emp_mng.app.base.adapter.AdapterHoliday
import com.asl_emp_mng.app.base.adapter.AdapterRequestLeaveHistory
import com.asl_emp_mng.app.base.adapter.ShiftAdapter
import com.asl_emp_mng.app.databinding.ActivityHolidayBinding
import com.asl_emp_mng.app.databinding.ActivityLeaveRequestHistoryBinding
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getEmployeeComId

class HolidayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHolidayBinding
    private lateinit var rvAdapter: AdapterHoliday

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHolidayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark)
        onClickListener()
        observeViewModel()


    }

    private fun onClickListener() {
        binding?.apply {
            getEmployeeComId()?.let { settingsViewModel.getHolidayList(this@HolidayActivity, it) }

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                getEmployeeComId()?.let {
                    settingsViewModel.getHolidayList(this@HolidayActivity,
                        it
                    )
                }

            }

            imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            binding.llcAddHoliday.setOnClickListener {

                startActivity(Intent(this@HolidayActivity, AddHolidayActivity::class.java))
            }


        }
    }

    private fun getToken(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mHolidayListResponse.observe(this) {

            Log.d("res",it.data.toString())

            if (it.status) {
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                binding.rvHolidayList.setLayoutManager(layoutManager)
                rvAdapter = AdapterHoliday(it.data, this@HolidayActivity)
                binding.rvHolidayList.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()

            } else {
               binding.txtMsg.visibility=View.GONE
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