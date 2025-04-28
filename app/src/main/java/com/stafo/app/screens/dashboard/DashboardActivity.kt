package com.stafo.app.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ActivityDashboardBinding
import com.stafo.app.screens.billpayment.Adapter.AdapterElectricityItemList
import com.stafo.app.screens.billpayment.Adapter.AdapterMenuList
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.billpayment.ElectricityBillerActivity
import com.stafo.app.screens.recharge.RechargeActivity
import com.stafo.app.screens.recharge.adapter.RecentRechargeAdapter
import com.stafo.app.screens.settings.UploadSelfieAttendanceActivity
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var rvAdapter: AdapterMenuList

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
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

    private fun onClickListener() {
        binding.apply {


            imgBackBtn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            billPaymentsViewModel.getMenuList(this@DashboardActivity)

            binding.imgBackBtn.setOnClickListener {
                onBackPressed()
            }

            /* recharge.setOnClickListener {
                 startActivity(Intent(this@DashboardActivity, RechargeActivity::class.java))
                 overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
             }
             llcElectricity.setOnClickListener {
                 startActivity(Intent(this@DashboardActivity, ElectricityBillerActivity::class.java))
                 overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
             }*/


        }
    }

    private fun observeViewModel() {
        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mCategoryMenuResponse.observe(this) {

            if (it.success) {
                if (!it.data.isNullOrEmpty()) {

                    val requiredNames = listOf(
                        "Mobile Postpaid",
                        "DTH",
                        "Fastag",
                        "Electricity",
                        "Insurance",
                        "Broadband Postpaid"
                    )

                    val filteredList = it.data
                        .filter { category -> category.name in requiredNames }
                        .distinctBy { category -> category.name }

                    val layoutManager = GridLayoutManager(this, 3)
                    binding.rvMenuList.layoutManager = layoutManager
                    rvAdapter = AdapterMenuList(
                        filteredList,
                        this,
                        object : AdapterMenuList.onItemClick {
                            override fun onItem(name: String) {

                                when (name) {
                                    "Electricity" -> {
                                        startActivity(
                                            Intent(
                                                this@DashboardActivity,
                                                ElectricityBillerActivity::class.java
                                            ).apply {
                                                putExtra("type", name)
                                            })
                                    }

                                    "DTH" -> {
                                        startActivity(
                                            Intent(
                                                this@DashboardActivity,
                                                ElectricityBillerActivity::class.java
                                            ).apply {
                                                putExtra("type", name)
                                            })
                                    }

                                    "Mobile Postpaid" -> {
                                        startActivity(
                                            Intent(
                                                this@DashboardActivity,
                                                RechargeActivity::class.java
                                            ).apply {
                                                putExtra("type", name)
                                            })
                                    }

                                    else -> {
                                        CustomToast(this@DashboardActivity, name)
                                    }
                                }
                            }
                        })
                    binding.rvMenuList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()
                }


            } else CustomToast(this, "No action available here")


        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    /*private fun shineAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.left_right)
        binding.shine.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                binding.shine.startAnimation(anim)
            }

            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationRepeat(p0: Animation?) {}

        })
    }*/
}