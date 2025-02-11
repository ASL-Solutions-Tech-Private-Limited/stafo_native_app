package com.asl_emp_mng.app.screens.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.base.adapter.EmpListAdapter
import com.asl_emp_mng.app.base.adapter.ShiftAdapter
import com.asl_emp_mng.app.base.model.DashboardType
import com.asl_emp_mng.app.base.model.ProfileType
import com.asl_emp_mng.app.databinding.ActivityEmployerDashboardBinding
import com.asl_emp_mng.app.screens.EmpProfileActivity
import com.asl_emp_mng.app.screens.settings.AddEmployeeActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.ui.EmplyeeListAdapter
import com.asl_emp_mng.app.screens.ui.EmplyeeyerProfile
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getGreetingBasedOnTime
import com.asl_emp_mng.app.utils.getTodayDate

class EmployerDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerDashboardBinding
    private lateinit var name: String

    private var token: String? = null
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var rvAdapter: EmpListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setOnClickEvents()
        observeViewModel()

    }

    private fun initViews() {
        binding.apply {

            token = getToken(this@EmployerDashboard, "token")

            name=intent.extras?.getString("name") ?: ""
            tvHeaderGreeting.text = getGreetingBasedOnTime()
            tvHeaderEmpName.text = name

            tvLetsCheck.text = "Today's Report (${getTodayDate()})"
            rvWishes.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
            val emplyeeListAdapter = EmplyeeListAdapter(this@EmployerDashboard)
            rvWishes.adapter = emplyeeListAdapter

         /*   rvLeaves.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
            val emplyeeListWishAdapter = EmplyeeListAdapter(this@EmployerDashboard)
            rvLeaves.adapter = emplyeeListWishAdapter*/

            token?.let {
                settingsViewModel.getEmpList(this@EmployerDashboard, it)

            }

        }


    }



    private fun getToken(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mEmployeeListResponse.observe(this) {

            if (it.status) {
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                binding.rvLeaves.setLayoutManager(layoutManager)
                rvAdapter = EmpListAdapter(it.data, this)
                binding.rvLeaves.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()

            } else {
                CustomToast(this, it.message)
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

    private fun setOnClickEvents() {



        binding.tvHeaderSetting.setOnClickListener {
            val intent = Intent(this@EmployerDashboard, EmplyeeyerProfile::class.java)
            intent.putExtra("DASHBOARD_TYPE", DashboardType.COMPANY.name)
            startActivity(intent)
        }

        binding.addEmp.setOnClickListener {
            startActivity(Intent(this, AddEmployeeActivity::class.java))
        }

        binding.tvLetsCheckViewAll.setOnClickListener {
           // startActivity(Intent(this, AddShiftActivity::class.java))
        }

        binding.tvHeaderEmpName.text=name
    }
}