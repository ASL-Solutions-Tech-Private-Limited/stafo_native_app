package com.asl_emp_mng.app.screens.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.ActionsListAdapter
import com.asl_emp_mng.app.base.adapter.EmpListAdapter
import com.asl_emp_mng.app.base.adapter.SliderAdapter
import com.asl_emp_mng.app.base.model.ActionModel
import com.asl_emp_mng.app.base.model.DashboardType
import com.asl_emp_mng.app.databinding.ActivityEmployerDashboardBinding
import com.asl_emp_mng.app.screens.settings.AddBranchActivity
import com.asl_emp_mng.app.screens.settings.AddEmployeeActivity
import com.asl_emp_mng.app.screens.settings.LeaveManagementActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.ui.EmplyeeyerProfile
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.getGreetingBasedOnTime
import com.asl_emp_mng.app.utils.getTodayDate

class EmployerDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerDashboardBinding
    private lateinit var name: String

    private var token: String? = null
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var rvAdapter: EmpListAdapter
    private val mActionList = ArrayList<ActionModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setOnClickEvents()
        observeViewModel()
        setupImageSlider()
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
            llNoWishes.visibility = View.VISIBLE
            llLeaves.visibility = View.VISIBLE

         /*   rvLeaves.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
            val emplyeeListWishAdapter = EmplyeeListAdapter(this@EmployerDashboard)
            rvLeaves.adapter = emplyeeListWishAdapter*/

            rvActions.layoutManager =
                LinearLayoutManager(this@EmployerDashboard, LinearLayoutManager.HORIZONTAL, false)
            val actionsAdapter = ActionsListAdapter(actionList(),
                this@EmployerDashboard,
                object : ActionsListAdapter.ActionClickListener {
                    override fun onActionClick(action: String) {
                        when (action) {
                            "Employee" -> {}
                            "Leaves" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        LeaveManagementActivity::class.java
                                    )
                                )
                            }

                            "Branchs" -> {
                                startActivity(
                                    Intent(
                                        this@EmployerDashboard,
                                        AddBranchActivity::class.java
                                    )
                                )
                            }

                            "Policy" -> {}
                        }
                    }

                })
            rvActions.adapter = actionsAdapter

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

        /*settingsViewModel.mEmployeeListResponse.observe(this) {

            if (it.status) {
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                binding.rvLeaves.setLayoutManager(layoutManager)
                rvAdapter = EmpListAdapter(it.data, this)
                binding.rvLeaves.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()

            } else {
                CustomToast(this, it.message)
            }
        }*/


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

    private fun actionList(): List<ActionModel> {
        mActionList.add(ActionModel("Employee", R.drawable.ic_user))
        mActionList.add(ActionModel("Leaves", R.drawable.ic_leaves))
        mActionList.add(ActionModel("Branchs", R.drawable.ic_calendar_month))
        mActionList.add(ActionModel("Policy", R.drawable.ic_policy))
        return mActionList
    }

    private fun setupImageSlider() {
        var imageList = ArrayList<Int>()
        imageList.add(R.drawable.banner_one)
        imageList.add(R.drawable.banner_two)

        binding.imageSlider.setSliderAdapter(SliderAdapter(this, imageList))
    }
}