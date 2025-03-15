package com.stafo.app.screens.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.stafo.app.base.adapter.AdapterHoliday
import com.stafo.app.databinding.ActivityHolidayBinding
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        onClickListener()
        observeViewModel()


    }

    override fun onResume() {
        super.onResume()
        getEmployeeComId()?.let {
            settingsViewModel.getHolidayList(this@HolidayActivity,
                it
            )
        }
    }

    private fun onClickListener() {
        binding?.apply {
            getEmployeeComId()?.let { settingsViewModel.getHolidayList(this@HolidayActivity, it) }

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                getEmployeeComId()?.let {
                    settingsViewModel.getHolidayList(this@HolidayActivity, it)
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

            if (it.status) {

                if (it.data.isNotEmpty()){
                    binding.txtMsg.visibility = View.GONE
                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.rvHolidayList.setLayoutManager(layoutManager)
                    rvAdapter = AdapterHoliday(it.data, this@HolidayActivity)
                    binding.rvHolidayList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()
                }else {
                    binding.txtMsg.visibility=View.VISIBLE
                }


            } else {
               binding.txtMsg.visibility=View.VISIBLE
            }
        }

        settingsViewModel.mDeleteResponse.observe(this) {

            if (it.status){
                getEmployeeComId()?.let {
                    settingsViewModel.getHolidayList(this@HolidayActivity, it)
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
    fun deleteHoliday(id:Int){

        settingsViewModel.companyDeleteHoliday(this@HolidayActivity, id)

    }




}